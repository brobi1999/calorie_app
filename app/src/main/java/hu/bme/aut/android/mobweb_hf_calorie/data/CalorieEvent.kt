package hu.bme.aut.android.mobweb_hf_calorie.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import hu.bme.aut.android.mobweb_hf_calorie.MyDate
import hu.bme.aut.android.mobweb_hf_calorie.MyTime
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "calorieevent")
data class CalorieEvent(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "type") var type: Type,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "calorie_count")var calorieCount: Int,
    @ColumnInfo(name = "date") var date: MyDate,
    @ColumnInfo(name = "time") var time: MyTime
) : Parcelable {
    init{
        if(type.name == "Workout"){
            calorieCount *=-1;
        }
    }


    enum class Type {
        Workout, Breakfast, Lunch,Dinner,Snack;
        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): Type? {
                var ret: Type? = null
                for (cat in values()) {
                    if (cat.ordinal == ordinal) {
                        ret = cat
                        break
                    }
                }
                return ret
            }

            @JvmStatic
            @TypeConverter
            fun toInt(category: Type): Int {
                return category.ordinal
            }
        }
    }


    }



