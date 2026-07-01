package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val isEvaluating: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(text: String, financialSummary: String = "") {
        val userMsg = ChatMessage(isUser = true, text = text)
        val assistantMsgId = java.util.UUID.randomUUID().toString()
        val assistantMsg = ChatMessage(id = assistantMsgId, isUser = false, text = "", isEvaluating = true)
        
        _messages.value = _messages.value + userMsg + assistantMsg

        viewModelScope.launch {
            try {
                // Build history
                val history = _messages.value.dropLast(1).map {
                    Content(
                        role = if (it.isUser) "user" else "model",
                        parts = listOf(Part(text = it.text))
                    )
                }

                val systemInstruction = Content(
                    parts = listOf(Part(text = "Eres una asistente financiera y de negocios experta para emprendedoras. Eres amigable, motivadora y experta en márgenes de ganancia. Responde de forma concisa y estética. Aquí está el resumen actual de las finanzas de la emprendedora:\n$financialSummary\nUsa esta información para darle insights personalizados sobre cómo van sus ganancias."))
                )

                val request = GenerateContentRequest(
                    contents = history,
                    systemInstruction = systemInstruction
                )

                var assistantText = ""
                withContext(Dispatchers.IO) {
                    val apiKey = BuildConfig.GEMINI_API_KEY
                    val response = RetrofitClient.service.generateContentStream(apiKey, request)
                    
                    response.byteStream().bufferedReader().use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if (line!!.startsWith("data: ")) {
                                val jsonLine = line!!.substring(6)
                                try {
                                    val chunk = Json.parseToJsonElement(jsonLine).jsonObject
                                    val chunkText = chunk["candidates"]?.jsonArray
                                        ?.getOrNull(0)?.jsonObject
                                        ?.get("content")?.jsonObject
                                        ?.get("parts")?.jsonArray
                                        ?.getOrNull(0)?.jsonObject
                                        ?.get("text")?.jsonPrimitive?.content
                                    
                                    if (chunkText != null) {
                                        assistantText += chunkText
                                        withContext(Dispatchers.Main) {
                                            _messages.value = _messages.value.map {
                                                if (it.id == assistantMsgId) it.copy(text = assistantText) else it
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("ChatViewModel", "Error parsing streaming chunk", e)
                                }
                            }
                        }
                    }
                }
                
                _messages.value = _messages.value.map {
                    if (it.id == assistantMsgId) it.copy(isEvaluating = false) else it
                }
                
            } catch (e: Exception) {
                _messages.value = _messages.value.map {
                    if (it.id == assistantMsgId) it.copy(text = "Error de conexión: ${e.message}", isEvaluating = false) else it
                }
            }
        }
    }
}
