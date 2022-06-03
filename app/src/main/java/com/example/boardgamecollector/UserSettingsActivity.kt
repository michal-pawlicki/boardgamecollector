package com.example.boardgamecollector

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

class UserSettingsActivity : AppCompatActivity() {
    private lateinit var text: TextView
    private lateinit var number: TextView
    private lateinit var date: TextView
    private lateinit var logout: Button
    private lateinit var showgames: Button
    private lateinit var showexpansions: Button
    private lateinit var refresh: Button
    private lateinit var progressBar: ProgressBar
    private val dbHandler = MyDBHandler(this, null, null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        text = findViewById(R.id.username)
        number = findViewById(R.id.number)
        logout = findViewById(R.id.logout)
        date = findViewById(R.id.date)
        showgames = findViewById(R.id.showgames)
        showexpansions = findViewById(R.id.showexpansions)
        progressBar = findViewById(R.id.progressBar)
        refresh = findViewById(R.id.refresh)

        viewData()

        logout.setOnClickListener{ alert() }
        refresh.setOnClickListener{ refresh() }
        showgames.setOnClickListener{ showgames() }
        showexpansions.setOnClickListener{ showexpansions() }

    }

    fun viewData(){
        val username = dbHandler.getUserData()
        val gamesnumber = dbHandler.getGamesNumber()
        val expnumber = dbHandler.getExpansionsNumber()
        var lastrefresh = dbHandler.getDate()
        if (lastrefresh.isNullOrBlank()){
            lastrefresh = "never"
        }
        text.text = username
        date.text = "Last refresh was performed\n $lastrefresh"
        number.text = "You own $gamesnumber games \n and $expnumber expansions"
    }

    private fun logOut() {
        dbHandler.deleteUserData()
        val intent = Intent(this, LoginActivity::class.java)
        val path = "$filesDir/XML"
        val file1 = File(path, "games.xml")
        val file2 = File(path, "expansions.xml")
        file1.delete()
        file2.delete()
        startActivity(intent)
        finish()
    }

    private fun showgames() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showexpansions() {
        val intent = Intent(this, ExpansionsActivity::class.java)
        startActivity(intent)
    }

    private fun alert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Log out")
        builder.setMessage("Do you want to log out?")

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener{
                _, _ -> logOut()
        })

        builder.setNegativeButton("No", DialogInterface.OnClickListener{
                dialog, _ -> dialog.dismiss()
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun loadData() {
        val filename = "games.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        val gamesList: ArrayList<Game> = ArrayList()
        val daterankList: ArrayList<DateRank> = ArrayList()

        if (inDir.exists()){
            val file = File(inDir, filename)
            if (file.exists()){
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for (i in 0 until items.length){
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        var id: Long = elem.getAttribute("objectid").toLong()
                        var rank: Int = 0
                        var releaseDate: Int = 0
                        var gameName: String = ""
                        var rating: Float = 0.0F
                        var image: String = ""

                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element){
                                when (node.nodeName){
                                    "name" -> {
                                        gameName = node.textContent
                                    }
                                    "yearpublished" -> {
                                        releaseDate = node.textContent.toInt()
                                    }
                                    "thumbnail" -> {
                                        image = node.textContent
                                    }
                                    "stats" -> {
                                        val children2 = node.childNodes
                                        for (k in 0 until children2.length) {
                                            val node2 = children2.item(k)
                                            if (node2 is Element) {
                                                when (node2.nodeName) {
                                                    "rating" -> {
                                                        val children3 = node2.childNodes
                                                        for (l in 0 until children3.length) {
                                                            val node3 = children3.item(l)
                                                            if (node3 is Element) {
                                                                when (node3.nodeName) {
                                                                    "average" ->{
                                                                        rating = node3.getAttribute("value").toFloat().round(1)
                                                                    }
                                                                    "ranks" -> {
                                                                        val children4 = node3.childNodes
                                                                        for (m in 0 until children4.length) {
                                                                            val node4 = children4.item(m)
                                                                            if (node4 is Element) {
                                                                                when (node4.nodeName) {
                                                                                    "rank" ->{
                                                                                        val type = node4.getAttribute("name")
                                                                                        if (type == "boardgame"){
                                                                                            val temprank = node4.getAttribute("value")
                                                                                            if (temprank == "Not Ranked"){
                                                                                                rank = 0
                                                                                            } else{
                                                                                                rank = temprank.toInt()
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val game = Game(id, rank, releaseDate, gameName, rating, image)
                        gamesList.add(game)
                        val sdf = SimpleDateFormat("HH:mm:ss dd/M/yyyy")
                        val currentDate = sdf.format(Date()).toString()
                        val daterank = DateRank(id, currentDate, rank)
                        daterankList.add(daterank)
                    }
                }
            }
        }
        dbHandler.saveGames(gamesList)
        dbHandler.saveDateRanks(daterankList)
    }

    fun loadExpansions() {
        val filename = "expansions.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        val expansionsList: ArrayList<Game> = ArrayList()

        if (inDir.exists()){
            val file = File(inDir, filename)
            if (file.exists()){
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for (i in 0 until items.length){
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        var id: Long = elem.getAttribute("objectid").toLong()
                        var rank: Int = 0
                        var releaseDate: Int = 0
                        var gameName: String = ""
                        var rating: Float = 0.0F
                        var image: String = ""

                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element){
                                when (node.nodeName){
                                    "name" -> {
                                        gameName = node.textContent
                                    }
                                    "yearpublished" -> {
                                        releaseDate = node.textContent.toInt()
                                    }
                                    "thumbnail" -> {
                                        image = node.textContent
                                    }
                                    "stats" -> {
                                        val children2 = node.childNodes
                                        for (k in 0 until children2.length) {
                                            val node2 = children2.item(k)
                                            if (node2 is Element) {
                                                when (node2.nodeName) {
                                                    "rating" -> {
                                                        val children3 = node2.childNodes
                                                        for (l in 0 until children3.length) {
                                                            val node3 = children3.item(l)
                                                            if (node3 is Element) {
                                                                when (node3.nodeName) {
                                                                    "average" ->{
                                                                        rating = node3.getAttribute("value").toFloat().round(1)
                                                                    }
                                                                    "ranks" -> {
                                                                        val children4 = node3.childNodes
                                                                        for (m in 0 until children4.length) {
                                                                            val node4 = children4.item(m)
                                                                            if (node4 is Element) {
                                                                                when (node4.nodeName) {
                                                                                    "rank" ->{
                                                                                        val type = node4.getAttribute("name")
                                                                                        if (type == "boardgame"){
                                                                                            val temprank = node4.getAttribute("value")
                                                                                            if (temprank == "Not Ranked"){
                                                                                                rank = 0
                                                                                            } else{
                                                                                                rank = temprank.toInt()
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val game = Game(id, rank, releaseDate, gameName, rating, image)
                        expansionsList.add(game)
                    }
                }
            }
        }
        dbHandler.saveExpansions(expansionsList)
    }

    @Suppress("DEPRECATION")
    private fun downloadData() {
        val cd = GamesDownloader()
        val cd2 = ExpansionsDownloader()
        cd.execute()
        cd2.execute()
    }

    @SuppressLint("SimpleDateFormat")
    fun refresh() {
        val sdf = SimpleDateFormat("HH:mm:ss dd/M/yyyy")
        val currentDate: Date = Date()
        val oldDate1 = dbHandler.getDate()
        if (oldDate1.isNullOrEmpty()){
            saveDate()
            return
        }
        val oldDate: Date = sdf.parse(oldDate1)
        val difference: Long = kotlin.math.abs(currentDate.time - oldDate.time) / (60 * 60 * 1000)
        if (difference < 24){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Refresh data")
            builder.setMessage("Do you want to refresh data? Less than 24 hours have passed since the last refresh.")

            builder.setPositiveButton("Yes", DialogInterface.OnClickListener{
                    _, _ -> saveDate()
            })

            builder.setNegativeButton("No", DialogInterface.OnClickListener{
                    dialog, _ -> dialog.dismiss()
            })
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        } else {
            saveDate()
        }
    }

    fun saveDate(){
        val sdf = SimpleDateFormat("HH:mm:ss dd/M/yyyy")
        val currentDate = sdf.format(Date())
        dbHandler.saveDate(currentDate)
        date.text = "Last refresh was performed\n $currentDate"
        downloadData()
    }

    private fun Float.round(decimals: Int): Float {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (kotlin.math.round(this * multiplier) / multiplier).toFloat()
    }

    @Suppress("DEPRECATION")
    private inner class GamesDownloader: AsyncTask<String, Int, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE
            loadData()
            viewData()
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val username = dbHandler.getUserData()
                val url = URL("https://boardgamegeek.com/xmlapi/collection/$username&stats=1")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/games.xml")
                val data = ByteArray(1024)
                var count = 0
                var total:Long = 0
                var progress = 0
                count = isStream.read(data)
                while (count != -1){
                    total += count.toLong()
                    val progress_temp = total.toInt()*100/lengthOfFile
                    if(progress_temp % 10 == 0 && progress != progress_temp){
                        progress = progress_temp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch (e: MalformedURLException){
                return "Zły URL"
            } catch (e: FileNotFoundException){
                return "Brak pliku"
            } catch (e: IOException){
                return "Wyjątek IO"
            }
            return "Success"
        }
    }

    @Suppress("DEPRECATION")
    private inner class ExpansionsDownloader: AsyncTask<String, Int, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE
            loadExpansions()
            viewData()
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val username = dbHandler.getUserData()
                val url = URL("https://boardgamegeek.com/xmlapi/collection/$username&stats=1&subtype=boardgameexpansion")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/expansions.xml")
                val data = ByteArray(1024)
                var count = 0
                var total:Long = 0
                var progress = 0
                count = isStream.read(data)
                while (count != -1){
                    total += count.toLong()
                    val progress_temp = total.toInt()*100/lengthOfFile
                    if(progress_temp % 10 == 0 && progress != progress_temp){
                        progress = progress_temp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch (e: MalformedURLException){
                return "Zły URL"
            } catch (e: FileNotFoundException){
                return "Brak pliku"
            } catch (e: IOException){
                return "Wyjątek IO"
            }
            return "Success"
        }
    }
}