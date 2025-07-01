package com.mercu.intrain.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercu.intrain.MainActivity
import com.mercu.intrain.R
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.theme.intrainPrimary
import com.spr.jetpack_loading.components.indicators.BallSpinFadeLoaderIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onRegisterClick = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sharedPrefHelper = remember { SharedPrefHelper(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Collect state from ViewModel
    val loginState by viewModel.loginState.collectAsState()
    var isTransitioning by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    // Handle state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                isTransitioning = true
                delay(1000)
                val successMessage = (loginState as LoginState.Success).message
                snackbarHostState.showSnackbar(successMessage)
            }
            is LoginState.Error -> {
                val error = (loginState as LoginState.Error).message
                snackbarHostState.showSnackbar(error)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    LaunchedEffect(isTransitioning) {
        if (isTransitioning) {
            onLoginSuccess()
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Username",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter username") },
                        isError = usernameError.isNotEmpty(),
                        supportingText = { if (usernameError.isNotEmpty()) Text(usernameError) }
                    )

                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordError.isNotEmpty(),
                        supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) }
                    )

                    Button(
                        onClick = {
                            // Validation
                            var valid = true
                            if (username.isEmpty()) {
                                usernameError = "Username is required"
                                valid = false
                            }
                            if (password.isEmpty()) {
                                passwordError = "Password is required"
                                valid = false
                            }

                            if (valid) {
                                scope.launch {
                                    viewModel.login(
                                        username = username,
                                        password = password,
                                        sharedPrefHelper = sharedPrefHelper
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text("Login")
                        }
                    }




                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onRegisterClick) {
                            Text(
                                text = "Register",
                                textDecoration = TextDecoration.Underline
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        TextButton(onClick = { /* Handle forgot password */ }) {
                            Text(
                                text = "Forgot Password",
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }


        }

        if (isTransitioning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(intrainPrimary),
                contentAlignment = Alignment.Center
            ) {
                BallSpinFadeLoaderIndicator(
                    color = Color.White
                )
            }
        }
    }
}
