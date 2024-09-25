//package com.example.weatherviewer.data.database.dao.converters
//
//import androidx.room.TypeConverter
//import com.example.weatherviewer.data.models.ForecastResponse
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class ConditionConverter {
//
//    @TypeConverter
//    fun fromCondition(condition: ForecastResponse.Current.Condition?): String? {
//        return Gson().toJson(condition)
//    }
//
//    @TypeConverter
//    fun toCondition(conditionString: String?): ForecastResponse.Current.Condition? {
//        return if (conditionString == null) {
//            null
//        } else {
//            val type = object : TypeToken<ForecastResponse.Current.Condition>() {}.type
//            Gson().fromJson(conditionString, type)
//        }
//    }
//}