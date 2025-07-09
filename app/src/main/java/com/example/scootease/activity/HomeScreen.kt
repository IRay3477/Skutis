package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.scootease.R
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType

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
    Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(3, "Honda Scoopy", "110cc · Auto", "75k", 4.9, R.drawable.honda_scoopy, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(4, "Honda PCX", "150cc · Auto", "150k", 4.7, R.drawable.honda_pcx, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(5, "Harley Sporster 48", "1200cc · Manual", "2000k", 4.6, R.drawable.harley_48, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(6, "BMW R 1200 GS", "1200cc · Manual", "5000k", 4.5, R.drawable.bmw_r1200gs, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(7, "Harley Road Glide", "1800cc · Manual", "5500k", 4.5, R.drawable.harley_rg, BikeStatus.AVAILABLE, BikeType.MANUAL)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allBikes: List<Bike>,
    onNavigateToProfile: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var isSearched by remember { mutableStateOf(false) }

    // Judul sekarang ditentukan secara reaktif berdasarkan state isSearched
    val listTitle = if (isSearched) "Available Bikes" else "Our Bikes"

    val displayedBikes = remember(selectedCategory, isSearched, allBikes) {
        // Tentukan daftar dasar: jika sudah dicari, mulai dari yang tersedia saja.
        val baseList = if (isSearched) {
            allBikes.filter { it.status == BikeStatus.AVAILABLE }
        } else {
            allBikes
        }

        // Filter lebih lanjut berdasarkan kategori dari daftar dasar tersebut.
        when (selectedCategory) {
            "Matic" -> baseList.filter { it.type == BikeType.MATIC }
            "Manual" -> baseList.filter { it.type == BikeType.MANUAL }
            else -> baseList // Untuk "All", tampilkan daftar dasar apa adanya.
        }
    }

    Scaffold(
        topBar = { TopHeader(onProfileClick = onNavigateToProfile) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchCard(
                    onSearchClicked = { startDate, endDate ->
                        isSearched = true
                    }
                )
                CategoriesSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                    }
                )
                PopularBikesSection(title = listTitle, bikes = displayedBikes)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCard(onSearchClicked: (startDateMillis: Long?, endDateMillis: Long?) -> Unit) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDateMillis by rememberSaveable { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var selectedEndDateMillis by rememberSaveable { mutableStateOf<Long?>(System.currentTimeMillis() + 86400000) }

    fun formatMillisToDate(millis: Long?): String {
        if (millis == null) return "Choose date"
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(calendar.time)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Find Your Perfect Ride", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.clickable { showStartDatePicker = true }) {
                OutlinedTextField(
                    value = formatMillisToDate(selectedStartDateMillis),
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Starting Date") },
                    leadingIcon = { Icon(Icons.Outlined.CalendarMonth, "Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.clickable { showEndDatePicker = true }) {
                OutlinedTextField(
                    value = formatMillisToDate(selectedEndDateMillis),
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Ending Date") },
                    leadingIcon = { Icon(Icons.Outlined.EventAvailable, "End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSearchClicked(selectedStartDateMillis, selectedEndDateMillis) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Search, "Search Icon")
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Search", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedStartDateMillis)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedStartDateMillis = datePickerState.selectedDateMillis
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedEndDateMillis)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedEndDateMillis = datePickerState.selectedDateMillis
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    // Daftar kategori yang akan ditampilkan
    val categories = listOf("All", "Matic", "Manual")

    Column {
        Text("Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }, // Panggil callback saat diklik
                    label = { Text(category) }
                )
            }
        }
    }
}


@Composable
fun PopularBikesSection(title: String, bikes: List<Bike>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, // Gunakan judul dari parameter
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

        }
        Spacer(modifier = Modifier.height(4.dp))
        // Tampilkan daftar motor dari parameter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bikes) { bike ->
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
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = bike.imageRes),
                    contentDescription = bike.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (bike.status == BikeStatus.AVAILABLE) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                        .border(1.dp, Color.White, CircleShape)
                )
            }
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
                        "IDR ${bike.price}/day",
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

//@Composable
//fun PromotionBanner() {
//    val gradientBrush = Brush.horizontalGradient(
//        colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
//    )
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//            .background(brush = gradientBrush)
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    "Weekly Rider Deals",
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.titleLarge
//                )
//                Text(
//                    "Get up to 20% off for long rentals!",
//                    color = Color.White.copy(alpha = 0.8f),
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//            Button(
//                onClick = { /* Handle View Deals */ },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White,
//                    contentColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text("View Deals", fontWeight = FontWeight.Bold, fontSize = 12.sp)
//            }
//        }
//    }
//}


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
        HomeScreen(
            allBikes = popularBikes,
            onNavigateToProfile = {}
        )
    }
}