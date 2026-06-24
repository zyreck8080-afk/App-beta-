package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.ProductRepository
import com.example.ui.LuminaApp
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.theme.MyApplicationTheme

import com.example.data.SettingsRepository
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ProductRepository(database.productDao(), database.saleDao()) }
    val settingsRepository by lazy { SettingsRepository(applicationContext) }
    val viewModel: MainViewModel by viewModels { MainViewModelFactory(repository, settingsRepository) }

    setContent {
      val primaryColorHex by viewModel.primaryColor.collectAsStateWithLifecycle()
      val typographyStyle by viewModel.typographyStyle.collectAsStateWithLifecycle()
      val isOledMode by viewModel.isOledMode.collectAsStateWithLifecycle()

      MyApplicationTheme(
          primaryColorHex = primaryColorHex,
          typographyStyle = typographyStyle,
          isOledMode = isOledMode
      ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LuminaApp(viewModel = viewModel)
        }
      }
    }
  }
}
