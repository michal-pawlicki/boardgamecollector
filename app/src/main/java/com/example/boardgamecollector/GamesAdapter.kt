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

class GamesAdapter : RecyclerView.Adapter<GamesAdapter.GamesViewHolder>() {

    private var gamesList: ArrayList<Game> = ArrayList()
    private lateinit var mListener: onItemClickListener

    fun addGames(games: ArrayList<Game>) {
        this.gamesList = games
    }

    interface onItemClickListener{

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_items_games, parent, false)
        return GamesViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val game = gamesList[position]
        holder.bindView(game)
    }

    override fun getItemCount(): Int {
       return gamesList.size
    }


    class GamesViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        private var rank = view.findViewById<TextView>(R.id.rank)
        private var gamename = view.findViewById<TextView>(R.id.game_name)
        private var rating = view.findViewById<TextView>(R.id.rating)
        private var releasedate = view.findViewById<TextView>(R.id.release_date)
        private var imageView = view.findViewById<ImageView>(R.id.image)

        init {
            view.setOnClickListener{listener.onItemClick(adapterPosition)}
        }

        fun bindView(game: Game){
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap?
            executor.execute {
                val imageURL = game.image
                try {
                    val `in` = URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        imageView.setImageBitmap(image)
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            rank.text = game.rank.toString()
            if(game.releaseDate == 0){
                releasedate.text = "?"
            } else{
                releasedate.text = game.releaseDate.toString()
            }
            gamename.text = game.gameName
            rating.text = game.rating.toString()

        }
    }
}