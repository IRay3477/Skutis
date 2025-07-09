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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allBikes: List<Bike>,
    bikeCount: Int,
    searchStartDate: Long,
    searchEndDate: Long,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    onBikeSelected: (bike: Bike, startDate: Long, endDate: Long) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var isSearched by remember { mutableStateOf(false) }
//    var searchStartDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
//    var searchEndDate by rememberSaveable { mutableStateOf(System.currentTimeMillis() + 86400000) }

    val listTitle = if (isSearched) "Available Bikes" else "Our Bikes"
    val displayedBikes = remember(selectedCategory, isSearched, allBikes) {
        val baseList = if (isSearched) allBikes.filter { it.status == BikeStatus.AVAILABLE } else allBikes
        when (selectedCategory) {
            "Matic" -> baseList.filter { it.type == BikeType.MATIC }
            "Manual" -> baseList.filter { it.type == BikeType.MANUAL }
            else -> baseList
        }
    }

    Scaffold(topBar = { TopHeader(bikeCount = bikeCount, onProfileClick = onNavigateToProfile) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchCard(
                    startDateMillis = searchStartDate,
                    endDateMillis = searchEndDate,
                    onStartDateChanged = onStartDateChanged,
                    onEndDateChanged = onEndDateChanged,
                    onSearchClicked = { isSearched = true }
                )
                CategoriesSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                PopularBikesSection(
                    title = listTitle,
                    bikes = displayedBikes,
                    onBikeSelected = { bike ->
                        onBikeSelected(bike, searchStartDate, searchEndDate)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader(bikeCount: Int, onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Total Bikes Loaded: $bikeCount", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, "Location", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gianyar, Bali", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
fun SearchCard(
    startDateMillis: Long,
    endDateMillis: Long,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit,
    onSearchClicked: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun formatMillisToDate(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(calendar.time)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Find Your Perfect Ride", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.clickable { showStartDatePicker = true }) {
                OutlinedTextField(value = formatMillisToDate(startDateMillis), onValueChange = {}, enabled = false, label = { Text("Tanggal Mulai") }, leadingIcon = { Icon(Icons.Outlined.CalendarMonth, "Start Date") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.clickable { showEndDatePicker = true }) {
                OutlinedTextField(value = formatMillisToDate(endDateMillis), onValueChange = {}, enabled = false, label = { Text("Tanggal Selesai") }, leadingIcon = { Icon(Icons.Outlined.EventAvailable, "End Date") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSearchClicked, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Icon(Icons.Filled.Search, "Search Icon")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Search")
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= System.currentTimeMillis() - 86400000
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onStartDateChanged(it) }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= startDateMillis
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onEndDateChanged(it) }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Matic", "Manual")
    Column {
        Text("Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }
    }
}

@Composable
fun PopularBikesSection(title: String, bikes: List<Bike>, onBikeSelected: (Bike) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(bikes) { bike ->
                BikeCard(bike = bike, onBikeSelected = onBikeSelected)
            }
        }
    }
}

@Composable
fun BikeCard(bike: Bike, onBikeSelected: (Bike) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(250.dp)
            .clickable(enabled = bike.status == BikeStatus.AVAILABLE) {
                onBikeSelected(bike)
            }
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = bike.imageRes),
                    contentDescription = bike.name,
                    modifier = Modifier.fillMaxWidth(),
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

val sampleBikesForPreview = listOf(
    Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC),
)

@Preview(showBackground = true, widthDp = 360, heightDp = 1200)
@Composable
fun HomeScreenPreview() {
    ScootEaseTheme {
        HomeScreen(
            allBikes = sampleBikesForPreview,
            bikeCount = sampleBikesForPreview.size,
            // --- TAMBAHKAN PARAMETER YANG HILANG DI SINI ---
            searchStartDate = System.currentTimeMillis(),
            searchEndDate = System.currentTimeMillis() + 86400000,
            onStartDateChanged = {},
            onEndDateChanged = {},
            // -------------------------------------------
            onNavigateToProfile = {},
            onBikeSelected = { _, _, _ -> }
        )
    }
}