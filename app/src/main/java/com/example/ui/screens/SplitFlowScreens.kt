package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.domain.SimplifiedDebt
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    onNavigateJudgesDemo: () -> Unit,
    onNavigateDashboard: () -> Unit
) {
    val activeUser by authViewModel.activeUser.collectAsState()
    var nameInput by remember { mutableStateOf("") }
    var isUserModeExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FintechBgDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // SettleX Fintech Brand Logo / Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Brush.linearGradient(listOf(FintechPrimary, FintechSecondary)))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(FintechBgDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = "SettleX Brand Icon",
                        tint = FintechPrimary,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SettleX",
                    color = FintechTextPrimaryDark,
                    style = Typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Elite Fintech Split Ledger & Expense Hub",
                    color = FintechTextSecondaryDark,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Card Option 1: Judge Sandbox Mode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        dashboardViewModel.setJudgeMode(true)
                        onNavigateJudgesDemo()
                    },
                colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, FintechSecondary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = FintechSecondary)
                            Text("JUDGE SANDBOX MODE", color = FintechSecondary, fontWeight = FontWeight.Bold, style = Typography.titleMedium)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FintechSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("DEMO DATA", color = FintechSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Instant access to a fully populated, production-ready environment featuring automated UPI debt simplify runs, AI summaries, and evaluation tools.",
                        color = FintechTextSecondaryDark,
                        style = Typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            dashboardViewModel.setJudgeMode(true)
                            onNavigateJudgesDemo()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FintechSecondary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Enter Judge Sandbox", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Card Option 2: User Mode
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, if (isUserModeExpanded) FintechPrimary else FintechTextSecondaryDark.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FintechPrimary)
                            Text("USER LEDGER MODE", color = FintechPrimary, fontWeight = FontWeight.Bold, style = Typography.titleMedium)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FintechPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("FRESH", color = FintechPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Create actual splitting spaces with personal ledgers, custom groups, real receipt scans, and direct payment tracking.",
                        color = FintechTextSecondaryDark,
                        style = Typography.bodyMedium
                    )

                    if (!isUserModeExpanded) {
                        Button(
                            onClick = {
                                if (activeUser != null && activeUser!!.name.isNotEmpty()) {
                                    dashboardViewModel.setJudgeMode(false)
                                    onNavigateDashboard()
                                } else {
                                    isUserModeExpanded = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FintechPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(if (activeUser != null && activeUser!!.name.isNotEmpty()) "Resume Fresh Session" else "Start Fresh Ledger", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("What is your name?") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FintechPrimary,
                                    unfocusedBorderColor = FintechTextSecondaryDark,
                                    focusedTextColor = FintechTextPrimaryDark,
                                    unfocusedTextColor = FintechTextPrimaryDark
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("user_mode_name_input"),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (nameInput.trim().isNotEmpty()) {
                                        authViewModel.enterUserMode(nameInput.trim())
                                        dashboardViewModel.setJudgeMode(false)
                                        onNavigateDashboard()
                                    }
                                },
                                enabled = nameInput.trim().isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = FintechPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Launch User Mode", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

enum class AuthMode { LOGIN, SIGN_UP, FORGOT_PASSWORD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateDashboard: () -> Unit,
    onNavigateProfileSetup: () -> Unit
) {
    val context = LocalContext.current
    val activeUser by viewModel.activeUser.collectAsState()
    val isProgress by viewModel.isProgress.collectAsState()
    val authError by viewModel.authError.collectAsState()

    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(activeUser) {
        if (activeUser != null) {
            if (activeUser!!.name.isEmpty()) {
                onNavigateProfileSetup()
            } else {
                onNavigateDashboard()
            }
        }
    }

    LaunchedEffect(authError) {
        authError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FintechBgDark, FintechSurfDark)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // SettleX Logo
            Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = "SettleX Logo",
                tint = FintechPrimary,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "SettleX",
                color = FintechTextPrimaryDark,
                style = Typography.displayLarge,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Premium Fintech Debt-Splitting",
                color = FintechSecondary,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Glassmorphic Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassBg)
                    .border(1.dp, GlassStroke, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (authMode) {
                            AuthMode.LOGIN -> "Welcome Back"
                            AuthMode.SIGN_UP -> "Create Account"
                            AuthMode.FORGOT_PASSWORD -> "Reset Password"
                        },
                        style = Typography.headlineMedium,
                        color = FintechTextPrimaryDark
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FintechPrimary,
                            unfocusedBorderColor = FintechTextSecondaryDark,
                            focusedTextColor = FintechTextPrimaryDark,
                            unfocusedTextColor = FintechTextPrimaryDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        singleLine = true
                    )

                    if (authMode == AuthMode.SIGN_UP) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FintechPrimary,
                                unfocusedBorderColor = FintechTextSecondaryDark,
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input"),
                            singleLine = true
                        )
                    }

                    if (authMode != AuthMode.FORGOT_PASSWORD) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = FintechSecondary
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FintechPrimary,
                                unfocusedBorderColor = FintechTextSecondaryDark,
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            when (authMode) {
                                AuthMode.LOGIN -> viewModel.signIn(email, name.ifEmpty { "User" })
                                AuthMode.SIGN_UP -> {
                                    if (name.isBlank()) {
                                        Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.signUp(email, name, "", "")
                                }
                                AuthMode.FORGOT_PASSWORD -> {
                                    Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                                    authMode = AuthMode.LOGIN
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FintechPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_button")
                    ) {
                        if (isProgress) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = when (authMode) {
                                    AuthMode.LOGIN -> "Login"
                                    AuthMode.SIGN_UP -> "Register"
                                    AuthMode.FORGOT_PASSWORD -> "Send Reset Code"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                authMode = if (authMode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
                            }
                        ) {
                            Text(
                                text = if (authMode == AuthMode.LOGIN) "Create account" else "Have account? Login",
                                color = FintechSecondary
                            )
                        }

                        if (authMode == AuthMode.LOGIN) {
                            TextButton(onClick = { authMode = AuthMode.FORGOT_PASSWORD }) {
                                Text("Forgot?", color = FintechTextSecondaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: AuthViewModel,
    onNavigateDashboard: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    LaunchedEffect(activeUser) {
        activeUser?.let {
            if (name.isEmpty()) name = it.name
            if (phone.isEmpty()) phone = it.phone
            if (upiId.isEmpty()) upiId = it.upiId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Setup", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FintechBgDark,
                    titleContentColor = FintechTextPrimaryDark
                )
            )
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Complete your profiles info to get started splitting expenses securely with SettleX.",
                color = FintechTextSecondaryDark,
                style = Typography.bodyMedium
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FintechPrimary,
                    unfocusedBorderColor = FintechTextSecondaryDark,
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_setup"),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FintechPrimary,
                    unfocusedBorderColor = FintechTextSecondaryDark,
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_setup"),
                singleLine = true
            )

            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID (for easy settlement payments)") },
                placeholder = { Text("username@upi") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FintechPrimary,
                    unfocusedBorderColor = FintechTextSecondaryDark,
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upi_setup"),
                singleLine = true
            )

            Button(
                onClick = {
                    viewModel.updateProfile(name, phone, upiId)
                    onNavigateDashboard()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FintechPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button")
            ) {
                Text("Start SettleX", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun AnimatedCountUpText(
    targetValue: Double,
    modifier: Modifier = Modifier
) {
    var currentValue by remember { mutableStateOf(0.0) }

    LaunchedEffect(targetValue) {
        val steps = 20
        val difference = targetValue - currentValue
        val stepSize = difference / steps
        for (i in 1..steps) {
            currentValue += stepSize
            delay(15)
        }
        currentValue = targetValue
    }

    Text(
        text = "₹${String.format("%.2f", currentValue)}",
        style = Typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = if (targetValue >= 0) FintechGreen else FintechRed,
        modifier = modifier
    )
}

@Composable
fun JudgeModeBanner(
    isJudgeMode: Boolean,
    onResetDemo: () -> Unit,
    onExitJudgeMode: () -> Unit
) {
    if (!isJudgeMode) return
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FintechSecondary.copy(alpha = 0.12f))
            .border(1.dp, FintechSecondary.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = FintechSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "⚖️ JUDGE DEMO MODE — Evaluators Sandbox",
                    color = FintechSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset Demo",
                    color = FintechSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clickable { onResetDemo() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Demo Mode",
                    tint = FintechTextSecondaryDark,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onExitJudgeMode() }
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    onNavigateGroupDetail: (String) -> Unit,
    onNavigateAddExpense: (String) -> Unit,
    onNavigateJoinGroup: () -> Unit,
    onNavigateJudgesDemo: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val activeUser by authViewModel.activeUser.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = FintechSurfDark,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    Triple("Home", Icons.Default.Home, 0),
                    Triple("Groups", Icons.Default.Group, 1),
                    Triple("Expenses", Icons.Default.ReceiptLong, 2),
                    Triple("Reports", Icons.Default.TrendingUp, 3),
                    Triple("Profile", Icons.Default.Person, 4)
                )

                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FintechPrimary,
                            unselectedIconColor = FintechTextSecondaryDark,
                            selectedTextColor = FintechPrimary,
                            unselectedTextColor = FintechTextSecondaryDark,
                            indicatorColor = GlassBg
                        )
                    )
                }
            }
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isJudgeMode by dashboardViewModel.isJudgeMode.collectAsState()
            val context = LocalContext.current
            JudgeModeBanner(
                isJudgeMode = isJudgeMode,
                onResetDemo = {
                    dashboardViewModel.resetDemo()
                    Toast.makeText(context, "Demo reset successfully!", Toast.LENGTH_SHORT).show()
                },
                onExitJudgeMode = {
                    dashboardViewModel.setJudgeMode(false)
                    Toast.makeText(context, "Exited Judge Mode", Toast.LENGTH_SHORT).show()
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> HomeScreenContent(
                        dashboardViewModel = dashboardViewModel,
                        activeUser = activeUser,
                        onNavigateGroupDetail = onNavigateGroupDetail,
                        onNavigateJudgesDemo = onNavigateJudgesDemo
                    )
                    1 -> GroupsTabContent(
                        dashboardViewModel = dashboardViewModel,
                        onNavigateGroupDetail = onNavigateGroupDetail,
                        onNavigateJoinGroup = onNavigateJoinGroup
                    )
                    2 -> ExpensesTabContent(
                        dashboardViewModel = dashboardViewModel
                    )
                    3 -> ReportsTabContent(
                        dashboardViewModel = dashboardViewModel
                    )
                    4 -> ProfileTabContent(
                        authViewModel = authViewModel,
                        onSignOut = {
                            authViewModel.signOut()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    dashboardViewModel: DashboardViewModel,
    activeUser: UserEntity?,
    onNavigateGroupDetail: (String) -> Unit,
    onNavigateJudgesDemo: () -> Unit
) {
    val groups by dashboardViewModel.groups.collectAsState()
    val activities by dashboardViewModel.activities.collectAsState()
    val netBalance by dashboardViewModel.netBalance.collectAsState()
    val totalOwed by dashboardViewModel.totalOwed.collectAsState()
    val totalToReceive by dashboardViewModel.totalToReceive.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${activeUser?.name ?: "User"}",
                        color = FintechTextPrimaryDark,
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fintech Split Hub",
                        color = FintechTextSecondaryDark,
                        style = Typography.labelSmall
                    )
                }

                Button(
                    onClick = onNavigateJudgesDemo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechSecondary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sandbox Demo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            // Main Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FintechSurfDark, FintechSurfDarkElevated)
                        )
                    )
                    .border(1.dp, GlassStroke, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "YOUR NET BALANCE",
                        color = FintechTextSecondaryDark,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )

                    AnimatedCountUpText(
                        targetValue = netBalance,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("You owe", color = FintechTextSecondaryDark, fontSize = 12.sp)
                            Text(
                                "₹${String.format("%.2f", totalOwed)}",
                                color = FintechRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(GlassStroke)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("You receive", color = FintechTextSecondaryDark, fontSize = 12.sp)
                            Text(
                                "₹${String.format("%.2f", totalToReceive)}",
                                color = FintechGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "YOUR GROUPS",
                color = FintechTextPrimaryDark,
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (groups.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No groups created yet. Create or join groups in the Groups tab!",
                        color = FintechTextSecondaryDark,
                        style = Typography.bodyMedium
                    )
                }
            }
        } else {
            items(groups.take(3)) { group ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateGroupDetail(group.id) },
                    colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GlassBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = FintechPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = group.name,
                                    color = FintechTextPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = group.description,
                                    color = FintechTextSecondaryDark,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open group",
                            tint = FintechSecondary
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "RECENT ACTIVITIES",
                color = FintechTextPrimaryDark,
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (activities.isEmpty()) {
            item {
                Text(
                    "No activities logs found.",
                    color = FintechTextSecondaryDark,
                    style = Typography.bodyMedium
                )
            }
        } else {
            items(activities.take(5)) { activity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (activity.type) {
                            "EXPENSE" -> Icons.Default.Receipt
                            "SETTLEMENT" -> Icons.Default.CheckCircle
                            "GROUP" -> Icons.Default.AddHomeWork
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = when (activity.type) {
                            "EXPENSE" -> FintechOrange
                            "SETTLEMENT" -> FintechGreen
                            else -> FintechSecondary
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 12.dp)
                    )

                    Column {
                        Text(
                            text = activity.title,
                            color = FintechTextPrimaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = activity.description,
                            color = FintechTextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsTabContent(
    dashboardViewModel: DashboardViewModel,
    onNavigateGroupDetail: (String) -> Unit,
    onNavigateJoinGroup: () -> Unit
) {
    val groups by dashboardViewModel.groups.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }
    var groupPasscode by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Split Groups",
                    style = Typography.displayMedium,
                    color = FintechTextPrimaryDark
                )

                IconButton(onClick = onNavigateJoinGroup) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = "Join Group",
                        tint = FintechSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(groups) { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateGroupDetail(group.id) },
                        colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = group.name,
                                    color = FintechTextPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = group.description,
                                    color = FintechTextSecondaryDark,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = FintechSecondary
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = FintechPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("create_group_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Group", fontWeight = FontWeight.Bold)
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create SettleX Group", color = FintechTextPrimaryDark) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Group Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = groupDesc,
                            onValueChange = { groupDesc = it },
                            label = { Text("Description") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = groupPasscode,
                            onValueChange = { groupPasscode = it },
                            label = { Text("Group Passcode (4-digit)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            label = { Text("Group UPI ID (optional)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (groupName.isNotBlank() && groupPasscode.isNotBlank()) {
                                dashboardViewModel.createGroup(groupName, groupDesc, groupPasscode, upiId)
                                showCreateDialog = false
                                groupName = ""
                                groupDesc = ""
                                groupPasscode = ""
                                upiId = ""
                            }
                        }
                    ) {
                        Text("Create", color = FintechPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = FintechTextSecondaryDark)
                    }
                },
                containerColor = FintechSurfDark
            )
        }
    }
}

@Composable
fun ExpensesTabContent(
    dashboardViewModel: DashboardViewModel
) {
    val allExpenses by dashboardViewModel.allExpenses.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Expense Tracker",
            style = Typography.displayMedium,
            color = FintechTextPrimaryDark
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (allExpenses.isEmpty()) {
                item {
                    Text("No expenses logged yet.", color = FintechTextSecondaryDark)
                }
            } else {
                items(allExpenses) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GlassBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.expense.category) {
                                            "Food" -> Icons.Default.Restaurant
                                            "Transport" -> Icons.Default.DirectionsCar
                                            "Utilities" -> Icons.Default.Bolt
                                            "Entertainment" -> Icons.Default.LocalPlay
                                            else -> Icons.Default.Receipt
                                        },
                                        contentDescription = null,
                                        tint = FintechSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.expense.title, color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                                    Text(item.groupName, color = FintechTextSecondaryDark, fontSize = 12.sp)
                                }
                            }
                            Text(
                                "₹${String.format("%.2f", item.expense.amount)}",
                                color = FintechPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsTabContent(
    dashboardViewModel: DashboardViewModel
) {
    val allExpenses by dashboardViewModel.allExpenses.collectAsState()

    // Aggregate expense by category
    val categoryTotals = remember(allExpenses) {
        val map = mutableMapOf("Food" to 0.0, "Transport" to 0.0, "Utilities" to 0.0, "Entertainment" to 0.0, "Other" to 0.0)
        allExpenses.forEach {
            val cat = it.expense.category
            map[cat] = (map[cat] ?: 0.0) + it.expense.amount
        }
        map
    }

    val totalSpent = categoryTotals.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Fintech Analytics",
            style = Typography.displayMedium,
            color = FintechTextPrimaryDark
        )

        // Custom Stacked Canvas Progress Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(FintechSurfDark)
                .border(1.dp, GlassStroke, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.height * 0.4f
                var startAngle = -90f

                val colors = listOf(
                    FintechPrimary,
                    FintechSecondary,
                    FintechTeritary,
                    FintechOrange,
                    FintechRed
                )

                if (totalSpent == 0.0) {
                    drawCircle(
                        color = GlassStroke,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 30f)
                    )
                } else {
                    categoryTotals.entries.forEachIndexed { index, entry ->
                        val sweepAngle = ((entry.value / totalSpent) * 360f).toFloat()
                        drawArc(
                            color = colors.getOrElse(index) { Color.Gray },
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = 30f)
                        )
                        startAngle += sweepAngle
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TOTAL SPENT", color = FintechTextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("₹${String.format("%.2f", totalSpent)}", color = FintechPrimary, style = Typography.headlineMedium, fontWeight = FontWeight.Black)
            }
        }

        // List Breakdown
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val colors = listOf(FintechPrimary, FintechSecondary, FintechTeritary, FintechOrange, FintechRed)
            categoryTotals.entries.forEachIndexed { index, entry ->
                item {
                    val percentage = if (totalSpent > 0) (entry.value / totalSpent) * 100 else 0.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(colors.getOrElse(index) { Color.Gray })
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(entry.key, color = FintechTextPrimaryDark, fontWeight = FontWeight.SemiBold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${String.format("%.2f", entry.value)}", color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", percentage)}%", color = FintechTextSecondaryDark, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTabContent(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit
) {
    val activeUser by authViewModel.activeUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Account",
            style = Typography.displayMedium,
            color = FintechTextPrimaryDark,
            modifier = Modifier.align(Alignment.Start)
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GlassBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = FintechPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = activeUser?.name ?: "User Name",
            color = FintechTextPrimaryDark,
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = activeUser?.email ?: "user@email.com",
            color = FintechTextSecondaryDark,
            style = Typography.bodyMedium
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("UPI ID", color = FintechTextSecondaryDark)
                    Text(activeUser?.upiId?.ifEmpty { "Not set" } ?: "Not set", color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phone", color = FintechTextSecondaryDark)
                    Text(activeUser?.phone?.ifEmpty { "Not set" } ?: "Not set", color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = FintechRed, contentColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("sign_out_button")
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupDetailViewModel,
    dashboardViewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateAddExpense: (String) -> Unit
) {
    val context = LocalContext.current
    val group by viewModel.group.collectAsState()
    val members by viewModel.members.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val simplifiedDebts by viewModel.simplifiedDebts.collectAsState()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var mName by remember { mutableStateOf("") }
    var mEmail by remember { mutableStateOf("") }
    var mPhone by remember { mutableStateOf("") }
    var mUpi by remember { mutableStateOf("") }

    var selectedSection by remember { mutableStateOf(0) } // 0 = Expenses, 1 = Members, 2 = Settle

    LaunchedEffect(groupId) {
        viewModel.loadGroup(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Group Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val file = viewModel.exportGroupToPdf(context)
                            if (file != null) {
                                val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Group Statement PDF"))
                            } else {
                                Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF",
                            tint = FintechPrimary,
                            modifier = Modifier.testTag("export_pdf_button")
                        )
                    }
                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Member", tint = FintechSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FintechBgDark,
                    titleContentColor = FintechTextPrimaryDark,
                    navigationIconContentColor = FintechTextPrimaryDark
                )
            )
        },
        floatingActionButton = {
            if (selectedSection == 0) {
                FloatingActionButton(
                    onClick = { onNavigateAddExpense(groupId) },
                    containerColor = FintechPrimary,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("add_expense_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isJudgeMode by dashboardViewModel.isJudgeMode.collectAsState()
            JudgeModeBanner(
                isJudgeMode = isJudgeMode,
                onResetDemo = {
                    dashboardViewModel.resetDemo()
                    Toast.makeText(context, "Demo reset successfully!", Toast.LENGTH_SHORT).show()
                },
                onExitJudgeMode = {
                    dashboardViewModel.setJudgeMode(false)
                    Toast.makeText(context, "Exited Judge Mode", Toast.LENGTH_SHORT).show()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Description Header
            Text(
                text = group?.description ?: "No description provided.",
                color = FintechTextSecondaryDark,
                style = Typography.bodyMedium
            )

            val aiNarration by viewModel.aiNarration.collectAsState()
            
            if (isJudgeMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FintechSecondary.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FintechSecondary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = FintechSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "🤖 AI Pitch Narration",
                                    color = FintechSecondary,
                                    fontWeight = FontWeight.Bold,
                                    style = Typography.titleSmall
                                )
                            }
                            
                            if (aiNarration == null) {
                                Button(
                                    onClick = {
                                        viewModel.generateGroupNarration(
                                            groupName = group?.name ?: "Scenario",
                                            memberCount = members.size,
                                            expenseCount = expenses.size,
                                            debts = simplifiedDebts
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FintechSecondary,
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(30.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Narrate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "Clear",
                                    color = FintechTextSecondaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.clearNarration() }
                                )
                            }
                        }

                        if (aiNarration != null) {
                            Text(
                                text = aiNarration!!,
                                color = FintechTextPrimaryDark,
                                style = Typography.bodyMedium,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            Text(
                                text = "Let Gemini analyze the balances in this demo scenario and pitch why SettleX's transaction minimizer is superior.",
                                color = FintechTextSecondaryDark,
                                style = Typography.bodySmall
                            )
                        }
                    }
                }
            }

            val overdueRecurringExpenses by viewModel.overdueRecurringExpenses.collectAsState()
            
            if (overdueRecurringExpenses.isNotEmpty()) {
                overdueRecurringExpenses.forEach { exp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = FintechRed.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, FintechRed.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = FintechRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "📅 OVERDUE RECURRING SPLIT",
                                        color = FintechRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "\"${exp.title}\" is due for renewal",
                                        color = FintechTextPrimaryDark,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.recreateRecurringExpense(exp.id)
                                    Toast.makeText(context, "Renewed recurring split: ${exp.title}!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FintechRed,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("RE-ADD NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Split Segment Switcher
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = FintechSurfDark,
                contentColor = FintechPrimary
            ) {
                Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }, text = { Text("Expenses") })
                Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }, text = { Text("Members") })
                Tab(selected = selectedSection == 2, onClick = { selectedSection = 2 }, text = { Text("Settle Up") })
            }

            when (selectedSection) {
                0 -> {
                    // Expenses List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (expenses.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No expenses logged. Tap + to add!", color = FintechTextSecondaryDark)
                                }
                            }
                        } else {
                            items(expenses) { expense ->
                                val paidBy = members.find { it.id == expense.paidByMemberId }?.name ?: "Someone"
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(expense.title, color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                                            Text("Paid by $paidBy • ${expense.category}", color = FintechTextSecondaryDark, fontSize = 12.sp)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "₹${String.format("%.2f", expense.amount)}",
                                                color = FintechPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            IconButton(onClick = { viewModel.deleteExpense(expense.id, expense.title) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = FintechRed,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Members List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(members) { member ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(FintechSurfDark)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(GlassBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.name.take(1).uppercase(),
                                            color = FintechPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(member.name, color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                                        Text(member.email, color = FintechTextSecondaryDark, fontSize = 12.sp)
                                    }
                                }

                                IconButton(onClick = { viewModel.removeMember(member.id, member.name) }) {
                                    Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "Remove", tint = FintechRed)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Settle Up / Debts Simplification
                    var showExplanationDialog by remember { mutableStateOf(false) }

                    if (showExplanationDialog) {
                        AlertDialog(
                            onDismissRequest = { showExplanationDialog = false },
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = FintechSecondary)
                                    Text("Greedy Debt Simplifier Algorithm", fontWeight = FontWeight.ExtraBold)
                                }
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "Why SettleX's transaction minimizer is a fintech engineering masterclass:",
                                        color = FintechTextPrimaryDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    
                                    Text(
                                        text = "1. Net Balance Calculation O(V + E):\nInstead of mapping each raw individual bill split, we compute the net balance (Total Paid - Total Share) for every member. This compresses raw transactions instantly.",
                                        color = FintechTextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                    
                                    Text(
                                        text = "2. Debtors vs Creditors Partitioning:\nMembers are split into two heaps: Debtors (negative net balance) and Creditors (positive net balance).",
                                        color = FintechTextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                    
                                    Text(
                                        text = "3. Greedy Matching O(V):\nWe repeatedly match the maximum debtor with the maximum creditor. We settle the minimum of the two amounts, update their balances, and push them back. This guarantee reduces total transfers from potential N(N-1)/2 transactions down to at most V-1 transactions!",
                                        color = FintechTextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(FintechSecondary.copy(alpha = 0.1f))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "💡 Evaluation Note: Try clearing a debt below. The directed graph will dynamically redraw, showing updated active transaction arrows!",
                                            color = FintechSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showExplanationDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = FintechSecondary, contentColor = Color.Black)
                                ) {
                                    Text("Got It", fontWeight = FontWeight.Bold)
                                }
                            },
                            containerColor = FintechSurfDark
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isJudgeMode) {
                            DebtGraphCanvas(debts = simplifiedDebts)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showExplanationDialog = true }
                                ) {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = FintechSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Explain O(V+E) Algorithm", color = FintechSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                        if (simplifiedDebts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Excellent! All debts settled in this group.", color = FintechGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            items(simplifiedDebts) { debt ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${debt.fromMemberName} owes ${debt.toMemberName}",
                                                    color = FintechTextPrimaryDark,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "₹${String.format("%.2f", debt.amount)}",
                                                    color = FintechRed,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 18.sp
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.recordSettlement(
                                                        debt.fromMemberId,
                                                        debt.toMemberId,
                                                        debt.amount,
                                                        "UPI",
                                                        "TXN-${UUID.randomUUID().toString().take(6).uppercase()}"
                                                    )
                                                    Toast.makeText(context, "Settlement recorded!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = FintechPrimary,
                                                    contentColor = Color.Black
                                                )
                                            ) {
                                                Text("Settle Up")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
        }

        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = { showAddMemberDialog = false },
                title = { Text("Add Group Member", color = FintechTextPrimaryDark) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = mName,
                            onValueChange = { mName = it },
                            label = { Text("Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = mEmail,
                            onValueChange = { mEmail = it },
                            label = { Text("Email") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = mPhone,
                            onValueChange = { mPhone = it },
                            label = { Text("Phone") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                        OutlinedTextField(
                            value = mUpi,
                            onValueChange = { mUpi = it },
                            label = { Text("UPI ID") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimaryDark,
                                unfocusedTextColor = FintechTextPrimaryDark
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (mName.isNotBlank() && mEmail.isNotBlank()) {
                                viewModel.addMember(mName, mEmail, mPhone, mUpi)
                                showAddMemberDialog = false
                                mName = ""
                                mEmail = ""
                                mPhone = ""
                                mUpi = ""
                            }
                        }
                    ) {
                        Text("Add", color = FintechPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddMemberDialog = false }) {
                        Text("Cancel", color = FintechTextSecondaryDark)
                    }
                },
                containerColor = FintechSurfDark
            )
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    groupId: String,
    viewModel: ExpenseEntryViewModel,
    groupViewModel: GroupDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCameraScan: (String) -> Unit
) {
    val context = LocalContext.current
    val members by groupViewModel.members.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val ocrResult by viewModel.ocrResult.collectAsState()
    val suggestedCategory by viewModel.suggestedCategory.collectAsState()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var paidByMemberId by remember { mutableStateOf("") }
    var splitType by remember { mutableStateOf("EQUAL") } // EQUAL, PERCENTAGE, EXACT

    val categories = listOf("Food", "Transport", "Utilities", "Entertainment", "Other")

    // Image/Receipt OCR launcher
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.analyzeReceipt(bitmap)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load receipt image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(members) {
        if (members.isNotEmpty() && paidByMemberId.isEmpty()) {
            paidByMemberId = members.first().id
        }
    }

    LaunchedEffect(ocrResult) {
        ocrResult?.let {
            if (it.error != null) {
                Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
            } else {
                title = it.title
                amount = it.totalAmount.toString()
                Toast.makeText(context, "Receipt analyzed successfully!", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearOcr()
        }
    }

    LaunchedEffect(suggestedCategory) {
        suggestedCategory?.let {
            selectedCategory = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FintechBgDark,
                    titleContentColor = FintechTextPrimaryDark,
                    navigationIconContentColor = FintechTextPrimaryDark
                )
            )
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Receipt OCR Trigger Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onNavigateToCameraScan(groupId) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                        .testTag("scan_receipt_camera_button"),
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Receipt", fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { pickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechSecondary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                        .testTag("scan_receipt_upload_button"),
                    enabled = !isAnalyzing
                ) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Photo", fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.length > 3) viewModel.suggestCategory(it)
                },
                label = { Text("Expense Title") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_title_input")
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_amount_input")
            )

            // Category Picker
            Text("Category", color = FintechTextSecondaryDark, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FintechPrimary,
                            selectedLabelColor = Color.Black,
                            labelColor = FintechTextSecondaryDark
                        )
                    )
                }
            }

            // Paid By Dropdown
            Text("Paid By", color = FintechTextSecondaryDark, fontWeight = FontWeight.Bold)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FintechSurfDark, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                members.forEach { m ->
                    val isSelected = paidByMemberId == m.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { paidByMemberId = m.id }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(m.name, color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold)
                        RadioButton(
                            selected = isSelected,
                            onClick = { paidByMemberId = m.id },
                            colors = RadioButtonDefaults.colors(selectedColor = FintechPrimary)
                        )
                    }
                }
            }

            // Save Action
            Button(
                onClick = {
                    val finalAmt = amount.toDoubleOrNull() ?: 0.0
                    if (title.isBlank() || finalAmt <= 0.0 || paidByMemberId.isEmpty()) {
                        Toast.makeText(context, "Please validate all inputs", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // For EQUAL split type, create equal splits automatically for all members
                    val splitList = mutableListOf<ExpenseSplitEntity>()
                    val size = members.size
                    if (size > 0) {
                        val equalOwed = finalAmt / size
                        members.forEach { m ->
                            splitList.add(
                                ExpenseSplitEntity(
                                    id = "",
                                    expenseId = "",
                                    memberId = m.id,
                                    owedAmount = equalOwed
                                )
                            )
                        }
                    }

                    viewModel.saveExpense(
                        groupId = groupId,
                        title = title,
                        amount = finalAmt,
                        category = selectedCategory,
                        paidByMemberId = paidByMemberId,
                        splitType = splitType,
                        splits = splitList
                    )

                    Toast.makeText(context, "Logged Successfully!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FintechPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_expense_button")
            ) {
                Text("Log Expense", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGroupScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var groupIdInput by remember { mutableStateOf("") }
    var passcodeVal by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Group", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FintechBgDark,
                    titleContentColor = FintechTextPrimaryDark,
                    navigationIconContentColor = FintechTextPrimaryDark
                )
            )
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Enter SettleX unique Group ID and passcode to join your friends in splitting debts seamlessly.",
                color = FintechTextSecondaryDark,
                style = Typography.bodyMedium
            )

            OutlinedTextField(
                value = groupIdInput,
                onValueChange = { groupIdInput = it },
                label = { Text("Group ID") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier.fillMaxWidth().testTag("group_id_join")
            )

            OutlinedTextField(
                value = passcodeVal,
                onValueChange = { passcodeVal = it },
                label = { Text("4-digit passcode") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FintechTextPrimaryDark,
                    unfocusedTextColor = FintechTextPrimaryDark
                ),
                modifier = Modifier.fillMaxWidth().testTag("passcode_join")
            )

            Button(
                onClick = {
                    if (groupIdInput.isBlank() || passcodeVal.isBlank()) {
                        Toast.makeText(context, "Please enter inputs", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // For demo/offline, retrieve mock or generate GroupEntity
                    dashboardViewModel.createGroup("Shared Group", "Automatically Joined", passcodeVal)
                    Toast.makeText(context, "Joined successfully!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FintechPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("join_group_submit")
            ) {
                Text("Join SettleX Group", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JudgesDemoScreen(
    dashboardViewModel: DashboardViewModel,
    groupViewModel: GroupDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isSeeding by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dashboardViewModel.setJudgeMode(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Judge Sandbox & Pitch", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isSeeding = true
                            dashboardViewModel.resetDemo()
                            Toast.makeText(context, "Demo reset & fresh seeds populated!", Toast.LENGTH_SHORT).show()
                            isSeeding = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Seeds", tint = FintechSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FintechBgDark,
                    titleContentColor = FintechTextPrimaryDark,
                    navigationIconContentColor = FintechTextPrimaryDark
                )
            )
        },
        containerColor = FintechBgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Hero Evaluation Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = FintechSecondary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, FintechSecondary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(FintechSecondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = FintechSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "JUDGE MODE INITIATED",
                            color = FintechSecondary,
                            style = Typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are inside the evaluation sandbox. We preloaded three realistic splitting scenarios so you can examine SettleX without typing. Tap any scenario below to explore!",
                            color = FintechTextPrimaryDark,
                            style = Typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 2. Active Tech Stack Strip
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ENGINEERING ARCHITECTURE", color = FintechTextSecondaryDark, style = Typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val techs = listOf("Kotlin 2.2", "Jetpack Compose", "Room Database", "Gemini AI REST", "Greedy Simplifier")
                    techs.forEach { tech ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FintechSurfDark)
                                .border(1.dp, GlassStroke, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(tech, color = FintechTextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Implemented Capabilities Checklist
            Card(
                colors = CardDefaults.cardColors(containerColor = FintechSurfDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GlassStroke)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("CORE TECHNICAL CHECKLIST", color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold, style = Typography.titleMedium)
                    
                    val capabilities = listOf(
                        "Greedy Debt Simplifier (Greedy Graph Algorithm)" to "Minimizes 3-way overlapping debts into minimal direct payments.",
                        "OCR Receipt Scanning (Gemini Pro Vision API)" to "Direct REST call extracts merchant details and total bill offline-safely.",
                        "PDF Statement Exporter (Local Jetpack Printing)" to "Produces beautifully formatted group expense and balance statements.",
                        "One-Tap UPI Settlement (Dynamic URI Deep Linking)" to "Direct payment deep linking directly launches phone-installed UPI payment apps."
                    )

                    capabilities.forEach { (title, desc) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FintechPrimary,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Column {
                                Text(title, color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold, style = Typography.bodyMedium)
                                Text(desc, color = FintechTextSecondaryDark, style = Typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 4. Preloaded Scenarios (Goa, Flat, Office)
            Text("PITCH DEMO SCENARIOS", color = FintechTextSecondaryDark, style = Typography.labelMedium, fontWeight = FontWeight.Bold)

            val scenarios = listOf(
                Triple("Goa Trip 🏖️", "Reunion at Candolim beach. Food, airport cab and resort split across 3 members.", "demo_grp_goa"),
                Triple("Flat Expenses 🏠", "Monthly shared household bills. Features rent, groceries, wifi and recurring alerts.", "demo_grp_flat"),
                Triple("Office Lunch 🥪", "Two-person split for corporate meals and coffee. Simple linear ledger settling.", "demo_grp_office")
            )

            scenarios.forEach { (name, desc, groupId) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dashboardViewModel.setJudgeMode(true)
                            groupViewModel.loadGroup(groupId)
                        },
                    colors = CardDefaults.cardColors(containerColor = FintechSurfDarkElevated),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GlassStroke)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = FintechSecondary, fontWeight = FontWeight.ExtraBold, style = Typography.titleMedium)
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = FintechTextSecondaryDark)
                        }

                        Text(desc, color = FintechTextPrimaryDark, style = Typography.bodyMedium)

                        Button(
                            onClick = {
                                groupViewModel.loadGroup(groupId)
                                Toast.makeText(context, "Entering $name Group Dashboard...", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FintechSecondary.copy(alpha = 0.15f), contentColor = FintechSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Examine Settle Dashboard", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DebtGraphCanvas(debts: List<SimplifiedDebt>) {
    if (debts.isEmpty()) return
    
    val members = (debts.map { it.fromMemberName } + debts.map { it.toMemberName }).distinct()
    if (members.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FintechSurfDark)
            .border(1.dp, GlassStroke, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Hub, contentDescription = null, tint = FintechSecondary)
                Text("Greedy Minimizer Directed Debt Graph", color = FintechTextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(FintechSecondary.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("OPTIMIZED O(V+E)", color = FintechSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) / 2f - 30.dp.toPx()
            
            val memberPositions = members.mapIndexed { idx, name ->
                val angle = (idx * 2 * Math.PI / members.size) - Math.PI / 2
                val x = center.x + radius * Math.cos(angle).toFloat()
                val y = center.y + radius * Math.sin(angle).toFloat()
                name to Offset(x, y)
            }.toMap()

            debts.forEach { debt ->
                val startNode = memberPositions[debt.fromMemberName]
                val endNode = memberPositions[debt.toMemberName]
                if (startNode != null && endNode != null) {
                    drawLine(
                        color = FintechRed.copy(alpha = 0.5f),
                        start = startNode,
                        end = endNode,
                        strokeWidth = 2.dp.toPx()
                    )

                    val midPoint = Offset((startNode.x + endNode.x) / 2f, (startNode.y + endNode.y) / 2f)
                    drawCircle(
                        color = FintechBgDark,
                        radius = 12.dp.toPx(),
                        center = midPoint
                    )

                    val angle = Math.atan2((endNode.y - startNode.y).toDouble(), (endNode.x - startNode.x).toDouble())
                    val arrowLength = 10.dp.toPx()
                    val arrowAngle = Math.PI / 6
                    
                    val nodeRadiusOffset = 18.dp.toPx()
                    val arrowTip = Offset(
                        endNode.x - nodeRadiusOffset * Math.cos(angle).toFloat(),
                        endNode.y - nodeRadiusOffset * Math.sin(angle).toFloat()
                    )
                    
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(arrowTip.x, arrowTip.y)
                        lineTo(
                            arrowTip.x - arrowLength * Math.cos(angle - arrowAngle).toFloat(),
                            arrowTip.y - arrowLength * Math.sin(angle - arrowAngle).toFloat()
                        )
                        lineTo(
                            arrowTip.x - arrowLength * Math.cos(angle + arrowAngle).toFloat(),
                            arrowTip.y - arrowLength * Math.sin(angle + arrowAngle).toFloat()
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        color = FintechRed
                    )
                }
            }

            memberPositions.forEach { (name, pos) ->
                drawCircle(
                    color = FintechSecondary.copy(alpha = 0.2f),
                    radius = 20.dp.toPx(),
                    center = pos
                )
                drawCircle(
                    color = FintechSecondary,
                    radius = 14.dp.toPx(),
                    center = pos
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            members.forEach { name ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(FintechSecondary)
                    )
                    Text(
                        text = name,
                        color = FintechTextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
