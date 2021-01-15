package com.psw.calcultrainer

open class RunCalculator(pFunc : (Any) -> Unit, pFunc2 : (Any, Int) -> Unit){

    companion object {
        var println : (Any) -> Unit = {}
        var print   : (Any, Int) -> Unit = {o, n -> }
    }

    init {
        println = pFunc
        print   = pFunc2
    }

}