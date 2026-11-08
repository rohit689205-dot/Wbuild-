package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.BuildItem
import com.example.data.model.Category
import com.example.ui.theme.WBuildsBadgeBg
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsCardElevated
import com.example.ui.theme.WBuildsDarkSurface
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsGoldLight
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@Composable
fun WBuildsTopBar(
  title: String? = null,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  showSearch: Boolean = true,
  onSearchClick: () -> Unit = {},
  showSettings: Boolean = true,
  onSettingsClick: () -> Unit = {},
  isAdmin: Boolean = false,
  onAdminClick: () -> Unit = {}
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(WBuildsBlack)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    // Left action
    Row(
      modifier = Modifier.align(Alignment.CenterStart),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = WBuildsGold
          )
        }
      } else if (showSettings) {
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier.testTag("settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = WBuildsTextSecondary
          )
        }
      }
    }

    // Center Branding / Title
    Row(
      modifier = Modifier.align(Alignment.Center),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (title == null) {
        // WBuilds Golden Logo
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WBuildsGoldDark),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            tint = WBuildsBlack,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "WBUILDS",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = WBuildsGold,
            letterSpacing = 1.sp
          )
          Text(
            text = "MINECRAFT GUIDE",
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            color = WBuildsTextSecondary,
            letterSpacing = 1.5.sp
          )
        }
      } else {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = WBuildsTextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Right actions
    Row(
      modifier = Modifier.align(Alignment.CenterEnd),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isAdmin) {
        IconButton(
          onClick = onAdminClick,
          modifier = Modifier.testTag("admin_panel_button")
        ) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin Panel",
            tint = WBuildsGold
          )
        }
      }
      if (showSearch) {
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier.testTag("search_icon_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Builds",
            tint = WBuildsTextPrimary
          )
        }
      }
    }
  }
}

@Composable
fun CategoryChipsRow(
  categories: List<Category>,
  selectedCategoryId: String,
  onCategorySelected: (String) -> Unit
) {
  val scrollState = rememberScrollState()

  val allCategories = listOf(
    Pair("favorites", "FAV"),
    Pair("all", "All"),
    Pair("beacon", "Beacon"),
    Pair("decoration", "Decoration"),
    Pair("farm", "Farm"),
    Pair("furniture", "Furniture"),
    Pair("houses", "Houses"),
    Pair("mansions", "Mansions"),
    Pair("statues", "Statues"),
    Pair("animals", "Animals"),
    Pair("redstone", "Redstone"),
    Pair("survival", "Survival"),
    Pair("modern", "Modern")
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    allCategories.forEach { (catId, catName) ->
      val isSelected = selectedCategoryId.equals(catId, ignoreCase = true)
      val bg = if (isSelected) Color.White else WBuildsCard
      val textColor = if (isSelected) WBuildsBlack else WBuildsTextPrimary

      if (catId == "favorites") {
        // Heart icon pill button on far left
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) WBuildsGold else WBuildsCard)
            .border(1.dp, if (isSelected) WBuildsGold else WBuildsDivider, RoundedCornerShape(10.dp))
            .clickable { onCategorySelected("favorites") }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("category_chip_favorites"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isSelected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorites",
            tint = if (isSelected) WBuildsBlack else WBuildsTextPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      } else {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, if (isSelected) Color.White else WBuildsDivider, RoundedCornerShape(10.dp))
            .clickable { onCategorySelected(catId) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("category_chip_$catId"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = catName,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
          )
        }
      }
    }
  }
}

@Composable
fun BuildCard(
  build: BuildItem,
  isFavorite: Boolean,
  onBuildClick: () -> Unit,
  onFavoriteClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onBuildClick)
      .border(1.dp, WBuildsDivider, RoundedCornerShape(16.dp))
      .testTag("build_card_${build.id}"),
    colors = CardDefaults.cardColors(containerColor = WBuildsBlack)
  ) {
    Column {
      // Thumbnail Box with W BUILDS Golden Watermark
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(0.95f)
          .clip(RoundedCornerShape(16.dp))
      ) {
        BuildThumbnailImage(
          imageUrl = build.thumbnailUrl,
          contentDescription = build.title,
          modifier = Modifier.fillMaxSize()
        )

        // Watermark Badge in Bottom Right corner
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Text(
            text = "W BUILDS",
            color = WBuildsGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }

        // Favorite Button top right (Subtle)
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(28.dp)
            .clip(CircleShape)
            .background(WBuildsBlack.copy(alpha = 0.6f))
            .clickable(onClick = onFavoriteClick)
            .testTag("fav_button_${build.id}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) WBuildsGold else Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Title & Subtitle Below Card Image
      Column(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
      ) {
        Text(
          text = build.title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${build.totalSteps} build steps",
          color = WBuildsTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal
        )
      }
    }
  }
}

@Composable
fun DifficultyPill(difficulty: String) {
  val (color, bg) = when (difficulty.lowercase()) {
    "easy" -> Pair(WBuildsSuccess, WBuildsBlack.copy(alpha = 0.8f))
    "medium" -> Pair(WBuildsGold, WBuildsBlack.copy(alpha = 0.8f))
    "hard" -> Pair(Color(0xFFFF9500), WBuildsBlack.copy(alpha = 0.8f))
    "expert" -> Pair(Color(0xFFFF453A), WBuildsBlack.copy(alpha = 0.8f))
    else -> Pair(WBuildsTextSecondary, WBuildsBlack.copy(alpha = 0.8f))
  }
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = difficulty.uppercase(),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = color
    )
  }
}

@Composable
fun BuildThumbnailImage(
  imageUrl: String,
  contentDescription: String?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  when {
    imageUrl == "img_minecraft_hero" -> {
      Image(
        painter = painterResource(id = R.drawable.img_minecraft_hero),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl == "img_splash_art" -> {
      Image(
        painter = painterResource(id = R.drawable.img_splash_art),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl == "img_app_icon_fg" -> {
      Image(
        painter = painterResource(id = R.drawable.img_app_icon_fg),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl.startsWith("http://") || imageUrl.startsWith("https://") ||
      imageUrl.startsWith("content:") || imageUrl.startsWith("file:") ||
      imageUrl.startsWith("gs://") -> {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(imageUrl)
          .crossfade(true)
          .diskCachePolicy(coil.request.CachePolicy.ENABLED)
          .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
          .networkCachePolicy(coil.request.CachePolicy.ENABLED)
          .error(R.drawable.img_minecraft_hero)
          .placeholder(R.drawable.img_minecraft_hero)
          .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl.isNotBlank() && !imageUrl.startsWith("img_") -> {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(imageUrl)
          .crossfade(true)
          .diskCachePolicy(coil.request.CachePolicy.ENABLED)
          .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
          .networkCachePolicy(coil.request.CachePolicy.ENABLED)
          .error(R.drawable.img_minecraft_hero)
          .placeholder(R.drawable.img_minecraft_hero)
          .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    else -> {
      Image(
        painter = painterResource(id = R.drawable.img_minecraft_hero),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
  }
}

@Composable
fun MinecraftBlockIcon(
  blockName: String,
  imageUrl: String = "",
  modifier: Modifier = Modifier.size(36.dp)
) {
  val context = LocalContext.current
  if (imageUrl.startsWith("http") || imageUrl.startsWith("content:") || imageUrl.startsWith("file:") || imageUrl.startsWith("gs:")) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
        .networkCachePolicy(coil.request.CachePolicy.ENABLED)
        .error(R.drawable.img_minecraft_hero)
        .placeholder(R.drawable.img_minecraft_hero)
        .build(),
      contentDescription = blockName,
      modifier = modifier.clip(RoundedCornerShape(6.dp)),
      contentScale = ContentScale.Crop
    )
  } else {
    val (primaryColor, accentColor) = when {
      blockName.contains("Stone", ignoreCase = true) || blockName.contains("Andesite", ignoreCase = true) || blockName.contains("Cobble", ignoreCase = true) -> Pair(Color(0xFF888888), Color(0xFF666666))
      blockName.contains("Oak", ignoreCase = true) || blockName.contains("Planks", ignoreCase = true) || blockName.contains("Barrel", ignoreCase = true) -> Pair(Color(0xFFA0764A), Color(0xFF6E4D2B))
      blockName.contains("Spruce", ignoreCase = true) -> Pair(Color(0xFF61442B), Color(0xFF3F2B1A))
      blockName.contains("Dirt", ignoreCase = true) || blockName.contains("Farmland", ignoreCase = true) -> Pair(Color(0xFF573D26), Color(0xFF3D2A1A))
      blockName.contains("Glass", ignoreCase = true) -> Pair(Color(0xC0A2D2FF), Color(0xFF64B5F6))
      blockName.contains("Water", ignoreCase = true) -> Pair(Color(0xFF29B6F6), Color(0xFF0288D1))
      blockName.contains("Lantern", ignoreCase = true) || blockName.contains("Torch", ignoreCase = true) || blockName.contains("Candle", ignoreCase = true) -> Pair(Color(0xFFFFB74D), Color(0xFFF57C00))
      blockName.contains("Diamond", ignoreCase = true) -> Pair(Color(0xFF4DD0E1), Color(0xFF00ACC1))
      blockName.contains("Gold", ignoreCase = true) -> Pair(Color(0xFFFFD54F), Color(0xFFFFB300))
      blockName.contains("Emerald", ignoreCase = true) -> Pair(Color(0xFF81C784), Color(0xFF43A047))
      blockName.contains("Redstone", ignoreCase = true) -> Pair(Color(0xFFE57373), Color(0xFFE53935))
      blockName.contains("Carrot", ignoreCase = true) -> Pair(Color(0xFFFF9800), Color(0xFF4CAF50))
      blockName.contains("Cauldron", ignoreCase = true) || blockName.contains("Chest", ignoreCase = true) -> Pair(Color(0xFF424242), Color(0xFF212121))
      else -> Pair(WBuildsGoldDark, WBuildsGold)
    }

    Box(
      modifier = modifier
        .clip(RoundedCornerShape(6.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(primaryColor, accentColor)
          )
        )
        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = blockName.take(2).uppercase(),
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 10.sp
      )
    }
  }
}
