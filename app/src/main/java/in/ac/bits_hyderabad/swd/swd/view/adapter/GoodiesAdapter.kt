package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.list_item_goodies.view.*

class GoodiesAdapter : RecyclerView.Adapter<GoodiesAdapter.ViewHolder>() {

    var data = listOf<Goodie>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var listener: (Goodie) -> Unit

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goodie = data[position]

        holder.nameText.text = goodie.name
        //holder.goodieImage.load(goodie.images[0])

        holder.priceText.text = goodie.getDisplayPrice()
        holder.hostText.text = goodie.hostOrganization

        holder.card.setOnClickListener {
            listener(goodie)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_goodies, parent, false)
    )

    override fun getItemCount() = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.name_text
        val goodieImage: ImageView = view.goodie_image
        val priceText: TextView = view.price_text
        val hostText: TextView = view.host_text
        val card: MaterialCardView = view.goodie_card
    }
}
