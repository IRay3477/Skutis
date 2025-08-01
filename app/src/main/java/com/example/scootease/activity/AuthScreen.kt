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
import com.example.scootease.services.SessionManager




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
fun AuthScreen(onAuthSuccess: (user: User) -> Unit) {
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
    // PERBAIKAN: Tipe data disesuaikan menjadi (User) -> Unit
    onLoginSuccess: (user: User) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ic_launcher_round), contentDescription = "Logo Aplikasi", modifier = Modifier.size(120.dp).padding(bottom = 16.dp))
        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Login to continue your journey!", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Email, "Email Icon") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, if (passwordVisible) "Sembunyikan" else "Tampilkan") }
        })
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    val user = dbHelper.login(email, password)
                    if (user != null) {
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        // PERBAIKAN: Kirim seluruh objek User saat login sukses
                        onLoginSuccess(user)
                    } else {
                        Toast.makeText(context, "Email atau password salah.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Harap isi semua kolom.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Login", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account yet?")
            TextButton(onClick = onSwitchToRegister) { Text("Register now") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onSwitchToLogin: () -> Unit,
    // PERBAIKAN: Tipe data disesuaikan menjadi (User) -> Unit
    onRegisterSuccess: (user: User) -> Unit
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
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_round), // Ganti dengan nama file logo Anda
            contentDescription = "Logo Scoot Ease",
            modifier = Modifier
                .height(80.dp) // Dibuat sedikit lebih kecil
                .padding(bottom = 24.dp)
        )

        Text(text = "Create New Account", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "One more step to start exploring.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Person, "Nama Icon") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Email, "Email Icon") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") },
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password"
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (dbHelper.isEmailExists(email)) {
                    Toast.makeText(context, "Email is already registered.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val user = User(username = fullName, email = email, password = password)
                val success = dbHelper.registerUser(user)
                if (success) {
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                    // PERBAIKAN: Kirim seluruh objek User yang baru dibuat
                    onRegisterSuccess(user)
                } else {
                    Toast.makeText(context, "Registration failed, please try again.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Register", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?")
            TextButton(onClick = onSwitchToLogin) { Text("Login") }
        }
    }
}

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