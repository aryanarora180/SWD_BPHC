package `in`.ac.bits_hyderabad.swd.swd.helper

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateFormatter {

    companion object {
        const val FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR = 1001
        const val FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME = 1002
    }

    private val fullMonthNameDayFullYearFormatter = DateTimeFormatter.ofPattern("MMMM dd, YYYY")
    private val halfDayNameHalfMonthDayTimeFormatter =
        DateTimeFormatter.ofPattern("E, MMM dd hh:mm a")

    fun getFormattedDate(date: String, type: Int = FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR): String {
        return try {
            when (type) {
                FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR -> LocalDate.parse(date)
                    .format(fullMonthNameDayFullYearFormatter)
                FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME -> LocalDate.parse(date)
                    .format(halfDayNameHalfMonthDayTimeFormatter)
                else -> LocalDateTime.parse(date).format(fullMonthNameDayFullYearFormatter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid date"
        }
    }

    fun getFormattedDate(date: Long, type: Int = FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR): String {
        return try {
            when (type) {
                FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR -> Instant.ofEpochMilli(
                    date
                ).atZone(ZoneId.systemDefault()).format(fullMonthNameDayFullYearFormatter)
                FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME -> Instant.ofEpochMilli(
                    date
                ).atZone(ZoneId.systemDefault()).format(halfDayNameHalfMonthDayTimeFormatter)
                else -> Instant.ofEpochMilli(
                    date
                ).atZone(ZoneId.systemDefault()).format(fullMonthNameDayFullYearFormatter)
            }
        } catch (e: Exception) {
            "Invalid date"
        }
    }
}