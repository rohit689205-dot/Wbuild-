package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.WBuildsRepository
import com.example.ui.WBuildsViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminBuildEditorScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminManageCategoriesScreen
import com.example.ui.screens.AdminManageStepsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BuildCompletedScreen
import com.example.ui.screens.BuildDetailsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StepViewerScreen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = WBuildsRepository(applicationContext)

    setContent {
      WBuildsTheme {
        val viewModel: WBuildsViewModel = viewModel {
          WBuildsViewModel(repository)
        }
        WBuildsApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun WBuildsApp(viewModel: WBuildsViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(userMessage) {
    userMessage?.let {
      snackbarHostState.showSnackbar(
        message = it,
        duration = SnackbarDuration.Short
      )
      viewModel.clearMessage()
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize(),
    containerColor = WBuildsBlack
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(WBuildsBlack)
        .padding(innerPadding)
    ) {
      when (val screen = currentScreen) {
        is Screen.Splash -> SplashScreen(viewModel)
        is Screen.Auth -> AuthScreen(viewModel)
        is Screen.Home -> HomeScreen(viewModel)
        is Screen.Search -> SearchScreen(viewModel)
        is Screen.BuildDetails -> BuildDetailsScreen(viewModel, screen.buildId)
        is Screen.StepViewer -> StepViewerScreen(viewModel, screen.buildId, screen.startStep)
        is Screen.BuildCompleted -> BuildCompletedScreen(viewModel, screen.buildId)
        is Screen.Favorites -> FavoritesScreen(viewModel)
        is Screen.Profile -> ProfileScreen(viewModel)
        is Screen.Settings -> SettingsScreen(viewModel)
        is Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
        is Screen.AdminCreateBuild -> AdminBuildEditorScreen(viewModel)
        is Screen.AdminEditBuild -> AdminBuildEditorScreen(viewModel, screen.buildId)
        is Screen.AdminManageSteps -> AdminManageStepsScreen(viewModel, screen.buildId)
        is Screen.AdminManageCategories -> AdminManageCategoriesScreen(viewModel)
      }
    }
  }
}
