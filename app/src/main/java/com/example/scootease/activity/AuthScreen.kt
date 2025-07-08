package com.example.scootease.activity

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.example.scootease.R
import com.example.scootease.helpers.DBHelper
import com.example.scootease.models.User

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
    var currentPage by remember { mutableStateOf(AuthPage.LOGIN) }
    if (currentPage == AuthPage.LOGIN) {
        LoginScreen(
            onSwitchToRegister = { currentPage = AuthPage.REGISTER },
            onLoginSuccess = onAuthSuccess
        )
    } else {
        RegisterScreen(
            onSwitchToLogin = { currentPage = AuthPage.LOGIN },
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

    // --- TAMBAHAN: Dapatkan context dan inisialisasi DBHelper ---
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (UI lainnya sama seperti sebelumnya)
        Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo Aplikasi", modifier = Modifier.size(120.dp).padding(bottom = 16.dp))
        Text(text = "Selamat Datang Kembali", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Login untuk melanjutkan petualanganmu di Bali!", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Email, "Email Icon") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, if (passwordVisible) "Sembunyikan" else "Tampilkan") }
        })
        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login
        Button(
            onClick = {
                // --- TAMBAHAN: Logika untuk Login ---
                if (email.isNotBlank() && password.isNotBlank()) {
                    val user = dbHelper.login(email, password)
                    if (user != null) {
                        // Jika login berhasil
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess() // Panggil callback untuk navigasi
                    } else {
                        // Jika login gagal
                        Toast.makeText(context, "Email atau password salah.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika ada field yang kosong
                    Toast.makeText(context, "Harap isi semua kolom.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun?")
            TextButton(onClick = onSwitchToRegister) { Text("Daftar di sini") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onSwitchToLogin: () -> Unit,
    // PERUBAHAN DI SINI: Tambahkan parameter onRegisterSuccess
    onRegisterSuccess: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (UI Register sama seperti sebelumnya)
        Text(text = "Buat Akun Baru", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Satu langkah lagi untuk mulai menjelajah.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Person, "Nama Icon") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Email, "Email Icon") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, "Toggle") }
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Konfirmasi Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") }, visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), trailingIcon = {
            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(imageVector = image, "Toggle") }
        })
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Harap isi semua kolom.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Password tidak cocok.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (dbHelper.isEmailExists(email)) {
                    Toast.makeText(context, "Email sudah terdaftar.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val user = User(username = fullName, email = email, password = password)
                val success = dbHelper.registerUser(user)
                if (success) {
                    // PERUBAHAN DI SINI: Panggil onRegisterSuccess
                    Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_LONG).show()
                    onRegisterSuccess() // Langsung navigasi ke halaman utama
                } else {
                    Toast.makeText(context, "Registrasi gagal, coba lagi.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Daftar", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Sudah punya akun?")
            TextButton(onClick = onSwitchToLogin) { Text("Login di sini") }
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
