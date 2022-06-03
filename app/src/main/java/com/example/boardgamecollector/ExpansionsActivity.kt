package com.example.boardgamecollector

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList
import android.util.AttributeSet
import android.widget.*
import kotlin.math.round

class ExpansionsActivity : AppCompatActivity() {
    private lateinit var text: TextView
    private lateinit var searchbar: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: ImageView
    private lateinit var sort: Spinner
    private lateinit var option: String
    private var name = ""
    private var howtosort = 2
    private var dbHandler = MyDBHandler(this, null, null, 1)
    private var adapter: ExpansionsAdapter? = null
    private var username:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expansions)
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
            this@ExpansionsActivity,
            android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinneroptions2)
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
                    viewExpansions()
                } else{
                    searchGames()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        viewExpansions()
    }


    fun deletefilter(v: View){
        viewExpansions()
    }

    fun userSettings(view: View) {
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun searchGames(){
        if( name.isNotEmpty()){
            val gamesList = dbHandler.findExpansions(name, howtosort)
            adapter = ExpansionsAdapter()
            adapter?.addGames(gamesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            return
        }
        if(searchbar.text.isEmpty()){
            Toast.makeText(this, "Enter the name you want to search", Toast.LENGTH_SHORT).show()
        } else {
            name = searchbar.text.toString()
            searchbar.setText("")
            searchbar.requestFocus()
            val gamesList = dbHandler.findExpansions(name, howtosort)
            adapter = ExpansionsAdapter()
            adapter?.addGames(gamesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }


    private fun viewExpansions(){
        val gamesList = dbHandler.getAllExpansions(howtosort)
        adapter = ExpansionsAdapter()
        adapter?.addGames(gamesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

}