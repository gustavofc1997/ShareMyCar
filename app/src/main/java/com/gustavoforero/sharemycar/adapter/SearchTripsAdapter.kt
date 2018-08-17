package com.gustavoforero.sharemycar.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.domain.Trip
import kotlinx.android.synthetic.main.item_post.view.*

class SearchTripsAdapter(val mContext: Context) : RecyclerView.Adapter<SearchTripsAdapter.ItemViewHolder>() {


    var trips: ArrayList<Trip> = ArrayList()


    fun setItems(list: ArrayList<Trip>) {
        this.trips = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_post, p0, false)
        return SearchTripsAdapter.ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        p0.loadItem(trips.get(p1))
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun loadItem(trip: Trip) {
            itemView.lab_cities.text="${trip.origen} - ${trip.destino}"
            itemView.lab_price.text = trip.precio
            itemView.lab_phone.text = trip.phone
            itemView.lab_contact.text = trip.name
            itemView.lab_hour.text = trip.hora
            itemView.lab_date.text = com.gustavoforero.sharemycar.util.Util.getDate(trip.fecha)
        }
    }
}