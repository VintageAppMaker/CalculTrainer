package com.psw.calcultrainer

import java.lang.Math.max
import java.lang.Math.random

//fun main() {
//    VedicAdd().process()
//    VedicSub().process()
//}

val VERTICAL_SPACE  = "########"
val VERTICAL_RESULT = "___________"

class VedicAdd( p : (Any) -> Unit, p2 : (Any, Int) -> Unit ) : RunCalculator(p, p2){
    open var op1 : Int = 0
    open var op2 : Int = 0
    open var sum : Int = 0
    val sArrayOp1 by lazy { op1.toString().let { s ->
        s.map {
                item -> "${item}".toInt()
        }.toMutableList()
    }}
    val sArrayOp2 by lazy{
        op2.toString().let { s ->
            s.map { item ->
                "${item}".toInt()
            }
        }.toMutableList()
    }
    val sSumArray by lazy{
        sum.toString().let { s ->
            s.map { item ->
                "${item}".toInt()
            }
        }.toMutableList()
    }

    var sumHistory : String = ""

    fun process (){
        sum = op1 + op2
        makeDigitItem()
        (0..sArrayOp1.size - 1).forEachIndexed { index, i ->
            solveLineAdd(index)
            println(VERTICAL_SPACE)
        }
    }

    private fun solveLineAdd(nSelected : Int) {
        print(" ", 0)
        sArrayOp1.forEachIndexed { index, i ->
            if (nSelected == index)
                print("$i", 1)
            else
                print(i, 0)
        }
        println ("")
        print("+", 0)
        sArrayOp2.forEachIndexed { index, i ->
            if (nSelected == index)
                print("$i", 1)
            else
                print(i, 0)
        }
        println ("")
        println (VERTICAL_RESULT)

        sumHistory += makeSpaceAndSum(nSelected,sArrayOp1[nSelected], sArrayOp2[nSelected ] ) + "\n"
        println (sumHistory)
    }

    private fun makeSpaceAndSum(nSelected: Int, num1 : Int, num2: Int) : String{
        // 계산값 넣기
        var space = ""
        val sub = if( num1 + num2  > 9) 1 else 0
        for(i  in (0..nSelected - sub) ){
            space += " "
        }
        return "${space}${num1 + num2}"
    }

    private fun makeDigitItem() {
        val nCount = max(sArrayOp1.size, sArrayOp2.size)
        if (sArrayOp1.size < nCount){
            (nCount - sArrayOp1.size).let {
                (0..it -1 ).forEach { sArrayOp1.add(0,0) }
            }
        }

        if (sArrayOp2.size < nCount){
            (nCount - sArrayOp2.size).let {
                (0..it -1 ).forEach { sArrayOp2.add(0,0) }
            }
        }
    }

}

class VedicSub( p : (Any) -> Unit, p2 : (Any, Int) -> Unit ) : RunCalculator(p, p2){
    open var op1 : Int = 0
    open var op2 : Int = 0
    open var sub : Int = 0
    val sArrayOp1 by lazy { op1.toString().let { s ->
        s.map {
                item -> "${item}".toInt()
        }.toMutableList()
    }}
    val sArrayOp2 by lazy{
        op2.toString().let { s ->
            s.map { item ->
                "${item}".toInt()
            }
        }.toMutableList()
    }
    val sSumArray by lazy{
        sub.toString().let { s ->
            s.map { item ->
                "${item}".toInt()
            }
        }.toMutableList()
    }

    var subHistory : String = ""

    fun process (){
        initOperand()
        makeDigitItem()
        (0..sArrayOp1.size - 1).forEachIndexed { index, i ->
            solveLineSub(index)
        }
    }

    private fun initOperand() {
        if(op1 < op2){
            val temp = op1
            op1 = op2
            op2 = temp
        }

        sub = op1 -op2
    }

    private fun solveLineSub(nSelected : Int) {
        print(" ", 0)
        sArrayOp1.forEachIndexed { index, i ->
            if (nSelected == index)
                print("$i", 1)
            else
                print(i, 0)
        }
        println ("")
        print("-", 0)
        sArrayOp2.forEachIndexed { index, i ->
            if (nSelected == index)
                print("$i", 1)
            else
                print(i, 0)
        }
        println ("")
        println (VERTICAL_RESULT)

        subHistory += makeSpaceAndSub(nSelected,sArrayOp1[nSelected], sArrayOp2[nSelected ] ) + "\n"
        println (subHistory)
        println(VERTICAL_SPACE)
    }

    private fun makeSpaceAndSub(nSelected: Int, num1 : Int, num2: Int) : String{
        // 계산값 넣기
        var space = " "
        val cal  = if ( num1 - num2 > -1  ) num1 - num2 else ( num1 + (10 - num2) ) + 10
        val sub  = if ( num1 - num2 > -1  ) 0 else 1

        for(i  in (0..nSelected - 1 - sub ) ){
            space += " "
        }

        return "${space}${cal}"
    }

    private fun makeDigitItem() {
        val nCount = max(sArrayOp1.size, sArrayOp2.size)
        if (sArrayOp1.size < nCount){
            (nCount - sArrayOp1.size).let {
                (0..it -1 ).forEach { sArrayOp1.add(0,0) }
            }
        }

        if (sArrayOp2.size < nCount){
            (nCount - sArrayOp2.size).let {
                (0..it -1 ).forEach { sArrayOp2.add(0,0) }
            }
        }
    }

}