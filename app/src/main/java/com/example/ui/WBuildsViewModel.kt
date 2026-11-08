package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BuildItem
import com.example.data.model.BuildStep
import com.example.data.model.BuildingProgress
import com.example.data.model.Category
import com.example.data.model.FavoriteItem
import com.example.data.model.UserProfile
import com.example.data.repository.WBuildsRepository
import com.example.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WBuildsViewModel(private val repository: WBuildsRepository) : ViewModel() {

  private val auth: FirebaseAuth = Firebase.auth

  // Screen stack for back-stack navigation
  private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
  val currentScreen: StateFlow<Screen> = _screenStack.combine(_screenStack) { stack, _ ->
    stack.lastOrNull() ?: Screen.Home
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Splash)

  // Auth state
  private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
  val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

  private val _isGuest = MutableStateFlow(false)
  val isGuest: StateFlow<Boolean> = _isGuest.asStateFlow()

  val isAdmin: Boolean
    get() = repository.isCurrentUserAdmin()

  // Selected category & search filter
  private val _selectedCategoryId = MutableStateFlow("all")
  val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Data streams
  val categories: StateFlow<List<Category>> = repository.observeCategories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val builds: StateFlow<List<BuildItem>> = combine(
    _selectedCategoryId,
    _searchQuery
  ) { catId, query ->
    Pair(catId, query)
  }.combine(repository.observeBuilds()) { pair, allBuilds ->
    val (catId, query) = pair
    var filtered = allBuilds
    if (catId != "all" && catId != "favorites") {
      filtered = filtered.filter { it.categoryId.equals(catId, ignoreCase = true) }
    }
    if (query.isNotBlank()) {
      val q = query.trim().lowercase()
      filtered = filtered.filter {
        it.title.lowercase().contains(q) ||
          it.description.lowercase().contains(q) ||
          it.difficulty.lowercase().contains(q) ||
          it.edition.lowercase().contains(q)
      }
    }
    filtered
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Admin builds (includes drafts)
  val adminBuilds: StateFlow<List<BuildItem>> = repository.observeBuilds(includeDrafts = true)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // User favorites
  private val _userFavorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
  val userFavorites: StateFlow<List<FavoriteItem>> = _userFavorites.asStateFlow()

  // User profile
  private val _userProfile = MutableStateFlow<UserProfile?>(null)
  val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

  // Active build details
  private val _activeBuild = MutableStateFlow<BuildItem?>(null)
  val activeBuild: StateFlow<BuildItem?> = _activeBuild.asStateFlow()

  private val _activeSteps = MutableStateFlow<List<BuildStep>>(emptyList())
  val activeSteps: StateFlow<List<BuildStep>> = _activeSteps.asStateFlow()

  private val _activeProgress = MutableStateFlow<BuildingProgress?>(null)
  val activeProgress: StateFlow<BuildingProgress?> = _activeProgress.asStateFlow()

  // Message / Notification Toast
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  init {
    auth.addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      _currentUser.value = user
      if (user != null) {
        _isGuest.value = false
        observeUserData(user.uid)
        syncUserProfile(user)
      } else {
        _userFavorites.value = emptyList()
        _userProfile.value = null
      }
    }

    // Auto-seed initial content if user is admin
    viewModelScope.launch {
      if (repository.isCurrentUserAdmin()) {
        repository.seedInitialDataIfEmpty()
      }
    }
  }

  private fun observeUserData(uid: String) {
    viewModelScope.launch {
      repository.observeFavorites(uid).collect { favs ->
        _userFavorites.value = favs
      }
    }
    viewModelScope.launch {
      repository.observeUserProfile(uid).collect { profile ->
        _userProfile.value = profile
      }
    }
  }

  private fun syncUserProfile(user: FirebaseUser) {
    viewModelScope.launch {
      try {
        repository.syncUserProfile(
          userId = user.uid,
          displayName = user.displayName ?: "Builder",
          email = user.email ?: "",
          photoURL = user.photoUrl?.toString() ?: ""
        )
      } catch (e: Exception) {
        // Ignored or logged
      }
    }
  }

  // --- Navigation Controls ---
  fun navigateTo(screen: Screen) {
    val current = _screenStack.value
    _screenStack.value = current + screen
  }

  fun navigateBack(): Boolean {
    val current = _screenStack.value
    if (current.size > 1) {
      _screenStack.value = current.dropLast(1)
      return true
    }
    return false
  }

  fun continueAsGuest() {
    _isGuest.value = true
    _screenStack.value = listOf(Screen.Home)
  }

  fun onAuthSuccess() {
    _isGuest.value = false
    _screenStack.value = listOf(Screen.Home)
  }

  fun signOut() {
    auth.signOut()
    _isGuest.value = false
    _screenStack.value = listOf(Screen.Auth)
  }

  fun clearMessage() {
    _userMessage.value = null
  }

  fun showMessage(msg: String) {
    _userMessage.value = msg
  }

  // --- Filtering ---
  fun selectCategory(categoryId: String) {
    _selectedCategoryId.value = categoryId
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // --- Build Details & Steps ---
  fun openBuild(buildId: String) {
    viewModelScope.launch {
      val item = repository.getBuildById(buildId)
      _activeBuild.value = item
      if (item != null) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
          repository.observeProgress(uid, buildId).collect { prog ->
            _activeProgress.value = prog
          }
        }
      }
      navigateTo(Screen.BuildDetails(buildId))
    }
  }

  fun startBuilding(buildId: String, startStep: Int = 1) {
    viewModelScope.launch {
      repository.observeBuildSteps(buildId).collect { steps ->
        _activeSteps.value = steps
      }
    }
    navigateTo(Screen.StepViewer(buildId, startStep))
  }

  fun isBuildFavorited(buildId: String): Boolean {
    return _userFavorites.value.any { it.buildId == buildId }
  }

  fun toggleFavorite(build: BuildItem) {
    val uid = auth.currentUser?.uid
    if (uid == null) {
      _userMessage.value = "Sign in to save favorites!"
      navigateTo(Screen.Auth)
      return
    }
    val isFav = isBuildFavorited(build.id)
    viewModelScope.launch {
      try {
        repository.toggleFavorite(uid, build, isFav)
        _userMessage.value = if (isFav) "Removed from favorites" else "Added to favorites!"
      } catch (e: Exception) {
        _userMessage.value = "Failed to update favorite: ${e.message}"
      }
    }
  }

  fun saveProgress(buildId: String, currentStep: Long, totalSteps: Long) {
    val uid = auth.currentUser?.uid ?: return
    viewModelScope.launch {
      try {
        val isFinished = currentStep >= totalSteps && totalSteps > 0
        repository.saveProgress(uid, buildId, currentStep, currentStep, isFinished)
      } catch (e: Exception) {
        // Logged
      }
    }
  }

  fun completeBuild(buildId: String, totalSteps: Long) {
    val uid = auth.currentUser?.uid
    if (uid != null) {
      viewModelScope.launch {
        repository.saveProgress(uid, buildId, totalSteps, totalSteps, isCompleted = true)
      }
    }
    navigateTo(Screen.BuildCompleted(buildId))
  }

  // --- Admin Actions ---
  fun adminPublishWorkflow(
    build: BuildItem,
    stepImages: List<String>,
    selectedBlocks: List<Map<String, Any>>,
    isPublished: Boolean,
    onDone: () -> Unit
  ) {
    viewModelScope.launch {
      try {
        repository.publishBuildWorkflow(build, stepImages, selectedBlocks, isPublished)
        _userMessage.value = if (isPublished) "Build published successfully!" else "Draft saved successfully!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to save build: ${e.message}"
      }
    }
  }

  fun adminCreateBuild(build: BuildItem, onDone: () -> Unit) {
    viewModelScope.launch {
      try {
        repository.createBuild(build)
        _userMessage.value = "Build created successfully!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to create build: ${e.message}"
      }
    }
  }

  fun adminUpdateBuild(build: BuildItem, onDone: () -> Unit) {
    viewModelScope.launch {
      try {
        repository.updateBuild(build)
        _userMessage.value = "Build updated!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to update build: ${e.message}"
      }
    }
  }

  fun adminDeleteBuild(buildId: String) {
    viewModelScope.launch {
      try {
        repository.deleteBuild(buildId)
        _userMessage.value = "Build deleted"
      } catch (e: Exception) {
        _userMessage.value = "Failed to delete build: ${e.message}"
      }
    }
  }

  fun adminCreateCategory(category: Category, onDone: () -> Unit) {
    viewModelScope.launch {
      try {
        repository.createCategory(category)
        _userMessage.value = "Category added!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to add category: ${e.message}"
      }
    }
  }

  fun adminUpdateCategory(category: Category, onDone: () -> Unit) {
    viewModelScope.launch {
      try {
        repository.updateCategory(category)
        _userMessage.value = "Category updated!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to update category: ${e.message}"
      }
    }
  }

  fun adminDeleteCategory(categoryId: String) {
    viewModelScope.launch {
      try {
        repository.deleteCategory(categoryId)
        _userMessage.value = "Category deleted!"
      } catch (e: Exception) {
        _userMessage.value = "Failed to delete category: ${e.message}"
      }
    }
  }

  fun adminSaveStep(step: BuildStep, onDone: () -> Unit) {
    viewModelScope.launch {
      try {
        repository.saveBuildStep(step)
        _userMessage.value = "Step saved!"
        onDone()
      } catch (e: Exception) {
        _userMessage.value = "Failed to save step: ${e.message}"
      }
    }
  }

  fun adminDeleteStep(stepId: String) {
    viewModelScope.launch {
      try {
        repository.deleteBuildStep(stepId)
        _userMessage.value = "Step removed"
      } catch (e: Exception) {
        _userMessage.value = "Failed to delete step: ${e.message}"
      }
    }
  }

  fun adminTriggerSeed() {
    viewModelScope.launch {
      try {
        repository.seedInitialDataIfEmpty()
        _userMessage.value = "Default builds seeded into Firestore!"
      } catch (e: Exception) {
        _userMessage.value = "Seed error: ${e.message}"
      }
    }
  }
}
