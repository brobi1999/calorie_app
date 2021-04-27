package hu.bme.aut.android.mobweb_hf_calorie.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.android.mobweb_hf_calorie.MyDate
import hu.bme.aut.android.mobweb_hf_calorie.MyTime

@Database(entities = [CalorieEvent::class], version = 1)
@TypeConverters(value = [CalorieEvent.Type::class,MyDate::class,MyTime::class])

abstract class CalorieEventDatabase : RoomDatabase() {
    abstract fun calorieEventDao(): CalorieEventDao
}