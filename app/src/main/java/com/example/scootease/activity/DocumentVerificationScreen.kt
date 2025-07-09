package com.example.scootease.activity

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.ui.theme.ScootEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVerificationScreen(onNavigateBack: () -> Unit) {
    var ktpUri by remember { mutableStateOf<Uri?>(null) }
    var simUri by remember { mutableStateOf<Uri?>(null) }

    val ktpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        ktpUri = uri
    }
    val simPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        simUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Verification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Please upload your documents to complete verification. This is a one-time process.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Upload KTP
            UploadBox(
                title = "Upload National ID (KTP)",
                fileName = ktpUri?.lastPathSegment,
                onClick = { ktpPickerLauncher.launch("image/*") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Upload SIM
            UploadBox(
                title = "Upload Driving License (SIM)",
                fileName = simUri?.lastPathSegment,
                onClick = { simPickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* TODO: Handle submission logic */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = ktpUri != null && simUri != null // Tombol aktif jika kedua file sudah dipilih
            ) {
                Text("Submit for Verification", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UploadBox(title: String, fileName: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = fileName ?: "No file chosen",
            style = MaterialTheme.typography.bodySmall,
            color = if (fileName != null) Color(0xFF43A047) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentVerificationScreenPreview() {
    ScootEaseTheme {
        DocumentVerificationScreen(onNavigateBack = {})
    }
}