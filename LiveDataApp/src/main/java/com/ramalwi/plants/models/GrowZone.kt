package com.ramalwi.plants.models

@JvmInline
value class GrowZone(val number: Int){
    companion object{
        val NoGrowZone = GrowZone(-1)
    }
}
