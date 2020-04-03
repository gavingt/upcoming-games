package com.gavinsappcreations.upcominggames.database

import androidx.room.TypeConverter

/**
 * These converters allow us to store a list of platforms in a single cell in SQLite. We use commas
 * as delimiters, and we put commas before the first item and after the last item as well. This
 * allows us to use SQLite LIKE clauses in the form of "LIKE '%,${platformAbbreviation},%'" to
 * return only games on specific platforms.
 */
class Converters {

    @TypeConverter
    fun listToString(list: List<String>?): String {

        if (list == null) {
            return ",NONE,"
        }

        val stringBuilder = StringBuilder()
        stringBuilder.append(",")
        for (item in list) {
            stringBuilder.append(item).append(",")
        }
        return stringBuilder.toString()
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.removePrefix(",").removeSuffix(",").split(",")
    }
}