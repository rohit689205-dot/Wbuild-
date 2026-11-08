package com.example.ui.navigation

sealed class Screen {
  object Splash : Screen()
  object Auth : Screen()
  object Home : Screen()
  object Search : Screen()
  data class BuildDetails(val buildId: String) : Screen()
  data class StepViewer(val buildId: String, val startStep: Int = 1) : Screen()
  data class BuildCompleted(val buildId: String) : Screen()
  object Favorites : Screen()
  object Profile : Screen()
  object Settings : Screen()
  object AdminDashboard : Screen()
  object AdminCreateBuild : Screen()
  data class AdminEditBuild(val buildId: String) : Screen()
  data class AdminManageSteps(val buildId: String) : Screen()
  object AdminManageCategories : Screen()
}
