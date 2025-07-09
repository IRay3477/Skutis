package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.R
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType
import com.example.scootease.ui.theme.ScootEaseTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// --- PERUBAHAN: Tambahkan parameter allBikes ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    allBikes: List<Bike>,
    onNavigateBack: () -> Unit
) {
    // --- State untuk melacak motor yang dipilih di peta ---
    var selectedBike by remember { mutableStateOf<Bike?>(null) }

    // --- Data Lokasi Contoh untuk Motor ---
    // Di aplikasi nyata, ini akan datang dari database Anda.
    val bikeLocations = remember {
        mapOf(
            1 to LatLng(-8.4594, 115.2683), // Vario di Ubud
            2 to LatLng(-8.6573, 115.2224), // NMAX di Denpasar
            3 to LatLng(-8.8149, 115.1697), // Scoopy di Kuta
            4 to LatLng(-8.7222, 115.1768), // PCX di Seminyak
            5 to LatLng(-8.4095, 115.2933), // Harley 48 di Tegalalang
            6 to LatLng(-8.3405, 115.0919), // BMW GS di Bedugul
            7 to LatLng(-8.7909, 115.2011)  // Harley RG di Nusa Dua
        )
    }

    // --- Pengaturan awal kamera peta, fokus di Bali ---
    val baliCenter = LatLng(-8.65, 115.2166)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(baliCenter, 10f)
    }

    Scaffold(
        topBar = { MapTopBar(onNavigateBack = onNavigateBack) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- PERBAIKAN: Gunakan GoogleMap ---
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    // Sembunyikan bottom sheet jika klik area kosong di peta
                    selectedBike = null
                }
            ) {
                // --- Tampilkan semua motor sebagai Marker ---
                allBikes.forEach { bike ->
                    bikeLocations[bike.id]?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = bike.name,
                            snippet = "Klik untuk detail",
                            // Ganti ikon berdasarkan status
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (bike.status == BikeStatus.AVAILABLE) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                            ),
                            onClick = {
                                selectedBike = bike
                                // Mengembalikan false agar event klik default (seperti judul muncul) tetap berjalan
                                false
                            }
                        )
                    }
                }
            }
            // --- Tampilkan Bottom Sheet jika ada motor yang dipilih ---
            if (selectedBike != null) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    BikeInfoBottomSheet(bike = selectedBike!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Cari di Sekitarmu", fontWeight = FontWeight.Bold) },
        navigationIcon = {
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
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)),
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
                    Text(" ${bike.rating}  ·  IDR ${bike.price}/hari", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
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
        MapScreen(allBikes = emptyList(), onNavigateBack = {})
    }
}