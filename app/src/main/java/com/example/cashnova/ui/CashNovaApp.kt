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

private object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SAVINGS = "savings"
    const val WALLET = "wallet"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
}

@Composable
fun CashNovaApp(
    viewModel: CashNovaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

                onAddTransaction = viewModel::addTransaction
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
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                state = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onSaveName = viewModel::updateProfileName,
                onResetData = viewModel::resetDemoData
            )
        }
    }
}