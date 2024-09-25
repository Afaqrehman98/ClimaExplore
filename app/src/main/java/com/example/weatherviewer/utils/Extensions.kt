package com.example.weatherviewer.utils

import com.google.gson.Gson

inline fun <reified T : Any> String.fromPrettyJson(): T = Gson().fromJson(this, T::class.java)