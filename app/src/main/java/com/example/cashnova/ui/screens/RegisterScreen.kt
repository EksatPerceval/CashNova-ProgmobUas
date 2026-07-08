package com.example.cashnova.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/*
 * Layar pendaftaran pengguna (Register).
 * Mengambil input username, PIN, dan konfirmasi PIN.
 * Setelah berhasil mendaftar, menampilkan Snackbar sukses lalu otomatis pindah ke Login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onRegisterAction: (String, String, () -> Unit, (String) -> Unit) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var registrationDone by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    /*
     * Setelah registrasi berhasil di ViewModel, tampilkan Snackbar sukses,
     * lalu navigasi ke Login. LaunchedEffect memastikan ini berjalan
     * di main thread secara urut (tunggu Snackbar selesai dulu).
     */
    LaunchedEffect(registrationDone) {
        if (registrationDone) {
            snackbarHostState.showSnackbar(
                message = "Akun berhasil dibuat! Silakan login.",
                duration = SnackbarDuration.Short
            )
            onRegisterSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join CashNova",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Track your expenses easily",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPin,
                onValueChange = { confirmPin = it },
                label = { Text("Confirm PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    if (pin != confirmPin) {
                        scope.launch {
                            snackbarHostState.showSnackbar("PIN and Confirm PIN must match")
                        }
                    } else {
                        onRegisterAction(
                            username,
                            pin,
                            { registrationDone = true } // Set flag untuk LaunchedEffect
                        ) { err ->
                            scope.launch {
                                snackbarHostState.showSnackbar(err)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Register",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
