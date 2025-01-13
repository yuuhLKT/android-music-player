package com.example.musicplayer.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.musicplayer.R

data class BottomNavItem(
    val icon: Int,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(R.drawable.ic_home_screen, "Home"),
    BottomNavItem(R.drawable.ic_songs_screen, "Songs"),
    BottomNavItem(R.drawable.ic_playlists_screen, "Playlists")
)

@Composable
fun BottomNavigationBar(
    onTabSelected: (Int) -> Unit,
    selectedTabIndex: Int
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}