package com.example.boardgamecollector

class DateRank {
    var id: Long = 0
    var date: String = ""
    var rank: Int = 0


    constructor(id: Long, date: String, rank: Int) {
        this.id = id
        this.date = date
        this.rank = rank
    }
}