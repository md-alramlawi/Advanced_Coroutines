package com.ramalwi.plants

@JvmInline
value class GrowZone(val number: Int){
    companion object{
        val NoGrowZone = GrowZone(-1)
    }
}
