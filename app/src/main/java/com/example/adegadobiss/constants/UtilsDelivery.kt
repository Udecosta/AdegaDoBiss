package com.example.adegadobiss.constants

import kotlin.math.*

class UtilsDelivery {

    fun calcDelivery(
        latLocal: Double,
        lngLocal: Double,
        latInicial: Double,
        lngInicial: Double
    ): Double {

        val KM: Double = 6371.0
        val latDistance = Math.toRadians(latLocal - latInicial)
        val lngDistance = Math.toRadians(lngLocal - lngInicial)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(latLocal)) * cos(Math.toRadians(latInicial)) *
                sin(lngDistance / 2) * sin(lngDistance / 2)
        val c: Double = 2 * atan2(sqrt(a), sqrt(1 - a))

        val result: Long = ((KM * c).roundToLong())
        var valorFrete: Double = result.toDouble()


        when {
            result <= 0 -> {
                valorFrete = 1.00
            }
            result > 1 -> {
                valorFrete *= 1.25
            }
            result > 5 -> {
                valorFrete *= 1.50
            }
        }

        var total: Double = 0.00
        for (i in 0 until Database.dbProducts.size) {
            total += Database.dbProducts[i].subtotal + valorFrete
            Database.dbProducts[i].total = total
        }
        return valorFrete
    }

    fun calcHourDelivery(dayOfWeak: String, hour: String): Boolean {
        return dayOfWeak.toString() == "TUESDAY" && hour > "16:00:00" && hour < "24:00:00" ||
                dayOfWeak.toString() == "WEDNESDAY" && hour > "16:00:00" && hour < "24:00:00" ||
                dayOfWeak.toString() == "THURSDAY" && hour > "16:00:00" && hour < "24:00:00" ||
                dayOfWeak.toString() == "FRIDAY" && hour > "16:00:00" && hour < "02:00:00" ||
                dayOfWeak.toString() == "SATURDAY" && hour > "16:00:00" && hour < "02:00:00" ||
                dayOfWeak.toString() == "SUNDAY" && hour > "14:00:00" && hour < "00:00:00"
    }
}