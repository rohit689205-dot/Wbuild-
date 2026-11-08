package com.example.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildCard
import com.example.ui.components.WBuildsTopBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsCardElevated
import com.example.ui.theme.WBuildsDarkSurface
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- 1. SPLASH SCREEN ---
@Composable
fun SplashScreen(
  viewModel: WBuildsViewModel
) {
  LaunchedEffect(Unit) {
    delay(1800)
    viewModel.navigateTo(Screen.Home)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(WBuildsBlack)
  ) {
    // Fullscreen Background Artwork
    Image(
      painter = painterResource(id = R.drawable.img_splash_art),
      contentDescription = "Minecraft Splash Art",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // Dark Gradient
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0x88000000), Color(0xDD000000), WBuildsBlack),
            startY = 100f
          )
        )
    )

    // Brand and Tagline
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.weight(1f))

      Box(
        modifier = Modifier
          .size(90.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(WBuildsGoldDark)
          .border(2.dp, WBuildsGold, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Construction,
          contentDescription = null,
          tint = WBuildsBlack,
          modifier = Modifier.size(54.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "WBUILDS",
        color = WBuildsGold,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "THE ULTIMATE MINECRAFT BUILDING GUIDE",
        color = WBuildsTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Discover blueprints, follow image-by-image building tutorials, and build epic structures.",
        color = WBuildsTextSecondary,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.weight(1f))

      CircularProgressIndicator(
        modifier = Modifier.size(32.dp),
        color = WBuildsGold,
        strokeWidth = 3.dp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { viewModel.navigateTo(Screen.Home) },
        colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("enter_app_button")
      ) {
        Text("ENTER GALLERY", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

// --- 2. AUTH SCREEN (Google Sign-In via Credential Manager) ---
@Composable
fun AuthScreen(
  viewModel: WBuildsViewModel
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  BackHandler {
    viewModel.continueAsGuest()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(WBuildsBlack)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, WBuildsDivider, RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = WBuildsCard),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(WBuildsGoldDark),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            tint = WBuildsBlack,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Welcome to WBuilds",
          color = WBuildsTextPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Sign in to save your building progress, sync favorite blueprints across devices, and access the admin builder suite.",
          color = WBuildsTextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = errorMessage!!,
            color = Color(0xFFFF453A),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Google Sign-In Button
        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            val clientId = try {
              context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
              errorMessage = "Google Sign-In configuration missing"
              isLoading = false
              return@Button
            }

            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()
            val credentialManager = CredentialManager.create(context)

            scope.launch {
              try {
                val result = credentialManager.getCredential(context as Activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                  val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                  val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                  Firebase.auth.signInWithCredential(authCredential).await()
                  isLoading = false
                  viewModel.onAuthSuccess()
                } else {
                  errorMessage = "Unexpected credential response"
                  isLoading = false
                }
              } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
                isLoading = false
              } catch (e: Exception) {
                Log.e("Auth", "Sign-in error", e)
                errorMessage = e.localizedMessage ?: "Sign in failed"
                isLoading = false
              }
            }
          },
          enabled = !isLoading,
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("google_signin_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = WBuildsBlack,
              strokeWidth = 2.dp
            )
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = WBuildsBlack)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Sign in with Google", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guest exploration option
        OutlinedButton(
          onClick = { viewModel.continueAsGuest() },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
            .testTag("guest_continue_button")
        ) {
          Text("Continue as Guest", color = WBuildsTextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

// --- 3. FAVORITES SCREEN ---
@Composable
fun FavoritesScreen(
  viewModel: WBuildsViewModel
) {
  val userFavorites by viewModel.userFavorites.collectAsState()
  val builds by viewModel.builds.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  BackHandler {
    viewModel.navigateTo(Screen.Home)
  }

  val favoriteBuilds = builds.filter { b -> userFavorites.any { f -> f.buildId == b.id } }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "My Favorites (${favoriteBuilds.size})",
        showBack = true,
        onBackClick = { viewModel.navigateTo(Screen.Home) },
        showSearch = true,
        onSearchClick = { viewModel.navigateTo(Screen.Search) }
      )
    },
    bottomBar = {
      WBuildsBottomNav(
        currentScreen = Screen.Favorites,
        onNavigate = { screen -> viewModel.navigateTo(screen) }
      )
    }
  ) { paddingValues ->
    if (currentUser == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = WBuildsGold, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("Sign in to View Favorites", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Save your favorite Minecraft designs across devices.", color = WBuildsTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.navigateTo(Screen.Auth) },
            colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold)
          ) {
            Text("Sign In", color = WBuildsBlack, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else if (favoriteBuilds.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.Favorite, contentDescription = null, tint = WBuildsTextSecondary, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No Favorites Yet", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Tap the bookmark heart icon on any build card to add it to your favorites.", color = WBuildsTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.navigateTo(Screen.Home) },
            colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold)
          ) {
            Text("Browse Gallery", color = WBuildsBlack, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(favoriteBuilds, key = { it.id }) { build ->
          BuildCard(
            build = build,
            isFavorite = true,
            onBuildClick = { viewModel.openBuild(build.id) },
            onFavoriteClick = { viewModel.toggleFavorite(build) }
          )
        }
      }
    }
  }
}

// --- 4. PROFILE SCREEN ---
@Composable
fun ProfileScreen(
  viewModel: WBuildsViewModel
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val userFavorites by viewModel.userFavorites.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val isAdmin = viewModel.isAdmin

  BackHandler {
    viewModel.navigateTo(Screen.Home)
  }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "Architect Profile",
        showBack = true,
        onBackClick = { viewModel.navigateTo(Screen.Home) },
        showSearch = false,
        showSettings = true,
        onSettingsClick = { viewModel.navigateTo(Screen.Settings) }
      )
    },
    bottomBar = {
      WBuildsBottomNav(
        currentScreen = Screen.Profile,
        onNavigate = { screen -> viewModel.navigateTo(screen) }
      )
    }
  ) { paddingValues ->
    val scrollState = rememberScrollState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp)
        .verticalScroll(scrollState)
    ) {
      // Profile Header Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, WBuildsDivider, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = WBuildsCard),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(WBuildsGoldDark)
              .border(2.dp, WBuildsGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = WBuildsBlack,
              modifier = Modifier.size(44.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = currentUser?.displayName ?: "Guest Builder",
            color = WBuildsTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )

          Text(
            text = currentUser?.email ?: "Sign in to activate cloud sync",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Role Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isAdmin) WBuildsGold else WBuildsDarkSurface)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = if (isAdmin) "ADMINISTRATOR" else "MASTER BUILDER",
              color = if (isAdmin) WBuildsBlack else WBuildsGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Stats Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
        ) {
          Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${userFavorites.size}", color = WBuildsGold, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Favorites", color = WBuildsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
        ) {
          Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("100%", color = WBuildsSuccess, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Accuracy", color = WBuildsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
        ) {
          Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cloud", color = WBuildsTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Firestore", color = WBuildsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Admin Panel shortcut if admin
      if (isAdmin) {
        Button(
          onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("profile_admin_panel_button")
        ) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = WBuildsBlack)
          Spacer(modifier = Modifier.width(8.dp))
          Text("OPEN ADMIN DASHBOARD", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Sign In or Sign Out button
      if (currentUser != null) {
        OutlinedButton(
          onClick = { viewModel.signOut() },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
            .testTag("sign_out_button")
        ) {
          Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF453A))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Sign Out", color = Color(0xFFFF453A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      } else {
        Button(
          onClick = { viewModel.navigateTo(Screen.Auth) },
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("profile_sign_in_button")
        ) {
          Text("Sign In with Google", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
      }
    }
  }
}

// --- 5. SETTINGS SCREEN ---
@Composable
fun SettingsScreen(
  viewModel: WBuildsViewModel
) {
  BackHandler {
    viewModel.navigateBack()
  }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "Settings",
        showBack = true,
        onBackClick = { viewModel.navigateBack() },
        showSearch = false,
        showSettings = false
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WBuildsCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("ABOUT WBUILDS", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text("WBuilds: The Ultimate Minecraft Building Guide", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Version 1.0.0 (Production)", color = WBuildsTextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Designed for builders of all skill levels. Image-by-image instructions, block quantities, and cloud synchronization.",
            color = WBuildsTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        }
      }

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WBuildsCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("CLOUD PERSISTENCE", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudDone, contentDescription = null, tint = WBuildsSuccess, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Google Cloud Firestore Enterprise", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Database status: Connected and synchronized with zero-trust security rules.", color = WBuildsTextSecondary, fontSize = 12.sp)
        }
      }
    }
  }
}
