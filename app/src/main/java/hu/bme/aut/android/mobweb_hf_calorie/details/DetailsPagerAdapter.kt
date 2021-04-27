package hu.bme.aut.android.mobweb_hf_calorie.details

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import hu.bme.aut.android.mobweb_hf_calorie.fragments.DescriptionFragment
import hu.bme.aut.android.mobweb_hf_calorie.fragments.DiagramFragment
import hu.bme.aut.android.mobweb_hf_calorie.fragments.WebFragment

class DetailsPagerAdapter(fragmentManager: FragmentManager, private val context: Context, var calorieEvent: CalorieEvent, var calevlist: MutableList<CalorieEvent>) :
    FragmentPagerAdapter(fragmentManager) {



    override fun getItem(position: Int): Fragment {
        if(!calorieEvent.type.name.equals("Workout"))
            return when (position) {
                0 ->
                    DescriptionFragment.newInstance(calorieEvent.description)


                1 -> DiagramFragment.newInstance(calorieEvent,calevlist)
                2 -> WebFragment.newInstance(calorieEvent.name)
                else -> throw IllegalStateException("There is no fragment with this position: $position")
            }
        else
            return when (position) {
                0 ->
                    DescriptionFragment.newInstance(calorieEvent.description)
                1 -> DiagramFragment.newInstance(calorieEvent,calevlist)
                else -> throw IllegalStateException("There is no fragment with this position: $position")
            }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(!calorieEvent.type.name.equals("Workout"))
            return when (position) {
                0 -> context.getString(R.string.description)
                1 -> context.getString(R.string.diagram)
                2->context.getString(R.string.web)
                else -> ""
            }
        else
            return when (position) {
                0 -> context.getString(R.string.description)
                1 -> context.getString(R.string.diagram)
                else -> ""
        }
    }

    override fun getCount(): Int{
        if(!calorieEvent.type.name.equals("Workout"))
            return 3
        else
            return 2
    }
}