package com.example.boardgamecollector

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var text: TextView
    private lateinit var searchbar: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: ImageView
    private lateinit var sort: Spinner
    private lateinit var option: String
    private var name = ""
    private var howtosort = 0
    private var dbHandler = MyDBHandler(this, null, null, 1)
    private var adapter: GamesAdapter? = null
    private var username:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        username = dbHandler.getUserData()
        text = findViewById(R.id.username)
        text.text = username
        recyclerView = findViewById(R.id.recyclerView)
        search = findViewById(R.id.search)
        sort = findViewById(R.id.sortspinner)
        search.setOnClickListener{ searchGames() }
        searchbar = findViewById(R.id.search_bar)
        searchbar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchGames()
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                    hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                searchbar.clearFocus()
                searchbar.isCursorVisible = false
                return@OnKeyListener true
            }
            false
        })

        val myAdapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinneroptions)
        )
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sort.adapter = myAdapter

        sort.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                option = sort.selectedItem.toString()
                if (option == "Rank ↑")
                {
                    howtosort = 0
                }
                if (option == "Rank ↓")
                {
                    howtosort = 1
                }
                if (option == "Name ↑")
                {
                    howtosort = 2
                }
                if (option == "Name ↓")
                {
                    howtosort = 3
                }
                if (option == "Release date ↑")
                {
                    howtosort = 4
                }
                if (option == "Release date ↓")
                {
                    howtosort = 5
                }
                if(name.isNullOrEmpty()){
                    viewGames()
                } else{
                    searchGames()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        viewGames()
    }


    fun deletefilter(v: View){
        name = ""
        viewGames()
    }

    fun userSettings(view: View) {
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun searchGames(){

        if( searchbar.text.isNotEmpty()){
            name = searchbar.text.toString()
            searchbar.setText("")
            searchbar.requestFocus()
            val gamesList = dbHandler.findGames(name, howtosort)
            adapter = GamesAdapter()
            adapter?.addGames(gamesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter!!.setOnItemClickListener(object: GamesAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@MainActivity,GameStatsActivity::class.java)
                    intent.putExtra("id", gamesList[position].id)
                    startActivity(intent)
                }
            })

            return
        }
        if(name.isNotEmpty()){
            val gamesList = dbHandler.findGames(name, howtosort)
            adapter = GamesAdapter()
            adapter?.addGames(gamesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter!!.setOnItemClickListener(object: GamesAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@MainActivity,GameStatsActivity::class.java)
                    intent.putExtra("id", gamesList[position].id)
                    startActivity(intent)
                }
            })

        } else {
            Toast.makeText(this, "Enter the name you want to search", Toast.LENGTH_SHORT).show()
        }
    }


    private fun viewGames(){
        val gamesList = dbHandler.getAllGames(howtosort)
        adapter = GamesAdapter()
        adapter?.addGames(gamesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter!!.setOnItemClickListener(object: GamesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity,GameStatsActivity::class.java)
                intent.putExtra("id", gamesList[position].id)
                startActivity(intent)
            }
        })
    }

}