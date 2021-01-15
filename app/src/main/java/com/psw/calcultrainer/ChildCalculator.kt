package com.psw.calcultrainer

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlinx.android.synthetic.main.result.*
import org.w3c.dom.Text
import kotlin.random.Random

class ChildCalculator : AppCompatActivity() {
    var currentLine : LinearLayout? = null
    val inflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    var inputDigitString = ""
    var resultNumber  = -1
    var score = mutableListOf(0, 0)

    var num_seed_id = 1970

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_calculator)

        askMath()
    }

    private fun askMath() {
        makeRandomChoice().let {
            when (it) {
                0 -> {
                    makeAddQuestion(makeNumer(), makeNumer())
                }
                1 -> {
                    makeSubQuestion(makeNumer(), makeNumer())
                }

                2 -> {
                    makeTenQuestion(makeNumer())
                }

                3 -> {
                    makeTenQuestion2(makeNumer2())
                }

                else -> {
                    makeAddQuestion(makeNumer(), makeNumer())
                }
            }
        }
    }

    private fun makeTenQuestion2(num1: Int) {
        addLineString("=?")

        val displayChar = "●"//makeChar(num1)

        val nTenCount = num1 / 10
        (1..nTenCount).forEach {
            (1..10).forEach {
                addDigitTextView3(inflater, displayChar, 1)
            }
            addDigitTextView3(inflater, it.toString(), 0)
            lnScrollBoard.addView(currentLine)
            currentLine = null
        }

        val nOneCount = num1 % 10
        (1..10).forEach {
            if(it <= nOneCount) addDigitTextView3(inflater, displayChar, 0)
            else addDigitTextView3(inflater, ".", 0)
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        addSpace ()


        scrollBoard.post{
            scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
            //scrollBoard.smoothScrollBy(0, 5000)
        }

        scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
        resultNumber =  num1
    }

    fun makeRandomChoice() : Int {
        return ( Math.random() * 4 ).toInt() % 4
    }

    private fun makeNumer(): Int {
        return ( Math.random() * 10 ).toInt()
    }

    private fun makeNumer2(): Int {
        return ( Math.random() * 89 ).toInt() + 10
    }

    private fun makeTenQuestion(num1 : Int){

        addLineString("$num1+?=10")

        val displayChar = makeChar(num1)
        (1..10).forEach {
            if(it <= num1) addDigitTextView(inflater, displayChar, 0)
            else addDigitTextView(inflater, ".", 0)
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        addSpace ()

        scrollBoard.post{
            scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
            //scrollBoard.smoothScrollBy(0, 5000)
        }

        scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
        resultNumber =  10 - num1
    }

    private fun makeSubQuestion(n1 : Int , n2 : Int){
        var num1 : Int = Math.max(n1, n2)
        var num2:  Int = Math.min(n1, n2)

        addLineString("$num1-$num2=")

        val displayChar = makeChar(num1)
        (1..10).forEach {
            when {
                it <= num1 -> { addDigitTextView(inflater, displayChar, if( it <= num2) 1 else 0 ) }
                else       -> { addDigitTextView(inflater, ".", 0) }
            }

        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        (1..10).forEach {
            if(it <= num2) addDigitTextView(inflater, displayChar, 0)
            else addDigitTextView(inflater, ".", 0)
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        addSpace ()

        scrollBoard.post{
            scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
            //scrollBoard.smoothScrollBy(0, 1000)
        }

        scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
        resultNumber =  num1 - num2
    }

    private fun makeAddQuestion(num1 : Int, num2: Int){
        addLineString("$num1+$num2=")

        var num1_seed : Int = makeSeedNum()
        var num2_seed = num1_seed + 20

        val displayChar = makeChar(num1)
        (1..10).forEach {
            if(it <= num1) addDigitTextView2(inflater, displayChar, num1_seed + it, {
                textV ->
                if ( changeNumber(num2_seed, displayChar) ){
                    textV.text = "."
                }
            })
            else addDigitTextView2(inflater, ".", num1_seed + it, {
                textV ->
                if ( changeNumber(num2_seed, displayChar) ){
                    textV.text = "."
                }
            })
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        (1..10).forEach {
            if(it <= num2) addDigitTextView2(inflater, displayChar, num2_seed + it, {
                    textV ->
                if ( changeNumber(num1_seed, displayChar) ){
                    textV.text = "."
                }
            })
            else addDigitTextView2(inflater, ".",  num2_seed + it, {
                textV ->
                if ( changeNumber(num1_seed, displayChar) ){
                    textV.text = "."
                }
            })
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null

        addSpace ()

        scrollBoard.post{
            scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
            //scrollBoard.smoothScrollBy(0, 1000)
        }

        scrollBoard.fullScroll(ScrollView.FOCUS_DOWN)
        resultNumber =  num1 + num2
    }

    private fun makeSeedNum(): Int {
        num_seed_id = num_seed_id + 50
        return num_seed_id
    }

    private fun changeNumber(idNum: Int, displayChar: String) : Boolean{

        (1..10).forEach {
            findViewById<TextView>(idNum + it)?.apply {
                if (text == "."){
                    text = displayChar
                    return true
                }
            }
        }
        return false
    }

    private fun addLineString(s : String ) {
        s.forEach {
            val ch = "${it}"
            addDigitTextView(inflater, ch, 0)
        }
        lnScrollBoard.addView(currentLine)
        currentLine = null
    }

    fun onPress(v : View){
        (v as Button).text.let{

            when {
                it.matches(Regex("\\d+(?:\\.\\d+)?")) -> {
                    inputDigitString += it
                    text_operator.text = inputDigitString
                    //addDigitTextView(inflater, it[0], 1)
                }

                it == "="  -> {
                    if (inputDigitString == "") return

                    var answer = ""

                    // 정답체크
                    inputDigitString.toInt().let {
                        if (it == resultNumber){
                            showCustom("\uD83D\uDE0D", 0)
                            answer = "\uD83D\uDE0D"

                            score[0] += 1
                        } else {
                            showCustom("\uD83D\uDE44", 1)
                            answer = "\uD83D\uDE44"

                            score[1] += 1
                        }
                    }

                    addResultText( "${answer} ${resultNumber}" )
                    addSpace()

                    inputDigitString = ""
                    text_operator.text = inputDigitString
                    text_answer.text = "⭕ ${score[0]} ✖️ ${score[1]} "

                    askMath()
                }

                it == "\uD83D\uDE4B"  -> {
                    lnScrollBoard.removeAllViews()
                    inputDigitString = ""
                    text_operator.text = inputDigitString
                    score[0] = 0
                    score[1] = 0
                    text_answer.text = "⭕ ${score[0]} ✖️ ${score[1]} "

                    makeAddQuestion(makeNumer(), makeNumer())
                }

                it == "C" -> {
                    inputDigitString = ""
                    text_operator.text = inputDigitString
                }

                else  -> {}
            }
        }
    }

    private fun addDigitTextView(inflater: LayoutInflater, s: String, n : Int) {
        val digit = inflater.inflate(R.layout.digit_char2, null) as TextView
        digit.text = "${s}"
        if(n !=0 ) {
            digit.setTextColor(Color.parseColor("#FF3322"))
            //digit.setBackgroundColor(Color.parseColor("#55FFFF22"))
            //digit.setBackgroundResource(R.drawable.back_sub)
            digit.alpha = 0.4f

        }else{
            digit.setTextColor(Color.parseColor("#333333"))
        }

        digit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28.0f)
        val width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)
                .toInt()

        digit.layoutParams = ViewGroup.LayoutParams(width, ActionBar.LayoutParams.WRAP_CONTENT)

        if ( currentLine == null ) getNewLine()
        currentLine!!.addView(digit)

    }

    private fun addDigitTextView2(inflater: LayoutInflater, s: String, id : Int, fnOnAction : (TextView) -> Unit) {
        val digit = inflater.inflate(R.layout.digit_char2, null) as TextView
        digit.text = "${s}"
        digit.id = id
        digit.tag = s
        digit.setOnClickListener {
            if(digit.text != ".") fnOnAction(digit)
        }
        digit.setTextColor(Color.parseColor("#333333"))

        digit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28.0f)
        val width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)
                .toInt()

        digit.layoutParams = ViewGroup.LayoutParams(width, ActionBar.LayoutParams.WRAP_CONTENT)

        if ( currentLine == null ) getNewLine()
        currentLine!!.addView(digit)

    }

    private fun addDigitTextView3(inflater: LayoutInflater, s: String, n : Int) {
        val digit = inflater.inflate(R.layout.digit_char2, null) as TextView
        digit.text = "${s}"
        if(n !=0 ) {
            digit.setTextColor(Color.parseColor("#333333"))
            digit.setBackgroundColor(Color.parseColor("#FFFF22"))
            //digit.setBackgroundResource(R.drawable.back_sub)
            digit.alpha = 0.4f

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
        txt.setTextSize (TypedValue.COMPLEX_UNIT_DIP, 28f)
        txt.gravity = Gravity.RIGHT
        lnScrollBoard.addView(txt)
    }

    private fun addSpace (){
        val ln = inflater.inflate(R.layout.space_line, null) as LinearLayout
        val height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, resources.displayMetrics)
                .toInt()

        ln.layoutParams = ViewGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, height)

        lnScrollBoard.addView(ln)
    }

    private fun getNewLine(): LayoutInflater {
        currentLine = inflater.inflate(R.layout.item_digit_line, null) as LinearLayout
        return inflater
    }

    fun makeChar(n : Int) : String {
        val lst = listOf("\uD83C\uDF49", "\uD83C\uDF53", "\uD83C\uDF68", "😼")
        return lst[ n % lst.size ]
    }

    override fun onBackPressed() {
        showSelectDialog(this)
    }
}
