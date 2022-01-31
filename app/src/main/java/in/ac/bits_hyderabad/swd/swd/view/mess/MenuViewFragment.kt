package `in`.ac.bits_hyderabad.swd.swd.view.mess

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.MessMenu
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_menu_fragment.*

@AndroidEntryPoint
class MenuViewFragment : Fragment(R.layout.view_menu_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        day_header_text.text = requireArguments().getString(KEY_DAY)

        breakfast_menu_text.text =
            requireArguments().getParcelable<MessMenu>(KEY_MENU)?.breakfast ?: ""
        lunch_menu_text.text =
            requireArguments().getParcelable<MessMenu>(KEY_MENU)?.lunch ?: ""
        snacks_menu_text.text =
            requireArguments().getParcelable<MessMenu>(KEY_MENU)?.snacks ?: ""
        dinner_menu_text.text =
            requireArguments().getParcelable<MessMenu>(KEY_MENU)?.dinner ?: ""
    }

    companion object {
        private const val KEY_DAY = "day"
        private const val KEY_MENU = "menu"

        fun newInstance(day: String, menu: MessMenu): MenuViewFragment {
            val args = Bundle().apply {
                putString(KEY_DAY, day)
                putParcelable(KEY_MENU, menu)
            }
            val frag = MenuViewFragment()
            frag.arguments = args
            return frag
        }
    }

}