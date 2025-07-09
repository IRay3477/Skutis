package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.ui.theme.ScootEaseTheme
import com.example.scootease.R
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType

// Data contoh untuk motor yang "dipilih" di peta.
val selectedBikeForMap = Bike(
    id = 2,
    name = "Yamaha NMAX",
    specs = "155cc · Otomatis · Bagasi Luas",
    price = "120k",
    rating = 4.8,
    imageRes = R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC // Pastikan Anda memiliki gambar ini di res/drawable
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 1. Tambahkan parameter onNavigateBack di sini
fun MapScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        // 2. Teruskan parameter onNavigateBack ke MapTopBar
        topBar = { MapTopBar(onNavigateBack = onNavigateBack) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapPlaceholder()
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BikeInfoBottomSheet(bike = selectedBikeForMap)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 3. Tambahkan parameter onNavigateBack di sini juga
fun MapTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text("Cari di Sekitarmu", fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            // 4. Gunakan parameter onNavigateBack pada saat tombol diklik
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Tampilkan dialog filter */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter Peta")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    )
}

@Composable
fun MapPlaceholder() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background), // GANTI DENGAN GAMBAR PETA
        contentDescription = "Peta Lokasi Motor",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun BikeInfoBottomSheet(bike: Bike) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = bike.imageRes),
                contentDescription = bike.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bike.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(bike.specs, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, "Rating", tint = Color(0xFFFFC700), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${bike.rating}  ·  IDR ${bike.price}/hari",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Button(
                onClick = { /* TODO: Navigasi ke halaman detail motor */ },
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Sewa")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    ScootEaseTheme {
        // 5. Beri nilai default untuk onNavigateBack di preview agar tidak error
        MapScreen(onNavigateBack = {})
    }
}
