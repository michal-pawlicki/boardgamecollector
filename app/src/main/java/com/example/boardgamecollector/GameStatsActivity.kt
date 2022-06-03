package com.example.boardgamecollector

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import java.util.concurrent.Executors

class GameStatsActivity : AppCompatActivity() {
    private var gameid: Long = 0
    private lateinit var text: TextView
    private lateinit var game_name: TextView
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private var adapter: GameRankAdapter? = null
    private var dbHandler = MyDBHandler(this, null, null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_stats)
        gameid = intent.getLongExtra("id",0)
        var game = dbHandler.findGameById(gameid)
        game_name = findViewById(R.id.game_name)
        game_name.text = game?.gameName
        val username = dbHandler.getUserData()
        text = findViewById(R.id.username)
        text.text = username
        recyclerView = findViewById(R.id.recyclerView)
        imageView = findViewById(R.id.image)
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap?
        executor.execute {
            val imageURL = game?.image
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
        viewStats()
    }

    fun userSettings(view: View) {
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun viewStats(){
        val gameRankList = dbHandler.findDateRanksById(gameid)
        adapter = GameRankAdapter()
        adapter?.addGames(gameRankList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}

