package com.gustavoforero.sharemycar.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.domain.Trip
import kotlinx.android.synthetic.main.item_post.view.*
import android.text.style.UnderlineSpan
import android.text.SpannableString




class SearchTripsAdapter(val mContext: Context, var listener: OnPhonePressed?) : RecyclerView.Adapter<SearchTripsAdapter.ItemViewHolder>() {


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

        p0.loadItem(trips.get(p1), listener)
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun loadItem(trip: Trip, listener: OnPhonePressed?) {
            itemView.lab_cities.text = "${trip.origen} - ${trip.destino}"
            itemView.lab_price.text = trip.precio
            val content = SpannableString(trip.phone)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            itemView.lab_phone.text = content
            itemView.lab_contact.text = trip.name
            itemView.lab_hour.text = itemView.lab_seats.context.getString(R.string.lab_departure, trip.hora)
            itemView.lab_seats.text = itemView.lab_seats.context.getString(R.string.lab_seats, trip.cupos.toString())
            itemView.lab_date.text = com.gustavoforero.sharemycar.util.Util.getDate(trip.fecha)
            itemView.lab_phone.setOnClickListener {
                listener?.run {
                    onPhonePressedListener(trip)
                }
            }
        }
    }


    interface OnPhonePressed {

        fun onPhonePressedListener(trip: Trip)
    }
}