package hu.bme.aut.android.mobweb_hf_calorie

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
class MyTime (var h: Int, var m: Int) : Parcelable {
    val hour: Int = h;
    val minute: Int = m;
    lateinit var displayTime: String
    init{
        var displayHour = String.format("%02d", hour)
        var displayMinute = String.format("%02d", minute)
        displayTime = displayHour + ":" + displayMinute
    }

    companion object {
        @JvmStatic
        @TypeConverter
        fun stringToMyTime(dateString: String): MyTime {
            var elemek = dateString.split(":")
            var _hour = Integer.parseInt(elemek[0])
            var _minute = Integer.parseInt(elemek[1])

            return  MyTime(_hour, _minute)



        }

        @JvmStatic
        @TypeConverter
        fun myTimeToString(t: MyTime): String {
            return "${t.hour}:${t.minute}"
        }
    }
}