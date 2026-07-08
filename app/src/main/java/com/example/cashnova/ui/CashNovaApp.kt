package com.example.cashnova.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cashnova.ui.screens.*

/*
 * Definisi rute navigasi aplikasi CashSnova.
 */
private object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val SAVINGS = "savings"
    const val WALLET = "wallet"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
}

/*
 * Root Composable yang mengatur navigasi seluruh aplikasi.
 */
@Composable
fun CashNovaApp(
    viewModel: CashNovaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Menentukan layar awal. Jika user sudah login (remember me), langsung ke Dashboard.
    // Jika belum login, selalu tampilkan Onboarding (sebagai layar Welcome) sebelum Login.
    val startDestination = when {
        uiState.currentUser != null -> Routes.DASHBOARD
        else -> Routes.ONBOARDING
    }

    // Navigasi otomatis jika status login berubah (misal: saat startup mendeteksi sesi lama)
    LaunchedEffect(uiState.currentUser) {
        if (uiState.onboardingCompleted && uiState.currentUser != null) {
            // Jika user terdeteksi login, pastikan kita tidak tertahan di layar Login
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) }
    ) {

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onStart = {
                    viewModel.completeOnboarding()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginAction = viewModel::login
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Arahkan ke halaman Login setelah register berhasil,
                    // hapus Register dari back stack agar tombol Back tidak kembali ke Register.
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onRegisterAction = viewModel::register
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                state = uiState,
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenSavings = { navController.navigate(Routes.SAVINGS) },
                onOpenWallet = { navController.navigate(Routes.WALLET) },
                onOpenAnalytics = { navController.navigate(Routes.ANALYTICS) },
                onAddTransaction = viewModel::addTransaction,
                onAddCustomCategory = viewModel::addCustomCategory
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(state = uiState, onBack = { navController.popBackStack() })
        }

        composable(Routes.SAVINGS) {
            SavingsScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onAddGoal = viewModel::addSavingGoal,
                onDeposit = viewModel::depositToSaving,
                onDeleteGoal = viewModel::deleteSavingGoal
            )
        }

        composable(Routes.WALLET) {
            WalletScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onAddTransaction = viewModel::addTransaction,
                onDeleteTransaction = viewModel::deleteTransaction,
                onOpenSavings = { navController.navigate(Routes.SAVINGS) },
                onSelectWallet = viewModel::selectWallet,
                onAddWallet = viewModel::addWallet,
                onDeleteWallet = viewModel::deleteWallet,
                onAddCustomCategory = viewModel::addCustomCategory
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onSaveName = viewModel::updateProfileName,
                onResetData = viewModel::resetDemoData,
                onUpdateTheme = viewModel::updateThemeMode,
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true } // Bersihkan history navigasi
                    }
                }
            )
        }
    }
}
