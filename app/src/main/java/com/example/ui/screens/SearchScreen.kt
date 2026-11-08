package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldLight
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
  viewModel: WBuildsViewModel
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val builds by viewModel.builds.collectAsState()
  val userFavorites by viewModel.userFavorites.collectAsState()
  val focusManager = LocalFocusManager.current

  BackHandler {
    viewModel.navigateBack()
  }

  val popularTags = listOf("Farm", "Medieval", "Mansion", "Tavern", "Castle", "Statue", "Ship", "Easy", "Java")

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(WBuildsBlack)
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateBack() },
          modifier = Modifier.testTag("search_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = WBuildsGold
          )
        }

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search Minecraft builds, difficulty, blocks...", fontSize = 13.sp, color = WBuildsTextSecondary) },
          singleLine = true,
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = WBuildsGold)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = WBuildsTextSecondary)
              }
            }
          },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WBuildsGold,
            unfocusedBorderColor = WBuildsDivider,
            focusedTextColor = WBuildsTextPrimary,
            unfocusedTextColor = WBuildsTextPrimary,
            focusedContainerColor = WBuildsCard,
            unfocusedContainerColor = WBuildsCard
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_text_input")
        )
      }
    },
    bottomBar = {
      WBuildsBottomNav(
        currentScreen = Screen.Search,
        onNavigate = { screen -> viewModel.navigateTo(screen) }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Popular tags chips
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = "POPULAR SEARCHES",
          color = WBuildsGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          popularTags.forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(WBuildsCard)
                .border(1.dp, WBuildsDivider, RoundedCornerShape(16.dp))
                .clickable { viewModel.setSearchQuery(tag) }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = tag,
                color = WBuildsTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Results Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 10.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item(span = { GridItemSpan(2) }) {
          Text(
            text = "${builds.size} RESULTS",
            color = WBuildsGoldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
          )
        }

        if (builds.isEmpty()) {
          item(span = { GridItemSpan(2) }) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No builds found matching '$searchQuery'.",
                color = WBuildsTextSecondary,
                fontSize = 14.sp
              )
            }
          }
        } else {
          items(builds, key = { it.id }) { build ->
            val isFav = userFavorites.any { it.buildId == build.id }
            BuildCard(
              build = build,
              isFavorite = isFav,
              onBuildClick = { viewModel.openBuild(build.id) },
              onFavoriteClick = { viewModel.toggleFavorite(build) }
            )
          }
        }
      }
    }
  }
}
