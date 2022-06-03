package com.example.boardgamecollector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import java.util.concurrent.Executors

class GameRankAdapter : RecyclerView.Adapter<GameRankAdapter.DateRankViewHolder>() {

    private var dateRankList: ArrayList<DateRank> = ArrayList()

    fun addGames(dateRanks: ArrayList<DateRank>) {
        this.dateRankList = dateRanks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateRankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_items_gameranks, parent, false)
        return DateRankViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateRankViewHolder, position: Int) {
        val daterank = dateRankList[position]
        holder.bindView(daterank)
    }

    override fun getItemCount(): Int {
        return dateRankList.size
    }


    class DateRankViewHolder(var view: View): RecyclerView.ViewHolder(view){
        private var date = view.findViewById<TextView>(R.id.date)
        private var rank = view.findViewById<TextView>(R.id.rank)

        fun bindView(daterank: DateRank){
            date.text = daterank.date
            rank.text = daterank.rank.toString()
        }
    }
}