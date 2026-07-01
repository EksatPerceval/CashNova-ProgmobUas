# CashSnova Complete

Project ini mengembangkan tampilan awal CashSnova menjadi aplikasi Jetpack Compose yang dapat digunakan.

## Fitur

- Onboarding.
- Dashboard sesuai referensi Figma.
- Ringkasan saldo, pemasukan, dan pengeluaran yang dihitung otomatis.
- Daftar sumber pendapatan dinamis.
- Halaman saving goals.
- Tambah target tabungan.
- Setor ke target tabungan dengan pilihan kategori.
- Halaman wallet (Multi-wallet support).
- Tambah, pilih, dan hapus wallet.
- Tambah transaksi pemasukan atau pengeluaran.
- Detail dan hapus transaksi.
- Ubah nama profil.
- Support Dark Mode & Light Mode.
- Grafik Keuangan dengan filter rentang waktu dan wallet.
- Data tersimpan lokal memakai Room Database + SharedPreferences.
- Reset demo data.

## Cara memasang pada project yang sudah ada

Cara paling aman:

1. Tutup Android Studio.
2. Backup project lama.
3. Salin folder `app/src/main/java/com/example/cashnova` dari paket ini ke project lama.
4. Salin `app/src/main/AndroidManifest.xml`.
5. Salin resource pada `app/src/main/res/values`.
6. Cocokkan dependency pada `app/build.gradle.kts`.
7. Sync Gradle lalu jalankan aplikasi.

Atau buka folder ini langsung sebagai project Android Studio.

## Catatan build

- JDK: 17
- compileSdk: 36
- targetSdk: 36
- minSdk: 24
- AGP: 9.2.1
- Gradle: 9.4.1
- Kotlin: 2.3.21

Project zip tidak menyertakan file biner `gradle-wrapper.jar`. Android Studio dapat membuat atau memperbarui wrapper saat project dibuka. Bila wrapper lama bermasalah, jalankan:

```bash
gradle wrapper --gradle-version 9.4.1
```

## Struktur utama

```text
com.example.cashnova
├── MainActivity.kt
├── data
│   ├── Models.kt
│   └── CashNovaRepository.kt
└── ui
    ├── CashNovaApp.kt
    ├── CashNovaViewModel.kt
    ├── components
    ├── screens
    ├── theme
    └── util
```

AGP 9 menggunakan built-in Kotlin, sehingga plugin `org.jetbrains.kotlin.android` tidak dipasang lagi.
