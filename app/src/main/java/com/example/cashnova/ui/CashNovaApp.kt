package com.example.cashnova.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cashnova.ui.screens.AnalyticsScreen
import com.example.cashnova.ui.screens.DashboardScreen
import com.example.cashnova.ui.screens.OnboardingScreen
import com.example.cashnova.ui.screens.SavingsScreen
import com.example.cashnova.ui.screens.SettingsScreen
import com.example.cashnova.ui.screens.WalletScreen

/*
 * Kumpulan route navigation aplikasi.
 * Dibuat terpusat agar konsisten dan menghindari typo string route.
 */
private object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SAVINGS = "savings"
    const val WALLET = "wallet"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
}

/*
 * Root composable aplikasi:
 * - Mengamati uiState global dari ViewModel.
 * - Menentukan start destination berdasarkan onboarding.
 * - Menghubungkan setiap screen dengan callback ke ViewModel.
 */
@Composable
fun CashNovaApp(
    viewModel: CashNovaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // State utama aplikasi; perubahan state akan memicu recomposition screen terkait.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Jika onboarding sudah selesai, langsung ke dashboard; jika belum, tampilkan onboarding dulu.
    val startDestination = if (uiState.onboardingCompleted) {
        Routes.DASHBOARD
    } else {
        Routes.ONBOARDING
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(260)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(260)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(260)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(260)
            )
        }
    ) {

        /*
         * Onboarding:
         * - Menandai onboarding selesai di state/persistence.
         * - Menghapus onboarding dari back stack agar tidak kembali saat tombol back ditekan.
         */
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onStart = {
                    viewModel.completeOnboarding()

                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.ONBOARDING) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * Dashboard:
         * menampilkan ringkasan keuangan utama dan memberi akses cepat ke halaman lain.
         */
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                state = uiState,

                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                },

                onOpenSavings = {
                    navController.navigate(Routes.SAVINGS)
                },

                onOpenWallet = {
                    navController.navigate(Routes.WALLET)
                },

                onOpenAnalytics = {
                    navController.navigate(Routes.ANALYTICS)
                },

                // Aksi transaksi/category diteruskan ke ViewModel sebagai source of truth.
                onAddTransaction = viewModel::addTransaction,
                onAddCustomCategory = viewModel::addCustomCategory
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(
                state = uiState,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SAVINGS) {
            SavingsScreen(
                state = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onAddGoal = viewModel::addSavingGoal,
                onDeposit = viewModel::depositToSaving,
                onDeleteGoal = viewModel::deleteSavingGoal
            )
        }

        composable(Routes.WALLET) {
            WalletScreen(
                state = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onAddTransaction = viewModel::addTransaction,
                onDeleteTransaction = viewModel::deleteTransaction,
                onOpenSavings = {
                    navController.navigate(Routes.SAVINGS)
                },
                onSelectWallet = viewModel::selectWallet,
                onAddWallet = viewModel::addWallet,
                onDeleteWallet = viewModel::deleteWallet,
                onAddCustomCategory = viewModel::addCustomCategory
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                state = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onSaveName = viewModel::updateProfileName,
                onResetData = viewModel::resetDemoData,
                onUpdateTheme = viewModel::updateThemeMode
            )
        }
    }
}