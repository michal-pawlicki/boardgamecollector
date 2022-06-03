package com.example.boardgamecollector

import java.util.*

class Game {
    var id: Long = 0
    var rank: Int = 0
    var releaseDate: Int = 0
    var gameName: String? = null
    var rating: Float = 0F
    var image: String? = null


    constructor(id: Long, rank: Int, releaseDate: Int, gameName: String, rating: Float, image: String) {
        this.id = id
        this.rank = rank
        this.releaseDate = releaseDate
        this.gameName = gameName
        this.rating = rating
        this.image = image
    }
}