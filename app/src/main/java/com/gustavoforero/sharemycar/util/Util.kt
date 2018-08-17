package com.gustavoforero.sharemycar.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Util {


    fun getDate(dateToFormat: String?): String {
        var out = dateToFormat ?: ""
        val input = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val output = SimpleDateFormat("MMM dd 'del' yyyy", Locale.getDefault())
        try {
            val date = input.parse(dateToFormat)                 // parse input
            out = output.format(date)    // format output
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return out
    }


}