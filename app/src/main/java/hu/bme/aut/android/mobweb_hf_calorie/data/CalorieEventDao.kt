package hu.bme.aut.android.mobweb_hf_calorie.data

import androidx.room.*

@Dao
interface CalorieEventDao {
    @Query("SELECT * FROM calorieevent")
    fun getAll(): List<CalorieEvent>

    @Insert
    fun insert(calorieEvent: CalorieEvent): Long

    @Update
    fun update(calorieEvent: CalorieEvent?)

    @Delete
    fun deleteItem(calorieEvent: CalorieEvent?)

    @Query("DELETE FROM calorieevent")
    fun deleteAll()

}