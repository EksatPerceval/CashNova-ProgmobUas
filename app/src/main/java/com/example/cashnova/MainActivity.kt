package com.example.cashnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashnova.ui.CashNovaApp
import com.example.cashnova.ui.CashNovaViewModel
import com.example.cashnova.ui.theme.CashNovaTheme

/*
 * Entry point utama aplikasi Android.
 *
 * Tanggung jawab file ini:
 * 1) Membuat/menyediakan ViewModel utama aplikasi.
 * 2) Mengamati uiState secara lifecycle-aware.
 * 3) Mengaplikasikan theme berdasarkan preferensi pengguna.
 * 4) Merender root composable (CashNovaApp).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengaktifkan layout edge-to-edge agar UI bisa memanfaatkan area penuh layar.
        enableEdgeToEdge()

        setContent {
            /*
             * ViewModel disediakan sekali pada scope Activity.
             * Semua screen di bawah CashNovaApp menggunakan state dari ViewModel ini.
             */
            val viewModel: CashNovaViewModel = viewModel()

            /*
             * collectAsStateWithLifecycle mencegah observasi berjalan saat lifecycle tidak aktif.
             * Ini aman untuk performa dan mencegah update state yang tidak diperlukan.
             */
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            /*
             * Theme aplikasi mengikuti preferensi (SYSTEM/LIGHT/DARK) dari uiState.themeMode.
             * Tidak mengubah logic bisnis, hanya mengontrol tampilan global.
             */
            CashNovaTheme(themeMode = uiState.themeMode) {
                // Root navigation + screen orchestration aplikasi.
                CashNovaApp(viewModel = viewModel)
            }
        }
    }
}
