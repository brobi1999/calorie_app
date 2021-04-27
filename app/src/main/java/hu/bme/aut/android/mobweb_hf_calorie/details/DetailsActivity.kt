package hu.bme.aut.android.mobweb_hf_calorie.details

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room



import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEventDatabase
import kotlinx.android.synthetic.main.activity_details.*


class DetailsActivity : AppCompatActivity() {
    lateinit var calEvent : CalorieEvent
    lateinit var caleEventList : MutableList<CalorieEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        var bundle = intent.extras?.get("MY_BUNDLE") as Bundle
        calEvent = bundle.get("KEY_EVENT") as CalorieEvent

        var arrayOfA = (bundle.get("KEY_EVENT_LIST") as Array<Parcelable>).map { it as CalorieEvent }?.toTypedArray()
        caleEventList = arrayOfA?.toMutableList()



    }

    override fun onResume() {
        super.onResume()
        val detailsPagerAdapter = DetailsPagerAdapter(supportFragmentManager, this, calEvent, caleEventList)
        mainViewPager.adapter = detailsPagerAdapter


    }




}