package com.psw.calcultrainer

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.result2.*

class ChildCalculator2 : AppCompatActivity() {
    var currentLine : LinearLayout? = null
    var currentPage : LinearLayout? = null
    val inflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    var inputDigitString = ""
    var resultNumber  = -1
    var score = mutableListOf(0, 0)

    var guess_number = 0
    var min_number = 1
    var max_number = 100

    var isCorrect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_calculator2)

        clearBoard()
    }

    private fun askMath() {
        findGuessNumber()
    }

    private fun makeNumber(): Int {
        return ( Math.random() * 99 ).toInt() + 1
    }

    private fun findGuessNumber(){
        getNewPage()

        displayNumberPos()

        //addLineString(setLang( "얼마일까요?" ,"how many?"), 0)
        addLineString("${min_number}~${max_number}", 0)

        addPage()

        scrollBoard.post{
            //scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
            scrollBoard.smoothScrollBy(1500, 0)
        }

        addSpacePageWidth()
    }

    private fun addLineString(s : String, type : Int ) {
        s.forEach {
            val ch = "${it}"
            addDigitTextView(inflater, ch, type)
        }

        currentPage!!.addView(currentLine)
        currentLine = null
    }

    private fun addPage(){
        if(currentPage != null) lnScrollBoard.addView(currentPage)
        getNewPage()
    }

    fun onPress(v : View){

        if (isCorrect){
            clearBoard()
            return
        }

        (v as Button).text.let{

            when {
                it.matches(Regex("\\d+(?:\\.\\d+)?")) -> {
                    if( inputDigitString.length < 3 ){
                        inputDigitString += it
                        text_operator.text = inputDigitString
                    }
                    //addDigitTextView(inflater, it[0], 1)
                }

                it == "="  -> {
                    if (inputDigitString == "") return

                    var answer = ""
                    var merong_char = "\uD83D\uDE1C"

                    // 정답체크
                    inputDigitString.toInt().let {
                        guess_number = it


                        if (it == resultNumber){
                            showCustom("\uD83D\uDE0D", 0)
                            answer = "\uD83D\uDE0D️"

                            score[0] += 1
                            merong_char = "\uD83E\uDD2A${resultNumber} "

                            isCorrect =true

                        } else {

//                            if (it < resultNumber){
//                                min_number = if ( max(min_number, it) == min_number ) min_number else it +1
//                            } else {
//                                max_number = if ( min(max_number, it) == max_number ) max_number else it - 1
//                            }
                            if (it < resultNumber){
                                min_number = if (  min_number > it ) min_number else it + 1
                            } else {
                                max_number = if (  max_number < it ) max_number else it - 1
                            }

                            //showCustom("\uD83D\uDE44", 1)
                            answer = "\uD83D\uDE44?"

                            score[1] += 1

                        }
                    }

                    //addResultText( "    ${guess_number} ${merong_char} ${answer}     " )
                    //addSpace()

                    inputDigitString = ""
                    text_operator.text = inputDigitString
                    //text_answer.text = "⭕ ${score[0]} ✖️ ${score[1]} "
                    text_answer.text = run {
                        if (score[0] != 1 ) "✖️ "+ score[1].toString()
                        else "ಠ_ಠ 👍 ${score[1] + 1 }"
                    }

                    addPage()

                    if(!isCorrect) askMath()
                }

                it == "\uD83D\uDE4B"  -> {
                    clearBoard()
                }

                it == "C" -> {
                    inputDigitString = ""
                    text_operator.text = inputDigitString
                }

                else  -> {}
            }
        }
    }

    private fun clearBoard() {
        isCorrect = false

        lnScrollBoard.removeAllViews()
        inputDigitString = ""
        text_operator.text = inputDigitString
        score[0] = 0
        score[1] = 0
        //text_answer.text = "⭕ ${score[0]} ✖️ ${score[1]} "
        text_answer.text = "  "

        guess_number = 0
        min_number = 1
        max_number = 100

        resultNumber = makeNumber()

        askMath()
    }

    private fun addDigitTextView(inflater: LayoutInflater, s: String, n : Int) {
        val digit = inflater.inflate(R.layout.digit_char2, null) as TextView
        digit.text = "${s}"
        if(n !=0 ) {
            digit.setTextColor(Color.parseColor("#000000"))
            digit.setBackgroundColor(Color.parseColor("#55FFFF22"))
            //digit.setBackgroundResource(R.drawable.back_sub)
            //digit.alpha = 0.4f

        }else{
            digit.setTextColor(Color.parseColor("#333333"))
        }

        val textsize = if (n == 0) 23.0f else 18.0f
        digit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textsize)

        val textWidth = if (n == 0) 25.0f else 20.0f
        val width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)
                .toInt()

        digit.layoutParams = ViewGroup.LayoutParams(width, ActionBar.LayoutParams.WRAP_CONTENT)

        if ( currentLine == null ) getNewLine()
        currentLine!!.addView(digit)

    }

    private fun displayNumberPos() {

        val displayChar = "●"//makeChar(num1)

        var spec = 0
        (1..10).forEach { i ->
            addDigitTextView3(inflater, (i-1).toString(), spec)
            (1..10).forEach { n ->
                spec = 0
                var index = n + (i - 1 ) * 10
                if( index  >= min_number   &&  index <= max_number  ) spec = 1
                addDigitTextView3(inflater, displayChar, spec)
            }
            currentPage!!.addView(currentLine)
            currentLine = null
        }
        //addSpace ()
    }

    private fun addDigitTextView3(inflater: LayoutInflater, s: String, n : Int) {
        val digit = inflater.inflate(R.layout.digit_char2, null) as TextView
        digit.text = "${s}"
        if(n !=0 ) {
            digit.setTextColor(Color.parseColor("#FF33FF"))
            digit.setBackgroundColor(Color.parseColor("#FFFF22"))
            //digit.setBackgroundResource(R.drawable.back_sub)
            digit.alpha = 0.3f

        }else{
            digit.setTextColor(Color.parseColor("#333333"))
        }

        digit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f)
        val width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
                .toInt()

        digit.layoutParams = ViewGroup.LayoutParams(width, ActionBar.LayoutParams.WRAP_CONTENT)

        if ( currentLine == null ) getNewLine()
        currentLine!!.addView(digit)

    }


    private fun addResultText(s: String) {
        val txt = TextView(this)
        txt.text = Html.fromHtml(s)
        txt.setTextSize (TypedValue.COMPLEX_UNIT_DIP, 18f)
        txt.gravity = Gravity.RIGHT
        currentPage!!.addView(txt)
    }

    private fun addSpacePageWidth(){
        getNewPage()
        val ln = inflater.inflate(R.layout.space_line, null) as LinearLayout
        val width =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
                        .toInt()

        ln.layoutParams = ViewGroup.LayoutParams( width, ActionBar.LayoutParams.MATCH_PARENT)

        currentPage!!.addView(ln)
        addPage()
    }

    private fun getNewLine(): LayoutInflater {
        currentLine = inflater.inflate(R.layout.item_digit_line, null) as LinearLayout
        return inflater
    }

    private fun getNewPage() : LayoutInflater{
        currentPage = inflater.inflate(R.layout.item_digit_page, null) as LinearLayout
        return inflater
    }

    override fun onBackPressed() {
        showSelectDialog(this)
    }
}
