package ds.meterscanner.ui

import L
import android.annotation.SuppressLint
import android.app.Activity
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.erased.instance
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import ds.meterscanner.R
import java.util.*

@SuppressLint("WrongConstant")
object DatePickers {
    fun pickDateTime(activity: Activity, initialDate: Date = Date(), callback: (Date) -> Unit) {
        val currCalendar: Calendar = activity.appKodein().instance()
        currCalendar.time = initialDate

        val cal: Calendar = activity.appKodein().instance()

        val dpd = DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            L.v("year=$year month=$monthOfYear day=$dayOfMonth")
            val tpd = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                L.v("h=$hourOfDay m=$minute s=$second")
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.YEAR, year)
                callback(cal.time)
            },
                currCalendar.get(Calendar.HOUR_OF_DAY),
                currCalendar.get(Calendar.MINUTE), true
            )
            tpd.setTimeInterval(1, 5)
            tpd.show(activity.fragmentManager, activity.getString(R.string.time))
        },
            currCalendar.get(Calendar.YEAR),
            currCalendar.get(Calendar.MONTH),
            currCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.firstDayOfWeek = Calendar.MONDAY
        dpd.show(activity.fragmentManager, activity.getString(R.string.date))
        //  dpd.setSelectableDays(days);    // todo for charts
    }

    fun pickTime(activity: Activity, initialDate: Date = Date(), callback: (hour: Int, minute: Int) -> Unit) {
        val currCalendar: Calendar = activity.appKodein().instance()
        currCalendar.time = initialDate

        val tpd = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
            L.v("h=$hourOfDay m=$minute s=$second")
            callback(hourOfDay, minute)
        },
            currCalendar.get(Calendar.HOUR_OF_DAY),
            currCalendar.get(Calendar.MINUTE), true
        )
        tpd.setTimeInterval(1, 5)
        tpd.show(activity.fragmentManager, activity.getString(R.string.time))
    }
}