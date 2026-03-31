package com.example.rampu2506_padeler.converters

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long{
        return date.time
    }
    @TypeConverter
    fun toDate(timeStamp: Long): Date{
        return Date(timeStamp)
    }
}