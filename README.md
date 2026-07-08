# CashSnova

CashSnova adalah aplikasi pencatatan keuangan pribadi (*personal finance tracker*) berbasis Android yang dirancang dengan antarmuka modern dan dinamis menggunakan Jetpack Compose. Aplikasi ini membantu pengguna melacak pemasukan, pengeluaran, target tabungan (*saving goals*), serta mengelola dompet (*multi-wallet*) dengan mudah dan aman.

---

## 🚀 Teknologi Aplikasi

| Teknologi | Versi / Keterangan |
|---|---|
| **Kotlin** | 2.3.21 — Bahasa pemrograman utama |
| **Jetpack Compose** | Toolkit UI deklaratif modern Android |
| **ViewModel & StateFlow** | Manajemen state reaktif, Single Source of Truth |
| **Room Database** | ORM lokal — menyimpan semua data keuangan & pengguna |
| **SharedPreferences** | Sesi login (remember_me, onboarding, tema) |
| **Navigation Compose** | Navigasi antar layar berbasis rute |
| **Kotlin Coroutines & Flow** | Operasi async dan data reaktif |
| **Material 3** | Sistem desain komponen & tema |
| **KSP** | Kotlin Symbol Processing untuk Room code-gen |

- **JDK**: 17 | **compileSdk**: 36 | **minSdk**: 24
- **AGP**: 9.2.1 | **Gradle**: 9.4.1

---

## ✨ Fitur Utama

- 🔐 **Sistem Autentikasi Lokal (Room-based)**: Login dan Register yang sesungguhnya — kredensial disimpan dan diverifikasi langsung ke Room Database.
- 🧠 **Remember Me**: Sesi pengguna tetap aktif antar restart aplikasi jika memilih "Remember Me".
- 👥 **Multi-User sejati**: Setiap pengguna memiliki data (wallet, transaksi, tabungan, kategori) yang sepenuhnya terisolasi di database.
- 📊 **Dashboard Komprehensif**: Ringkasan saldo, total pemasukan, pengeluaran, dan daftar sumber pendapatan dinamis.
- 💳 **Multi-Wallet**: Pengguna dapat membuat banyak dompet/rekening. Transaksi dan saldo dihitung spesifik per dompet.
- 📈 **Grafik & Analitik**: Visualisasi grafik keuangan yang dapat difilter berdasarkan rentang waktu.
- 💰 **Saving Goals**: Tetapkan target tabungan, setor dana secara progresif, dan pantau kemajuannya.
- 🏷️ **Kategori Custom**: Tambahkan kategori pengeluaran/pemasukan sendiri di samping kategori bawaan.
- 🎨 **Tema Fleksibel**: Dukungan penuh Dark Mode, Light Mode, dan mengikuti pengaturan sistem.
- 🔄 **Cascade Delete**: Penghapusan wallet otomatis menghapus semua transaksi terkait melalui relasi FK di Room.
- ⚙️ **Pengaturan Profil**: Ganti nama profil dan tombol Logout yang tersedia di halaman Settings.

---

## 🔄 Alur Program (Program Flow)

```
Buka Aplikasi (MainActivity)
        │
        ▼
   Cek SharedPreferences
        │
   ┌────┴────────────┐
   │                 │
Onboarding      Sesi aktif?
Belum selesai       │
   │           ┌─────┴──────┐
   ▼           │            │
OnboardingScreen  Ya (RememberMe)  Tidak
   │               │            │
   ▼               ▼            ▼
LoginScreen    DashboardScreen  LoginScreen
   │
   ├── Belum punya akun → RegisterScreen
   │       └── Daftar → Room (users table) + inisialisasi
   │                     wallet default + kategori default
   │
   └── Punya akun → Verifikasi ke Room DB
           └── Berhasil → DashboardScreen
                  │
         ┌────────┼─────────┬──────────┐
         ▼        ▼         ▼          ▼
    WalletScreen SavingsScreen AnalyticsScreen SettingsScreen
                                                    │
                                                Logout → LoginScreen
```

---

## 🗄️ Arsitektur Database (Room v4)

Aplikasi menggunakan **5 tabel Room** yang saling berelasi:

```
users (PK: username)
 ├── wallets (FK: username → CASCADE)
 │    └── transactions (FK: walletId → CASCADE)
 ├── saving_goals (FK: username → CASCADE)
 └── categories (FK: username → CASCADE, username="" untuk default)
```

### Tabel & Kolom

| Tabel | Kolom Utama | Keterangan |
|---|---|---|
| `users` | `username` (PK), `pin`, `profileName` | Akun pengguna |
| `wallets` | `id` (PK), `username` (FK), `name`, `balance`, `colorKey` | Dompet per pengguna |
| `transactions` | `id` (PK), `walletId` (FK), `username`, `title`, `amount`, `type`, `category`, `createdAt` | Riwayat transaksi |
| `saving_goals` | `id` (PK), `username` (FK), `title`, `currentAmount`, `targetAmount`, `daysLeft`, `colorKey` | Target tabungan |
| `categories` | `name`+`username` (PK), `type` | Kategori default & custom |

> 📌 **Cascade Delete**: Menghapus `wallet` → semua `transactions`-nya ikut terhapus otomatis. Menghapus `user` → semua wallets, savings, dan categories-nya ikut terhapus.

### Lokasi File Database di Perangkat Android
```
/data/data/com.example.cashnova/databases/
  ├── cashnova_database        (file utama SQLite)
  ├── cashnova_database-wal    (Write-Ahead Log)
  └── cashnova_database-shm    (Shared Memory)

/data/data/com.example.cashnova/shared_prefs/
  └── cashnova_preferences.xml (sesi login, tema, onboarding)
```

---

## 🧩 Penjelasan Kode dan Fungsi Penting

### 1. `MainActivity.kt` & `CashNovaApp.kt`
- **`MainActivity.kt`**: Entry point Android. Hanya menyiapkan edge-to-edge layout dan menyediakan `CashNovaViewModel` secara global.
- **`CashNovaApp.kt`**: Pusat navigasi (`NavHost`). Memeriksa state `onboardingCompleted` dan `currentUser` untuk menentukan layar awal (Onboarding / Login / Dashboard).

### 2. `CashNovaViewModel.kt` (Business Logic)
- **`observeUserData()`**: Menggunakan `flatMapLatest` untuk mengamati semua Flow Room (Transactions, Wallets, Savings, Categories) secara reaktif. Ketika pengguna berganti (login/logout), semua observasi diperbarui secara otomatis.
- **`login(...)`**: Memvalidasi kredensial ke tabel `users` di Room. Jika cocok, memuat sesi pengguna ke state UI.
- **`register(...)`**: Mendaftarkan akun baru ke tabel `users`, lalu secara otomatis menginisialisasi wallet default dan kategori default untuk pengguna baru.
- **`depositToSaving(...)`**: Memperbarui `currentAmount` di tabel `saving_goals` DAN mencatat transaksi pengeluaran di tabel `transactions` secara bersamaan dalam satu coroutine.
- **`deleteWallet(...)`**: Memastikan pengguna tidak bisa menghapus wallet terakhirnya. Penghapusan di Room secara otomatis menghapus semua transaksi terkait via FK CASCADE.

### 3. `CashNovaRepository.kt` (Data Layer)
- **`observeTransactionsByUser()`**: Mengembalikan `Flow<List<FinanceTransaction>>` yang difilter per pengguna. UI bereaksi otomatis setiap kali ada perubahan data.
- **`registerUser()`**: Menyimpan `UserEntity` baru, kemudian memanggil `walletDao.insertWallet()` dan `seedDefaultCategories()` untuk inisialisasi data pengguna baru.
- **`seedDefaultCategories()`**: Mengisi tabel `categories` dengan 10 kategori bawaan (Salary, Food, Transport, dll) menggunakan `username = ""` agar bisa dipakai semua pengguna.
- **`saveSession()` / `clearSession()`**: Menyimpan/menghapus sesi login di SharedPreferences untuk keperluan "Remember Me".

### 4. `Models.kt` (Domain Models)
- **`CashNovaUiState`**: Data class yang menjadi Single Source of Truth UI. Properti seperti `totalBalance`, `filteredTransactions`, dan `earningSources` menggunakan Kotlin `getter` sehingga selalu dihitung ulang secara dinamis berdasarkan `selectedWalletId`.
- **`SavingGoal.progress`**: Computed property yang menghitung persentase kemajuan tabungan secara otomatis.

### 5. Mapper (`TransactionMapper.kt`)
Menyediakan fungsi extension untuk mengkonversi antara model domain (digunakan UI) dan entity Room (digunakan database):
- `TransactionEntity.toFinanceTransaction()` — Entity → Domain
- `FinanceTransaction.toTransactionEntity(username)` — Domain → Entity
- `WalletEntity.toWallet()` / `Wallet.toWalletEntity(username)` — Wallet mapper
- `SavingGoalEntity.toSavingGoal()` / `SavingGoal.toSavingGoalEntity(username)` — Saving mapper

---

## 📁 Struktur Direktori

```text
com.example.cashnova
├── MainActivity.kt
├── data
│   ├── Models.kt                    // Domain models & CashNovaUiState
│   ├── CashNovaRepository.kt        // Pengelola semua sumber data Room + Prefs
│   └── local/
│       ├── CashNovaDatabase.kt      // Room Database (v4, 5 tabel)
│       ├── dao/
│       │   ├── UserDao.kt
│       │   ├── WalletDao.kt
│       │   ├── TransactionDao.kt
│       │   ├── SavingGoalDao.kt
│       │   └── CategoryDao.kt
│       ├── entity/
│       │   ├── UserEntity.kt
│       │   ├── WalletEntity.kt
│       │   ├── TransactionEntity.kt
│       │   ├── SavingGoalEntity.kt
│       │   └── CategoryEntity.kt
│       └── mapper/
│           └── TransactionMapper.kt // Mapper Domain ↔ Entity
└── ui/
    ├── CashNovaApp.kt               // Pusat Navigasi (NavHost)
    ├── CashNovaViewModel.kt         // State Management & Logika Bisnis
    ├── components/                  // Komponen UI reusable
    ├── screens/
    │   ├── LoginScreen.kt           // Layar Login (verifikasi ke Room)
    │   ├── RegisterScreen.kt        // Layar Register (simpan ke Room)
    │   ├── OnboardingScreen.kt
    │   ├── DashboardScreen.kt
    │   ├── WalletScreen.kt
    │   ├── SavingsScreen.kt
    │   ├── AnalyticsScreen.kt
    │   └── SettingsScreen.kt        // Termasuk tombol Logout
    ├── theme/                       // Material 3 (Warna, Tipografi)
    └── util/                        // Extension functions & helper
```

---

## 🛠️ Build & Run

Build APK Debug:
```bash
./gradlew assembleDebug
```

Jalankan langsung ke emulator/device yang terhubung:
```bash
./gradlew installDebug
```

Jika gradle-wrapper.jar bermasalah:
```bash
gradle wrapper --gradle-version 9.4.1
```
