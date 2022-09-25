package com.ramalwi.plant.models

@JvmInline
value class GrowZone(val number: Int){
    companion object{
        val NoGrowZone = GrowZone(-1)
    }
}
