package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentMultipleImagesBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultipleImagesFragment : Fragment() {

    private lateinit var binding: FragmentMultipleImagesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultipleImagesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images = arguments?.getStringArray(ARGS_IMAGES) ?: arrayOf()

        with(binding) {
            if (images.isEmpty()) {
                imageViewpager.isGone = true
                pageIndicator.isGone = true
                noImagesImage.isVisible = true
                noImagesText.isVisible = true
            } else {
                pageIndicator.count = images.size

                imageViewpager.adapter = ImageStateAdapter(images)
                imageViewpager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        pageIndicator.selection = position
                    }
                })

                if (images.size == 1)
                    pageIndicator.isGone = true
            }
        }
    }

    inner class ImageStateAdapter(private val images: Array<String>) :
        FragmentStateAdapter(requireActivity()) {

        override fun createFragment(position: Int) =
            GoodieImageFragment.newInstance(images[position])

        override fun getItemCount() = images.size
    }

    companion object {
        fun newInstance(images: Array<String>): MultipleImagesFragment {
            val args = Bundle()
            args.putStringArray(ARGS_IMAGES, images)

            val fragment = MultipleImagesFragment()
            fragment.arguments = args
            return fragment
        }

        const val ARGS_IMAGES = "images"
    }
}