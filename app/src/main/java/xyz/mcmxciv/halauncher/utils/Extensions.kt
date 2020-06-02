package xyz.mcmxciv.halauncher.utils

import android.widget.TextView

val TextView.value: String
    get() = text.toString()
