package com.example.tour_a_versity_v3.dto

data class Buildings (var buildingID:Int = 0, var buildingName:String = "", var info:String = "", var latitude:String = "", var longitude:String = ""){
    override fun toString(): String {
        return "$buildingID $buildingName $info $latitude $longitude"
    }
}