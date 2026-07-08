# CashSnova

CashSnova adalah aplikasi pencatatan keuangan pribadi (*personal finance tracker*) berbasis Android yang dirancang dengan antarmuka modern dan dinamis. Aplikasi ini membantu pengguna melacak pemasukan, pengeluaran, target tabungan (*saving goals*), serta mengelola dompet (*multi-wallet support*) dengan mudah.

## 🚀 Teknologi Aplikasi

Aplikasi ini dibangun menggunakan arsitektur modern Android:
- **Kotlin**: Bahasa pemrograman utama.
- **Jetpack Compose**: Toolkit UI modern deklaratif untuk membangun antarmuka Android.
- **ViewModel & StateFlow**: Manajemen state dan logika bisnis yang mematuhi prinsip *Single Source of Truth*.
- **Room Database**: Solusi penyimpanan lokal (ORM) untuk menyimpan data transaksi secara persisten.
- **SharedPreferences**: Menyimpan data preferensi ringan seperti status *onboarding*, konfigurasi tema, dan profil pengguna.
- **Navigation Compose**: Mengatur navigasi antar layar (Screen) dalam aplikasi.

## ✨ Fitur Utama

- **Sistem Autentikasi Lokal**: Fitur Login dan Register yang aman. Pengguna baru akan mendapatkan *workspace* (data) baru yang bersih, dan dilengkapi dengan fitur *Remember Me*.
- **Onboarding Interaktif**: Pengenalan fitur aplikasi bagi pengguna baru.
- **Dashboard Komprehensif**: Menampilkan ringkasan saldo, pengeluaran, pemasukan, serta sumber pendapatan dinamis.
- **Manajemen Transaksi**: Tambah, detail, dan hapus transaksi (Pemasukan/Pengeluaran).
- **Multi-Wallet Support**: Pengguna dapat memiliki banyak dompet/rekening dan melakukan pelacakan transaksi secara spesifik untuk masing-masing dompet.
- **Saving Goals**: Tetapkan target tabungan dan lakukan penyetoran dana ke tabungan tersebut secara progresif.
- **Grafik & Analitik**: Visualisasi grafik keuangan yang dapat difilter berdasarkan rentang waktu dan dompet.
- **Tema Fleksibel**: Dukungan penuh untuk *Dark Mode*, *Light Mode*, dan mengikuti pengaturan sistem.
- **Manajemen Profil**: Ganti nama profil pengguna, dan *reset* ke *demo data* untuk mencoba aplikasi.

## 🔄 Alur Program (Program Flow)

1. **Aplikasi Dibuka (`MainActivity.kt`)**: 
   Aplikasi memuat `CashNovaApp` sebagai akar (root) navigasi dan menginisialisasi `CashNovaViewModel`.
2. **Pengecekan State Awal**: 
   Aplikasi akan memeriksa status pengguna di lokal:
   - Jika pengguna belum pernah membuka aplikasi -> Arahkan ke **OnboardingScreen**.
   - Jika belum ada sesi *login* / *logout* terakhir -> Arahkan ke **LoginScreen**.
   - Jika sesi *login* masih aktif (*Remember Me*) -> Arahkan langsung ke **DashboardScreen**.
3. **Autentikasi (Login / Register)**:
   - Pengguna baru bisa membuat akun di **RegisterScreen**.
   - Pengguna masuk di **LoginScreen**. Jika pengguna yang masuk berbeda dengan pengguna sebelumnya, data transaksi akan di-reset (dikunci per pengguna).
4. **Interaksi Utama (Dashboard & Wallet)**:
   - Dari **DashboardScreen**, pengguna dapat melihat ringkasan keuangan, menambah transaksi, atau pindah ke **WalletScreen** untuk melihat detail saldo tiap dompet.
   - Tersedia tombol cepat untuk masuk ke layar analitik (**AnalyticsScreen**) atau tabungan (**SavingsScreen**).
5. **Pengaturan (Settings)**:
   - Di **SettingsScreen**, pengguna bisa mengganti tema, nama, menghapus akun (*Logout*), atau mengatur ulang data percobaan (*Reset Demo Data*).

## 🧩 Penjelasan Kode dan Fungsi Penting

Berikut adalah beberapa komponen inti yang menyusun arsitektur aplikasi CashSnova:

### 1. `MainActivity.kt` & `CashNovaApp.kt`
- **`MainActivity.kt`**: Titik masuk utama aplikasi (Entry Point). Hanya bertugas menyiapkan *edge-to-edge layout* dan menyediakan `CashNovaViewModel` secara global.
- **`CashNovaApp.kt`**: Pusat navigasi (*NavHost*). Mendefinisikan rute (`Routes`) aplikasi (Onboarding, Login, Register, Dashboard, dll) dan melakukan *routing* otomatis bergantung pada state `onboardingCompleted` dan `currentUser`.

### 2. `CashNovaViewModel.kt` (Business Logic & State Management)
*ViewModel* bertugas mengelola semua interaksi pengguna dari UI dan berkomunikasi dengan Layer Data (`CashNovaRepository`).
Fungsi penting:
- **`login(...)`**: Melakukan pengecekan kredensial. Jika *username* berbeda dari yang terakhir kali login, fungsi ini akan memanggil repositori untuk mengosongkan state/transaksi, memberikan ruang kerja yang bersih (*refresh akun*).
- **`addTransaction(...)`**: Menyimpan transaksi ke Room Database dan otomatis merefleksikan perubahan di UI melalui aliran reaktif (`StateFlow`).
- **`depositToSaving(...)`**: Melakukan kalkulasi logika tabungan, di mana deposit akan mengurangi saldo dompet dan meningkatkan jumlah (*progress*) tabungan secara bersamaan.

### 3. `CashNovaRepository.kt` (Data Layer)
Bertindak sebagai jembatan *Single Source of Truth* antara *Room Database* dan *SharedPreferences*.
Fungsi penting:
- **`observeTransactions()`**: Mengembalikan `Flow<List<FinanceTransaction>>` sehingga setiap kali ada perubahan pada database Room (insert/delete/update), UI akan bereaksi dan memperbarui tampilannya tanpa perlu muat ulang manual.
- **`loadPreferencesState()`**: Mengambil data preferensi awal (profil pengguna, target tabungan, daftar dompet, sesi login).
- **`clearDataForNewUser()`**: Dijalankan ketika mendeteksi pergantian pengguna (*user switch*). Fungsi ini akan menghapus semua riwayat transaksi dari Room Database dan me-reset preferensi dompet dan tabungan ke kondisi awal yang kosong.

### 4. `Models.kt` (Domain Models)
Menyimpan struktur data inti aplikasi:
- **`CashNovaUiState`**: Data class yang menyatukan seluruh state layar. Properti dinamis seperti `totalBalance`, `totalIncome`, dan `totalExpense` memanfaatkan Kotlin *getter* sehingga nilainya selalu dikalkulasi (dihitung ulang) sesuai dengan dompet yang aktif (`selectedWalletId`).

## 📁 Struktur Direktori

```text
com.example.cashnova
├── MainActivity.kt           // Entry point aplikasi Android
├── data
│   ├── Models.kt             // Kelas data (State, Transaction, User, Wallet, dll)
│   ├── CashNovaRepository.kt // Pengelola sumber data (Room & SharedPreferences)
│   └── local/                // Implementasi Room DB (Entity, DAO, Mapper, Database)
└── ui
    ├── CashNovaApp.kt        // Pusat Navigasi (NavHost)
    ├── CashNovaViewModel.kt  // Pusat State dan Logika Bisnis
    ├── components/           // Komponen UI yang dapat digunakan kembali (Cards, Charts, Dialogs)
    ├── screens/              // Layar utama (Login, Dashboard, Wallet, Settings, dll)
    ├── theme/                // Konfigurasi Material 3 (Warna, Tipografi, Bentuk)
    └── util/                 // Kelas bantuan (*Helper/Extension*)
```

## 🛠️ Catatan Build
- **JDK**: 17
- **compileSdk**: 36 (Minimum SDK 24)
- **AGP**: 9.2.1 | **Gradle**: 9.4.1
- **Kotlin**: 2.3.21

Untuk build aplikasi dari Terminal Android Studio:
```bash
./gradlew assembleDebug
```
