package com.example.scootease

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.ui.theme.ScootEaseTheme
import androidx.compose.material.icons.filled.* // Tanda * akan mengimpor semua ikon Filled
import androidx.compose.material.icons.outlined.*

/**
 * PENTING:
 * Untuk menggunakan sistem navigasi ini, buka file MainActivity.kt Anda
 * dan ubah isinya untuk memanggil `ScootEaseApp()` seperti di bawah ini:
 *
 * class MainActivity : ComponentActivity() {
 * override fun onCreate(savedInstanceState: Bundle?) {
 * super.onCreate(savedInstanceState)
 * setContent {
 * ScootEaseTheme {
 * // Panggil ScootEaseApp, bukan AuthScreen atau HomeScreen secara langsung
 * ScootEaseApp()
 * }
 * }
 * }
 * }
 */
@Composable
fun ScootEaseApp() {
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isAuthenticated) {
            // Berikan aksi logout ke MainScreen
            MainScreen(onLogout = { isAuthenticated = false })
        } else {
            AuthScreen(onAuthSuccess = { isAuthenticated = true })
        }
    }
}

// 1. MainScreen sekarang menerima parameter onLogout
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf("Home", "Map", "Bookings", "Profile")
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Map, Icons.Outlined.Article, Icons.Outlined.Person)
                val filledIcons = listOf(Icons.Filled.Home, Icons.Filled.Map, Icons.Filled.Article, Icons.Filled.Person)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            val icon = if (selectedItem == index) filledIcons[index] else icons[index]
                            Icon(icon, contentDescription = item)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                // 2. Berikan aksi navigasi ke profil dari HomeScreen
                0 -> HomeScreen(onNavigateToProfile = { selectedItem = 3 })
                1 -> MapScreen(onNavigateBack = { selectedItem = 0 })
                2 -> CenterText(text = "Halaman Booking")
                // 3. Tampilkan ProfileScreen dan berikan aksi logout
                3 -> ProfileScreen(onLogoutClick = onLogout)
            }
        }
    }
}

@Composable
fun CenterText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.headlineMedium)
    }
}


// Enum untuk menentukan halaman mana yang ditampilkan
private enum class AuthPage {
    LOGIN,
    REGISTER
}

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    // State untuk mengontrol halaman mana yang aktif (Login atau Register)
    var currentPage by remember { mutableStateOf(AuthPage.LOGIN) }

    if (currentPage == AuthPage.LOGIN) {
        LoginScreen(
            onSwitchToRegister = { currentPage = AuthPage.REGISTER },
            // Teruskan callback onAuthSuccess ke LoginScreen
            onLoginSuccess = onAuthSuccess
        )
    } else {
        RegisterScreen(
            onSwitchToLogin = { currentPage = AuthPage.LOGIN },
            // Teruskan callback onAuthSuccess ke RegisterScreen
            onRegisterSuccess = onAuthSuccess
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSwitchToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Membuat kolom bisa di-scroll
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo atau Gambar
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Ganti dengan logo Anda
            contentDescription = "Logo Aplikasi",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Selamat Datang Kembali",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Login untuk melanjutkan petualanganmu di Bali!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login
        Button(
            // Panggil onLoginSuccess saat tombol diklik
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }

        // Tombol untuk ke halaman Register
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Belum punya akun?")
            TextButton(onClick = onSwitchToRegister) {
                Text("Daftar di sini")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onSwitchToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Buat Akun Baru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Satu langkah lagi untuk mulai menjelajah.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Input Nama Lengkap
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama Icon") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, "Toggle Password Visibility")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Konfirmasi Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, "Toggle Password Visibility")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Daftar
        Button(
            // Panggil onRegisterSuccess saat tombol diklik
            onClick = onRegisterSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Daftar", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }

        // Tombol untuk ke halaman Login
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sudah punya akun?")
            TextButton(onClick = onSwitchToLogin) {
                Text("Login di sini")
            }
        }
    }
}

// Preview untuk memudahkan desain
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ScootEaseTheme {
        LoginScreen(onSwitchToRegister = {}, onLoginSuccess = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ScootEaseTheme {
        RegisterScreen(onSwitchToLogin = {}, onRegisterSuccess = {})
    }
}
