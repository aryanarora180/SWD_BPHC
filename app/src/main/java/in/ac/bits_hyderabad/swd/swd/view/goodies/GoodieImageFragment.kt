package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoodieImageFragment : Fragment(R.layout.fragment_goodie_image) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<ImageView>(R.id.goodie_image)
            .load(arguments?.getString(IMAGE_LINK) ?: "")
    }

    companion object {
        fun newInstance(imageLink: String): GoodieImageFragment {
            val args = Bundle()
            args.putString(IMAGE_LINK, imageLink)

            val fragment = GoodieImageFragment()
            fragment.arguments = args
            return fragment
        }

        const val IMAGE_LINK = "image-link"
    }
}