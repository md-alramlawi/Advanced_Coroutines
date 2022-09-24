package com.ramalwi.flowapp.demo

/**
 *@ param STR1 parameter 1
 *@ param STR2 parameter 2
 */
fun getResult(str1: String, int1: Int): String = "result is {$str1 , $int1}"

/**
 *@ param P1 parameter 1
 *@ param P2 parameter2
 *@ param method method method name
 */
fun lock(p1: String, p2: Int, method: (str1: String, int1: Int) -> String): String {
    return method(p1, p2)
}


fun text1(){
    val lock1 = lock(
        p1 = "Hello",
        p2 = 5,
        method = {str1, int1 ->
            getResult(str1, int1)
        }
    )

    val lock2 = lock(
        p1 = "Hello",
        p2 = 5,
        method = ::getResult
    )

    println(::getResult)
    println(lock1)
    println(lock2)
}


class ParentClass(
    val family: String
){

    fun changeName(onNameChanged: (String) -> Unit){
        onNameChanged("Hello Mohammed $family")
    }

    fun printName(name: String){
        println(name)
    }
}

fun test2(){
    val parentClass = ParentClass("Ramlawi")

    parentClass.changeName(
        onNameChanged = parentClass::printName
    )
}

fun main() {

    text1()
}