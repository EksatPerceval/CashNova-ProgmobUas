# 📖 Panduan Presentasi Kelompok: Pembagian Tugas CashNova (4 Orang)

Dokumen ini adalah panduan lengkap dan sangat detail mengenai pembagian tugas dalam pengembangan aplikasi CashNova. Dokumen ini dirancang khusus agar setiap anggota kelompok memiliki pemahaman mendalam tentang bagiannya masing-masing, sehingga dapat menjelaskannya dengan percaya diri saat presentasi di depan dosen penguji.

Aplikasi CashNova dibangun menggunakan arsitektur **MVVM (Model-View-ViewModel)**. Arsitektur ini sangat ideal untuk dibagi menjadi 4 bagian karena setiap lapisannya memiliki tugas yang spesifik dan terpisah.

---

## 👥 Rangkuman Pembagian Peran

| Anggota | Peran / Lapisan Arsitektur | Fokus Utama Pekerjaan | File Utama yang Dikerjakan |
| :--- | :--- | :--- | :--- |
| **Anggota 1** | **Database & Data Layer (Model)** | Merancang tabel, struktur data, dan kueri database Room. | `Entity.kt`, `Dao.kt`, `CashNovaDatabase.kt` |
| **Anggota 2** | **Business Logic & State (ViewModel)** | Mengolah data dari DB agar siap dipakai UI, membuat fungsi logika aplikasi. | `CashNovaViewModel.kt`, `CashNovaRepository.kt` |
| **Anggota 3** | **UI Core & Visualisasi (Frontend A)** | Membuat tampilan utama (Dashboard), mendesain kartu, dan grafik keuangan. | `DashboardScreen.kt`, `AnalyticsScreen.kt` |
| **Anggota 4** | **Navigasi, Auth, & UX (Frontend B)** | Mengatur pindah halaman, layar Login/Register, dan notifikasi ke pengguna. | `CashNovaApp.kt`, `LoginScreen.kt`, `RegisterScreen.kt` |

---

## 👨‍💻 Anggota 1: Database & Data Layer (Bagian "Model")

**Tugas Utama:** Kamu adalah "arsitek penyimpan data". Tugasmu memastikan data pengguna (seperti uang, transaksi, dan akun) tersimpan dengan aman di dalam memori HP (menggunakan SQLite / Room Database) dan tidak tercampur dengan data pengguna lain.

### Apa yang harus kamu jelaskan saat presentasi?

1. **Konsep Multi-User (Banyak Pengguna):**
   * *Penjelasan:* "Aplikasi kami bisa digunakan oleh banyak akun dalam satu HP. Untuk mewujudkan ini, saya membuat tabel `users`. Setiap tabel lain (seperti tabel `wallets` atau `saving_goals`) memiliki kolom `username` sebagai **Foreign Key**. Jadi, jika Asep login, dia hanya akan melihat dompet milik Asep, bukan milik Budi."
2. **Integritas Data (Penghapusan Otomatis / CASCADE):**
   * *Penjelasan:* "Saya mengatur relasi antar tabel menggunakan aturan `CASCADE`. Artinya, jika seorang pengguna menghapus sebuah dompet (Wallet), maka sistem database akan secara otomatis menghapus semua riwayat transaksi yang ada di dalam dompet tersebut. Ini mencegah adanya 'data hantu' atau data transaksi yang tidak punya induk."
3. **Migrasi Database Destruktif:**
   * *Penjelasan:* "Dalam proses pengembangan, struktur tabel sering berubah. Saya menggunakan fungsi `fallbackToDestructiveMigration()` pada Room. Fungsi ini bertindak seperti tombol *reset*; jika kami mengubah struktur kolom, database akan menghapus tabel lama dan membuat yang baru secara otomatis agar aplikasi tidak *crash*."

### File yang menjadi tanggung jawabmu:
* `data/local/entity/*`: Tempat kamu mendefinisikan bentuk tabel (UserEntity, WalletEntity, TransactionEntity, dll).
* `data/local/dao/*`: Tempat kamu menulis perintah SQL (Query, Insert, Delete).
* `data/local/CashNovaDatabase.kt`: Jantung dari database, tempat semua tabel didaftarkan.

---

## 🧑‍💻 Anggota 2: Business Logic & State (Bagian "ViewModel")

**Tugas Utama:** Kamu adalah "otak" dari aplikasi. Tugasmu mengambil data mentah dari Anggota 1 (Database), mengolahnya (misalnya menjumlahkan total uang), dan mengirimkan data yang sudah matang ke Anggota 3 & 4 (UI/Tampilan).

### Apa yang harus kamu jelaskan saat presentasi?

1. **Aliran Data Reaktif (Reactive Programming dengan Flow):**
   * *Penjelasan:* "Saya menggunakan teknologi `StateFlow` dan `Flow` dari Kotlin Coroutines. Konsepnya seperti pipa air yang mengalir terus menerus. Begitu ada transaksi baru masuk ke database, aliran data ini akan langsung memberi tahu layar (UI) untuk memperbarui total saldo tanpa perlu me-refresh halaman (otomatis)."
2. **Pemisahan Tugas (Single Source of Truth):**
   * *Penjelasan:* "UI (tampilan) di aplikasi kami sangat bodoh, ia hanya bertugas menampilkan gambar. Semua logika perhitungan (seperti menghitung sisa tabungan atau memfilter transaksi bulan ini) saya letakkan di `CashNovaViewModel.kt`. Ini membuat kode sangat rapi dan mudah dicari jika ada *bug* (error)."
3. **Logika Transaksi Kompleks (Contoh: Menabung):**
   * *Penjelasan:* "Saya juga membuat logika yang menghubungkan dua tabel. Contohnya saat fitur 'Deposit Tabungan' ditekan, fungsi yang saya buat akan melakukan dua hal sekaligus: (1) Menambah saldo di tabel `saving_goals`, dan (2) Mencatat pengeluaran di tabel `transactions`. Semuanya berjalan bersamaan di latar belakang (*background thread*)."

### File yang menjadi tanggung jawabmu:
* `ui/CashNovaViewModel.kt`: Tempat semua logika dan variabel State (kondisi aplikasi saat ini) berada.
* `data/CashNovaRepository.kt`: Perantara antara ViewModel dan Database, juga mengurus penyimpanan sesi login ringan (*SharedPreferences*).
* `data/Models.kt`: Kelas cetakan data (seperti `CashNovaUiState`) yang menyatukan semua informasi untuk ditampilkan ke layar.

---

## 👩‍🎨 Anggota 3: UI Core & Visualisasi (Bagian "Frontend A")

**Tugas Utama:** Kamu adalah "desainer antarmuka utama". Tugasmu mengambil data matang dari Anggota 2 (ViewModel) dan menyajikannya menjadi tampilan yang cantik, modern, dan interaktif menggunakan Jetpack Compose.

### Apa yang harus kamu jelaskan saat presentasi?

1. **Penggunaan Jetpack Compose:**
   * *Penjelasan:* "Untuk membuat tampilan, saya tidak lagi menggunakan file XML kuno. Saya menggunakan **Jetpack Compose** di mana tampilan dibangun menggunakan kode Kotlin (Deklaratif). Jika status data berubah (misalnya saldo bertambah), Compose akan secara cerdas hanya menggambar ulang bagian teks saldo tersebut, bukan memuat ulang seluruh layar."
2. **Dashboard yang Dinamis:**
   * *Penjelasan:* "Pada `DashboardScreen`, saya membuat fitur *Multi-Wallet*. Pengguna bisa menggeser kartu dompet, dan saat dompet berubah, daftar riwayat transaksi di bawahnya akan langsung berubah menyesuaikan dompet yang sedang dipilih. Ini memberikan pengalaman pengguna (UX) yang sangat mulus."
3. **Komponen yang Bisa Dipakai Ulang (Reusable Components):**
   * *Penjelasan:* "Agar kode tidak panjang dan berulang, saya memisahkan elemen desain ke dalam `CommonComponents.kt`. Misalnya, desain kartu transaksi (dengan ikon bundar, judul, dan nominal warna merah/hijau) saya buat menjadi satu komponen. Komponen ini kemudian tinggal dipanggil di layar Dashboard, layar Wallet, maupun layar Analytics."

### File yang menjadi tanggung jawabmu:
* `ui/screens/DashboardScreen.kt`: Halaman utama aplikasi dengan ringkasan saldo.
* `ui/screens/AnalyticsScreen.kt`: Halaman laporan grafik dan statistik pengeluaran.
* `ui/components/CommonComponents.kt`: Kumpulan desain kartu, tombol, dan elemen visual lainnya.

---

## 🦸‍♂️ Anggota 4: Navigasi, Auth, & UX (Bagian "Frontend B")

**Tugas Utama:** Kamu adalah "pengatur lalu lintas" aplikasi. Tugasmu memastikan pengguna bisa berpindah dari satu layar ke layar lain tanpa tersesat, mengatur sistem pendaftaran (Register) & masuk (Login), serta memberikan pesan umpan balik (seperti pesan sukses/gagal).

### Apa yang harus kamu jelaskan saat presentasi?

1. **Sistem Routing (Navigasi Antar Halaman):**
   * *Penjelasan:* "Saya menggunakan `NavHost` pada `CashNovaApp.kt` untuk mengatur semua jalan di aplikasi. Saya mengatur logika cerdas: Jika pengguna baru pertama kali menginstal, mereka diarahkan ke layar `Onboarding`. Jika mereka mencentang 'Remember Me' saat login, aplikasi akan langsung melompat ke `Dashboard` tanpa meminta password lagi di kemudian hari."
2. **Validasi Formulir & Snackbar (Umpan Balik Pengguna):**
   * *Penjelasan:* "Pada halaman `RegisterScreen`, saya mengatur validasi keamanan. Jika PIN dan konfirmasi PIN tidak sama, sistem tidak akan mengirim data ke database. Sebaliknya, saya memunculkan pesan peringatan di bawah layar menggunakan `Snackbar` yang berjalan di atas teknologi `Coroutine` agar tidak membuat aplikasi *freeze* (macet)."
3. **Pembersihan Tumpukan Halaman (Backstack Management):**
   * *Penjelasan:* "Saya sangat memperhatikan UX. Setelah pengguna berhasil mendaftar, mereka dipindahkan ke halaman Login. Saat itu juga, saya menghapus halaman Register dari *sejarah navigasi* (pop backstack). Jadi, saat pengguna di halaman Login dan menekan tombol 'Kembali' di HP-nya, aplikasi akan tertutup, bukannya mundur secara aneh ke halaman pendaftaran lagi."

### File yang menjadi tanggung jawabmu:
* `ui/CashNovaApp.kt`: Pusat pengendali rute (NavHost) seluruh aplikasi.
* `ui/screens/LoginScreen.kt` & `RegisterScreen.kt`: Tampilan dan validasi gerbang masuk aplikasi.
* `ui/screens/OnboardingScreen.kt` & `SettingsScreen.kt`: Layar perkenalan dan pengaturan profil pengguna (termasuk fitur Logout).

---

## 🎓 Skenario Presentasi Ideal (Demo Flow)

Agar presentasi memukau dosen, lakukan demo dengan urutan berikut, dan biarkan masing-masing anggota menjelaskan saat bagiannya didemokan:

1. **Mulai dari awal (Anggota 4 menjelaskan):** Buka aplikasi. Tunjukkan layar Onboarding. Jelaskan sistem *routing*. Lakukan pendaftaran akun baru, tunjukkan validasi error jika PIN salah, lalu tunjukkan *Snackbar* sukses.
2. **Login & Arsitektur Data (Anggota 1 & 2 menjelaskan):** Lakukan login. Jelaskan bahwa data pengguna yang baru mendaftar ini sudah tersimpan secara permanen di SQLite Room, dan ViewModel telah menginisialisasi dompet bawaan untuknya.
3. **Eksplorasi Fitur (Anggota 3 menjelaskan):** Tambahkan beberapa transaksi (pemasukan dan pengeluaran). Tunjukkan bagaimana angka di layar berubah secara *real-time* (animasi Compose). Tunjukkan pembuatan dompet baru dan perpindahan kartu.
4. **Pembuktian Multi-User (Anggota 1 & 4 menjelaskan):** Pergi ke halaman Settings. Tekan tombol **Logout**. Daftar/Login dengan nama pengguna *lain*. Tunjukkan bahwa dashboard sekarang kosong (tidak ada transaksi milik pengguna sebelumnya). Ini adalah nilai jual utama (Multi-User Isolation) yang membuktikan database dirancang dengan sempurna!

*Selamat mempresentasikan hasil kerja keras kelompok kalian! Semoga mendapatkan nilai A! 🚀*
