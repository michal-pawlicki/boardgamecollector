package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.Pair

class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context,
    DATABASE_NAME, factory, DATABASE_VERSION) {
                      companion object {
                          private val DATABASE_VERSION = 1
                          private val DATABASE_NAME = "gameDB.db"
                          val TABLE_GAMES = "games"
                          val TABLE_EXPANSIONS = "expansions"
                          val COLUMN_ID = "_id"
                          val COLUMN_RANK = "rank"
                          val COLUMN_RELEASE = "releaseDate"
                          val COLUMN_NAME = "gameName"
                          val COLUMN_RATING = "rating"
                          val COLUMN_IMAGE = "image"
                      }

    override fun onCreate(db: SQLiteDatabase) {
       val CREATE_GAMES_TABLE = ("CREATE TABLE IF NOT EXISTS " +
               TABLE_GAMES + "("
               + COLUMN_ID + " INTEGER PRIMARY KEY," +
               COLUMN_RANK + " INTEGER," +
               COLUMN_RELEASE + " INTEGER," +
               COLUMN_NAME + " TEXT," +
               COLUMN_RATING + " REAL," +
               COLUMN_IMAGE + " TEXT" +")")
        val CREATE_EXPANSIONS_TABLE = ("CREATE TABLE IF NOT EXISTS " +
                TABLE_EXPANSIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_RANK + " INTEGER," +
                COLUMN_RELEASE + " INTEGER," +
                COLUMN_NAME + " TEXT," +
                COLUMN_RATING + " REAL," +
                COLUMN_IMAGE + " TEXT" +")")
        db.execSQL(CREATE_GAMES_TABLE)
        db.execSQL(CREATE_EXPANSIONS_TABLE)
        db.execSQL("CREATE TABLE IF NOT EXISTS users (user TEXT PRIMARY KEY, date TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS dateranks (id INTEGERT , date TEXT, rank INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPANSIONS")
        onCreate(db)
    }

    fun saveGames(games: ArrayList<Game>){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_GAMES")
        for (i in 0 until games.size){
            val values = ContentValues()
            values.put(COLUMN_ID, games[i].id)
            values.put(COLUMN_NAME, games[i].gameName)
            values.put(COLUMN_RANK, games[i].rank)
            values.put(COLUMN_RATING, games[i].rating)
            values.put(COLUMN_RELEASE, games[i].releaseDate)
            values.put(COLUMN_IMAGE, games[i].image)
            db.insert(TABLE_GAMES, null, values)
        }
        db.close()
    }

    fun saveDateRanks(dateranks: ArrayList<DateRank>){
        val db = this.writableDatabase
        for (i in 0 until dateranks.size){
            val values = ContentValues()
            values.put("id", dateranks[i].id)
            values.put("date", dateranks[i].date)
            values.put("rank", dateranks[i].rank)
            db.insert("dateranks", null, values)
        }
        db.close()
    }

    fun saveExpansions(games: ArrayList<Game>){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_EXPANSIONS")
        for (i in 0 until games.size){
            val values = ContentValues()
            values.put(COLUMN_ID, games[i].id)
            values.put(COLUMN_NAME, games[i].gameName)
            values.put(COLUMN_RANK, games[i].rank)
            values.put(COLUMN_RATING, games[i].rating)
            values.put(COLUMN_RELEASE, games[i].releaseDate)
            values.put(COLUMN_IMAGE, games[i].image)
            db.insert(TABLE_EXPANSIONS, null, values)
        }
        db.close()
    }

    fun getGamesNumber(): Int{
        val countQuery = "SELECT  * FROM $TABLE_GAMES EXCEPT SELECT  * FROM $TABLE_EXPANSIONS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count
    }

    fun getExpansionsNumber(): Int{
        val countQuery = "SELECT  * FROM $TABLE_EXPANSIONS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count
    }

    fun getAllGames(howtosort: Int): ArrayList<Game>{
        val gamesList: ArrayList<Game> = ArrayList()
        var text = ""
        if (howtosort == 0){
            text = "ORDER BY $COLUMN_RANK"
        }
        if (howtosort == 1){
            text = "ORDER BY $COLUMN_RANK DESC"
        }
        if (howtosort == 2){
            text = "ORDER BY $COLUMN_NAME"
        }
        if (howtosort == 3){
            text = "ORDER BY $COLUMN_NAME DESC"
        }
        if (howtosort == 4){
            text = "ORDER BY $COLUMN_RELEASE"
        }
        if (howtosort == 5){
            text = "ORDER BY $COLUMN_RELEASE DESC"
        }
        val query = "SELECT  * FROM $TABLE_GAMES EXCEPT SELECT  * FROM $TABLE_EXPANSIONS $text"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Long
        var rank: Int
        var releaseDate: Int
        var gameName: String
        var rating: Float
        var image: String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getLong(0)
                rank = cursor.getInt(1)
                releaseDate = cursor.getInt(2)
                gameName = cursor.getString(3)
                rating = cursor.getFloat(4)
                image = cursor.getString(5)
                val game = Game(id, rank, releaseDate, gameName, rating, image)
                gamesList.add(game)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gamesList
    }

    fun getAllExpansions(howtosort: Int): ArrayList<Game>{
        val gamesList: ArrayList<Game> = ArrayList()
        var text = ""
        if (howtosort == 2){
            text = "ORDER BY $COLUMN_NAME"
        }
        if (howtosort == 3){
            text = "ORDER BY $COLUMN_NAME DESC"
        }
        if (howtosort == 4){
            text = "ORDER BY $COLUMN_RELEASE"
        }
        if (howtosort == 5){
            text = "ORDER BY $COLUMN_RELEASE DESC"
        }
        val query = "SELECT  * FROM $TABLE_EXPANSIONS $text"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Long
        var rank: Int
        var releaseDate: Int
        var gameName: String
        var rating: Float
        var image: String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getLong(0)
                rank = cursor.getInt(1)
                releaseDate = cursor.getInt(2)
                gameName = cursor.getString(3)
                rating = cursor.getFloat(4)
                image = cursor.getString(5)
                val game = Game(id, rank, releaseDate, gameName, rating, image)
                gamesList.add(game)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gamesList
    }

    fun findGames(game_name: String, howtosort: Int): ArrayList<Game>{
        val gamesList: ArrayList<Game> = ArrayList()
        var text = ""
        if (howtosort == 0){
            text = "ORDER BY $COLUMN_RANK"
        }
        if (howtosort == 1){
            text = "ORDER BY $COLUMN_RANK DESC"
        }
        if (howtosort == 2){
            text = "ORDER BY $COLUMN_NAME"
        }
        if (howtosort == 3){
            text = "ORDER BY $COLUMN_NAME DESC"
        }
        if (howtosort == 4){
            text = "ORDER BY $COLUMN_RELEASE"
        }
        if (howtosort == 5){
            text = "ORDER BY $COLUMN_RELEASE DESC"
        }
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_NAME LIKE \"%$game_name%\" EXCEPT SELECT * FROM $TABLE_EXPANSIONS $text"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Long
        var rank: Int
        var releaseDate: Int
        var gameName: String
        var rating: Float
        var image: String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getLong(0)
                rank = cursor.getInt(1)
                releaseDate = cursor.getInt(2)
                gameName = cursor.getString(3)
                rating = cursor.getFloat(4)
                image = cursor.getString(5)
                val game = Game(id, rank, releaseDate, gameName, rating, image)
                gamesList.add(game)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gamesList

    }

    fun findGameById(id: Long): Game?{
        var game: Game? = null

        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID = $id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Long
        var rank: Int
        var releaseDate: Int
        var gameName: String
        var rating: Float
        var image: String

        if (cursor.moveToFirst()){
                id = cursor.getLong(0)
                rank = cursor.getInt(1)
                releaseDate = cursor.getInt(2)
                gameName = cursor.getString(3)
                rating = cursor.getFloat(4)
                image = cursor.getString(5)
                game = Game(id, rank, releaseDate, gameName, rating, image)
                cursor.close()
        }
        db.close()
        return game
    }

    fun findDateRanksById(id: Long): ArrayList<DateRank>{
        val daterankList: ArrayList<DateRank> = ArrayList()
        val query = "SELECT * FROM dateranks WHERE id = $id"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Long
        var rank: Int
        var date: String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getLong(0)
                date = cursor.getString(1)
                rank = cursor.getInt(2)

                val daterank = DateRank(id, date, rank)
                daterankList.add(daterank)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return daterankList

    }

    fun findExpansions(game_name: String, howtosort: Int): ArrayList<Game>{
        val gamesList: ArrayList<Game> = ArrayList()
        var text = ""
        if (howtosort == 2){
            text = "ORDER BY $COLUMN_NAME"
        }
        if (howtosort == 3){
            text = "ORDER BY $COLUMN_NAME DESC"
        }
        if (howtosort == 4){
            text = "ORDER BY $COLUMN_RELEASE"
        }
        if (howtosort == 5){
            text = "ORDER BY $COLUMN_RELEASE DESC"
        }
        val query = "SELECT * FROM $TABLE_EXPANSIONS WHERE $COLUMN_NAME LIKE \"%$game_name%\" $text"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Long
        var rank: Int
        var releaseDate: Int
        var gameName: String
        var rating: Float
        var image: String

        if (cursor.moveToFirst()){
            do {
                id = cursor.getLong(0)
                rank = cursor.getInt(1)
                releaseDate = cursor.getInt(2)
                gameName = cursor.getString(3)
                rating = cursor.getFloat(4)
                image = cursor.getString(5)
                val game = Game(id, rank, releaseDate, gameName, rating, image)
                gamesList.add(game)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gamesList
    }

    fun saveUser(username: String){
        val user = ContentValues()
        val db = this.writableDatabase
        user.put("user", username)
        db.insert("users", null, user)
        db.close()
    }

    fun getUserData():String? {
        val db = this.writableDatabase
        val query = "SELECT * FROM users"
        val cursor = db.rawQuery(query,null)
        var username: String? = null

        if(cursor.moveToFirst()) {
            username = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return username
    }

    fun getDate():String? {
        val db = this.writableDatabase
        val query = "SELECT * FROM users"
        val cursor = db.rawQuery(query,null)
        var date: String? = null

        if(cursor.moveToFirst()) {
            date = cursor.getString(1)
            cursor.close()
        }
        db.close()
        return date
    }

    fun saveDate(date: String){
        val db = this.writableDatabase
        db.execSQL("UPDATE users SET date = \"$date\"")
        db.close()
    }

    fun deleteUserData(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM users")
        db.execSQL("DELETE FROM $TABLE_EXPANSIONS")
        db.execSQL("DELETE FROM $TABLE_GAMES")
        db.execSQL("DELETE FROM dateranks")
        db.execSQL("VACUUM")
    }
}