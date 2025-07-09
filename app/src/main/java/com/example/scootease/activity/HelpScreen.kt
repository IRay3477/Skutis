package com.example.scootease.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.ui.theme.ScootEaseTheme

data class FaqItem(val question: String, val answer: String)

val faqList = listOf(
    FaqItem("How do I rent a scooter?", "Find an available scooter on the home screen or map, select your rental dates, and proceed to the booking confirmation page. It's that simple!"),
    FaqItem("What documents are required?", "You will need to upload a clear picture of your valid national ID (KTP for locals, Passport for foreigners) and a valid driving license (SIM C or an international equivalent)."),
    FaqItem("What is the payment process?", "All payments are handled securely within the app. We accept major credit cards and popular e-wallets."),
    FaqItem("What if the scooter breaks down?", "In the rare case of a breakdown, please use the 'Help' section in your profile to contact our 24/7 support line immediately. We will arrange for assistance or a replacement vehicle as soon as possible."),
    FaqItem("Can I extend my rental period?", "Yes, you can request a rental extension through the 'My Bookings' page, provided the scooter is available for the extended period.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center (FAQ)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(faqList) { faq ->
                Text(faq.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(faq.answer, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    ScootEaseTheme {
        HelpScreen(onNavigateBack = {})
    }
}