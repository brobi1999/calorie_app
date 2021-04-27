package hu.bme.aut.android.mobweb_hf_calorie

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
class MyDate(var y: Int, var m: Int, var d:Int) : Parcelable {
    val year: Int = y;
    val month: Int = m;
    val day: Int = d;

    lateinit var displayDate: String
    init{

        var displayYear = String.format("%04d", year)
        var displayMonth = String.format("%02d", month)
        var displayDay = String.format("%02d", day)
        displayDate = displayYear + "." + displayMonth + "." + displayDay

    }


    companion object {
            @JvmStatic
            @TypeConverter
            fun stringToMyDate(dateString: String): MyDate {
                var elemek = dateString.split(".")
                var _year = Integer.parseInt(elemek[0])
                var _month = Integer.parseInt(elemek[1])
                var _day = Integer.parseInt(elemek[2])
                return  MyDate(_year, _month,_day)



            }

            @JvmStatic
            @TypeConverter
            fun myDateToString(d: MyDate): String {
                return "${d.year}.${d.month}.${d.day}"
            }
        }
    }