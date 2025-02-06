package com.application.dmaps.feat_auth.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.application.dmaps.feat_auth.presentation.viewmodel.LoginScreenIncomingEvent
import com.application.dmaps.feat_auth.presentation.viewmodel.LoginScreenOutgoingEvent
import com.application.dmaps.feat_auth.presentation.viewmodel.LoginViewModel
import com.application.dmaps.feat_core.presentation.navigation.Screen
import com.application.dmaps.feat_core.utils.snackbar.SnackbarController
import com.application.dmaps.feat_core.utils.snackbar.SnackbarEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: LoginViewModel = hiltViewModel()
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current


    LaunchedEffect(key1 = true) {
        viewModel.eventFLow.collectLatest { event ->
            when (event) {
                is LoginScreenOutgoingEvent.LoginSuccess -> {
                    navController.navigate(Screen.MapHome.route){
                        popUpTo(Screen.Login.route){inclusive = true}
                    }
                }

                is LoginScreenOutgoingEvent.ShowSnackBar -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = event.message))
                }
            }
        }
    }
    LaunchedEffect(key1 = isPasswordVisible.value) {
        if (isPasswordVisible.value) {
            delay(2000)
            isPasswordVisible.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
            imageVector = Icons.Default.MapsHomeWork,
            contentDescription = "App Logo"
        )
        Text(
            text = "Welcome To Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(80.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 40.dp),
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person, // Email icon
                    contentDescription = "Username Icon"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Shows the "Next" button on the keyboard
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 40.dp),
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock, // Email icon
                    contentDescription = "Email Icon"
                )
            },
            trailingIcon = {
                val icon =
                    if (isPasswordVisible.value) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val description = if (isPasswordVisible.value) "Hide Password" else "Show Password"
                Icon(imageVector = icon, contentDescription = description,
                    modifier = Modifier.clickable {
                        isPasswordVisible.value = !isPasswordVisible.value
                    }
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done // Shows the "Next" button on the keyboard
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.onEvent(
                    LoginScreenIncomingEvent.OnLoginClicked(
                        username.value,
                        password.value
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login", fontSize = 20.sp)
        }
    }
}