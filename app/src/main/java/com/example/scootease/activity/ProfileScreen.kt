package com.example.scootease.activity

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.scootease.ui.theme.ScootEaseTheme
import com.example.scootease.R

@Composable
fun ProfileScreen(
    username: String,
    email: String,
    onLogoutClick: () -> Unit,
    onNavigateToDocVerification: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    // Data untuk menu sekarang didefinisikan di dalam Composable
    val accountMenuItems = listOf(
        MenuItemData(Icons.Default.VerifiedUser, "Document Verification", onNavigateToDocVerification),
    )
    val otherMenuItems = listOf(
        MenuItemData(Icons.AutoMirrored.Filled.HelpOutline, "Help", onNavigateToHelp),
        MenuItemData(Icons.Default.Info, "About Us", onNavigateToAbout),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        ProfileHeaderSection(username = username, email = email)
        Spacer(modifier = Modifier.height(16.dp))
        MenuSection(title = "Account", items = accountMenuItems)
        Spacer(modifier = Modifier.height(16.dp))
        MenuSection(title = "Others", items = otherMenuItems)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Logout", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileHeaderSection(username: String, email: String) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // Gunakan AsyncImage dari Coil untuk menampilkan gambar dari URI
            AsyncImage(
                model = imageUri ?: R.drawable.ic_launcher_foreground,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .size(30.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Change Profile Picture", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MenuSection(title: String, items: List<MenuItemData>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        items.forEach { item ->
            MenuItem(icon = item.icon, title = item.title, onClick = item.onClick)
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Lanjutkan",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class MenuItemData(val icon: ImageVector, val title: String, val onClick: () -> Unit = {})
val accountMenuItems = listOf(
    MenuItemData(Icons.Default.VerifiedUser, "Document Verification"),
)
val otherMenuItems = listOf(
    MenuItemData(Icons.AutoMirrored.Filled.HelpOutline, "Help"),
    MenuItemData(Icons.Default.Info, "About Us"),
)


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ScootEaseTheme {
        ProfileScreen(
            username = "User Example",
            email = "example@email.com",
            onLogoutClick = {},
            onNavigateToDocVerification = {},
            onNavigateToHelp = {},
            onNavigateToAbout = {}
        )
    }
}
