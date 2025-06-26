package com.example.scootease

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scootease.ui.theme.ScootEaseTheme
import java.text.SimpleDateFormat
import java.util.*

// --- Dummy Data (Data Contoh) ---
// Di aplikasi nyata, data ini akan berasal dari ViewModel atau API
val bikeCategories = listOf(
    Category("All"),
    Category("Scooter"),
    Category("Sport"),
    Category("Adventure"),
    Category("Classic")
)

val popularBikes = listOf(
    Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario),
    Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax),
    Bike(3, "Honda Scoopy", "110cc · Auto", "75k", 4.9, R.drawable.honda_scoopy),
    Bike(4, "Honda PCX", "150cc · Auto", "150k", 4.7, R.drawable.honda_pcx),
    Bike(5, "Harley Sporster 48", "1200cc · Manual", "2000k", 4.6, R.drawable.harley_48)
)
// Catatan: Ganti R.drawable.ic_launcher_background dengan gambar motor Anda sendiri nanti.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToProfile: () -> Unit) {
    Scaffold(
        // 2. Teruskan parameter ke TopHeader
        topBar = { TopHeader(onProfileClick = onNavigateToProfile) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchCard()
                CategoriesSection()
                PopularBikesSection()
                PromotionBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// 3. TopHeader sekarang menerima parameter onProfileClick
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader(onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Your Location", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, "Location", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gianyar, Bali", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                BadgedBox(badge = { Badge { Text("3") } }) {
                    Icon(Icons.Filled.Notifications, "Notifications")
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            // 4. Tambahkan modifier .clickable ke gambar avatar
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Ganti dengan avatar
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}

// ... (Sisa fungsi Composable lainnya seperti SearchCard, CategoriesSection, dll. letakkan di sini)
// ... (Salin semua fungsi Composable dari kode sebelumnya ke sini)
// Untuk keringkasan, saya tidak menyalin ulang semua fungsi, tetapi Anda harus melakukannya.
// Salin semua fungsi dari @Composable fun SearchCard() sampai @Composable fun BottomNavigationBar() dari jawaban sebelumnya.
// Kode dari `SearchCard` sampai `BottomNavigationBar` yang ada di jawaban sebelumnya sudah benar, cukup salin ke sini.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCard() {
    // Helper to format dates
    fun getPlaceholderDate(daysToAdd: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        val dateFormat = SimpleDateFormat("EEE, MMM d, hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Find Your Perfect Ride",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Search for the best motorbikes in your area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Text Fields for dates
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Start: ${getPlaceholderDate(0)}") },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, "Start Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("End: ${getPlaceholderDate(1)}") },
                leadingIcon = { Icon(Icons.Outlined.EventAvailable, "End Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Button
            Button(
                onClick = { /* Handle search */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Search, "Search Icon", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Search", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesSection() {
    var selectedCategory by remember { mutableStateOf("All") }

    Column {
        Text(
            "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bikeCategories) { category ->
                val isSelected = category.name == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category.name },
                    label = { Text(category.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}


@Composable
fun PopularBikesSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Popular Near You",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* Handle See All */ }) {
                Text("See All", fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(popularBikes) { bike ->
                BikeCard(bike)
            }
        }
    }
}

@Composable
fun BikeCard(bike: Bike) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(200.dp)
    ) {
                Column {
                    // =================================================================
                    // BAGIAN PENTING YANG HARUS ANDA PERBAIKI ADA DI SINI
                    // Ganti Box placeholder dengan komponen Image yang sebenarnya.
                    // =================================================================
                    Image(
                        painter = painterResource(id = bike.imageRes), // Menggunakan ID gambar dari data `Bike`
                        contentDescription = bike.name, // Deskripsi untuk aksesibilitas
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop // Ini memastikan gambar memenuhi area tanpa gepeng (distorsi)
                    )

                    // Bagian bawah kartu (teks) kemungkinan sudah benar,
                    // tapi kita sertakan lagi untuk memastikan.
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = bike.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = bike.specs,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IDR ${bike.price}/day",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFC700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = bike.rating.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

@Composable
fun PromotionBanner() {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradientBrush)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Weekly Rider Deals",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "Get up to 20% off for long rentals!",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = { /* Handle View Deals */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("View Deals", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}


//@Composable
//fun BottomNavigationBar() {
//    // State untuk mengingat item mana yang sedang dipilih
//    var selectedItem by remember { mutableIntStateOf(0) }
//
//    // Daftar nama untuk setiap item navigasi
//    val items = listOf("Home", "Map", "Bookings", "Profile")
//
//    // Daftar ikon versi "Outlined" (saat tidak dipilih)
//    // FIX: Mengganti ListAlt yang usang dengan Article
//    val outlinedIcons = listOf(
//        Icons.Outlined.Home,
//        Icons.Outlined.Map,
//        Icons.Outlined.Article, // Menggantikan ListAlt
//        Icons.Outlined.Person
//    )
//
//    // Daftar ikon versi "Filled" (saat dipilih)
//    // FIX: Mengganti ListAlt yang usang dengan Article
//    val filledIcons = listOf(
//        Icons.Filled.Home,
//        Icons.Filled.Map,
//        Icons.Filled.Article, // Menggantikan ListAlt
//        Icons.Filled.Person
//    )
//
//    NavigationBar {
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                // Label teks di bawah ikon
//                label = { Text(item) },
//
//                // Cek apakah item ini sedang dipilih
//                selected = selectedItem == index,
//
//                // Aksi saat item diklik (update state)
//                onClick = { selectedItem = index },
//
//                // Logika untuk menampilkan ikon yang benar
//                icon = {
//                    // Gunakan ikon 'filled' jika terpilih, jika tidak, gunakan 'outlined'
//                    val icon = if (selectedItem == index) filledIcons[index] else outlinedIcons[index]
//                    Icon(icon, contentDescription = item)
//                }
//            )
//        }
//    }
//}


@Preview(showBackground = true, widthDp = 360, heightDp = 1200)
@Composable
fun HomeScreenPreview() {
    ScootEaseTheme {
        // Beri nilai default untuk onNavigateToProfile di preview
        HomeScreen(onNavigateToProfile = {})
    }
}