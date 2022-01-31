package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.GoodieSales
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.ViewSalesViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import coil.load
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_sales_fragment.*

@AndroidEntryPoint
class ViewSalesFragment : Fragment(R.layout.view_sales_fragment) {

    private val viewModel by viewModels<ViewSalesViewModel>()
    private val args by navArgs<ViewSalesFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGoodieSales(args.goodie.id.toInt())

        goodie_image.load(args.goodie.images[0])
        goodie_name_text.text = args.goodie.name
        goodie_hoster_text.text = args.goodie.hostName
        goodie_price_text.text = args.goodie.getDisplayPrice()

        total_sales_amount_ticker.setCharacterLists(TickerUtils.provideNumberList())
        total_quantity_ticker.setCharacterLists(TickerUtils.provideNumberList())
        total_sales_amount_ticker.text = viewModel.formatAmount(0)
        total_quantity_ticker.text = 0.toString()

        viewModel.goodieSales.observe(viewLifecycleOwner, goodieSalesObserver)
        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
    }

    private val goodieSalesObserver = Observer<GoodieSales> { sales ->
        total_sales_amount_ticker.text = viewModel.formatAmount(sales.totalAmount)
        total_quantity_ticker.text = sales.netQuantity.toString()
        if (args.goodie.type == 0)
            size_details_text.text = viewModel.getFormattedSizes()
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            loading_details_progress.visibility = View.VISIBLE
            size_details_text.visibility = View.GONE
        } else {
            loading_details_progress.visibility = View.GONE
            size_details_text.visibility = View.VISIBLE
        }
    }

    private val onErrorMessageObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

}