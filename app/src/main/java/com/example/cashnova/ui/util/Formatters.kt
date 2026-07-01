package com.example.cashnova.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

private val indonesiaLocale: Locale =
    Locale.forLanguageTag("id-ID")

fun formatMoney(
    amount: Double,
    showCents: Boolean = false
): String {

    val formatter =
        NumberFormat.getCurrencyInstance(
            indonesiaLocale
        ).apply {

            currency =
                Currency.getInstance("IDR")

            minimumFractionDigits =
                if (showCents) 2 else 0

            maximumFractionDigits =
                if (showCents) 2 else 0
        }

    /*
     * Beberapa perangkat menghasilkan non-breaking space
     * antara simbol Rp dan angka.
     */
    return formatter
        .format(amount)
        .replace('\u00A0', ' ')
}

fun formatTransactionDate(
    timestamp: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMMM yyyy, HH:mm",
            indonesiaLocale
        )

    return formatter.format(
        Date(timestamp)
    )
}