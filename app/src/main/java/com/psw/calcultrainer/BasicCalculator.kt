package com.psw.calcultrainer

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import kotlinx.android.synthetic.main.result.*
import java.lang.Math.random


class BasicCalculator : AppCompatActivity() {

    var currentLine : LinearLayout? = null
    val inflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    var touchedbuttons = ""
    var firstValue  : Int = 0
    var secondValue : Int = 0
    var operation   : String = ""
    var resultValue : Int = -1

    val MESSAGE_RESULT by lazy {
        if(isKorea()) "\uD83D\uDE4B 계산값을 입력 후, =를 누르세요"
        else "\uD83D\uDE4B Input result number and ="
    }
    val MESSAGE_READ_ALL by lazy {
        if(isKorea()) "\uD83D\uDE0A 끝까지 스크롤하면 hint가 보입니다."
        else "\uD83D\uDE0A \"scroll to the end\" is hint"
    }

    var mathAdd : VedicAdd? = null
    var mathSub : VedicSub? = null

    var LIMIT_COUNT = ( 60 * 1.5 ).toLong()
    var tmr : CountDownTimer

    init {
        tmr = setCountDownTimer(LIMIT_COUNT)
    }

    private fun setCountDownTimer(tick: Long): CountDownTimer {
        return object:CountDownTimer(tick * 1000, 1000) {
            override fun onFinish() {
                text_counter.text = "0"
            }

            override fun onTick(millisUntilFinished: Long) {
                text_counter.text = "${millisUntilFinished / 1000}"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basiccalculator)
        scrollBoard.getViewTreeObserver()
            .addOnScrollChangedListener{
                if (scrollBoard.getChildAt(0).getBottom() <= scrollBoard.getHeight() + scrollBoard.getScrollY()) {
                    if(resultValue != -1 ) askResult()
                }
            }

        val intent = intent
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            if (uri != null) {
                val op1  = uri.getQueryParameter("op1")
                val op2  = uri.getQueryParameter("op2")

                if(op1 == null || op2 == null) return
                if(op1!!.isDigitsOnly() && op2!!.isDigitsOnly()){
                    firstValue  = op1.toInt()
                    secondValue = op2.toInt()
                    addProcess()
                }

            }
        }

    }

    private fun askResult() {
        text_answer.text = MESSAGE_RESULT
    }

    private fun addProcess() {
        mathAdd = VedicAdd(
            { obj -> displayLines(obj.toString(), "+") },
            { obj, n ->
                obj.toString().forEach { c ->
                    addDigitTextView(inflater, c, n)
                }
            }
        )

        mathAdd!!.op1 = firstValue
        mathAdd!!.op2 = secondValue

        mathAdd!!.process()

        resultValue = mathAdd!!.sum

        text_operator.text = "${mathAdd!!.op1} + ${mathAdd!!.op2}"
        text_answer.text = MESSAGE_READ_ALL

        scrollBoard.smoothScrollTo(0, 0)
        tmr.start()

        if (firstValue < 10 && secondValue < 10 && resultValue > 10 ){
            kindlyDescriptForChild(mathAdd!!)
        }

        askGoogle("${mathAdd!!.op1} add ${mathAdd!!.op2}")

    }

    private fun kindlyDescriptForChild(v: VedicAdd) {

        val p  = v.op1
        val p2 = v.op2

        val txt = TextView(this)
        var s = "10을 만들기위해 큰 수(${Math.max(p, p2)})에서<br>필요한 값(10 - ${Math.max(p, p2)})을" +
                "<br><font color = '#FFFFFF'>작은 수(${Math.min(p, p2)})에서 빼기<br>=${v.sum}</font>"

        if (!isKorea()) s = "to make 10<br>Big number(${Math.max(p, p2)})need<br>(10 - ${Math.max(
            p,
            p2
        )})" +
                "<br><font color = '#FFFFFF'>Subtract from a small number(${Math.min(p, p2)})<br>=${v.sum}</font>"

        txt.text = Html.fromHtml(s)

        txt.gravity = Gravity.RIGHT
        lnScrollBoard.addView(txt)

    }

    private fun kindlyDescriptTrick(v: VedicSub){

        val p  = v.op1
        val p2 = v.op2

        val fnMid = {  val n = ( p + p2 ) / 2; if (n > 10 ) ( n / 10 ) * 10 else n }
        val txt = TextView(this)
        val title = if (isKorea()) "\uD83D\uDE00【으악! 이런 방법도 있어요】" else "\uD83D\uDE00【This is also possible】"
        var s = "${title}<br> ${Math.min(p, p2)}＿＿＿${fnMid()}＿＿＿${Math.max(p, p2)}<br> ${Math.max(
            p,
            p2
        )} - ${Math.min(p, p2)}<br> =" +
                "(${fnMid()} - ${Math.min(p, p2)}) + (${Math.max(p, p2)} - ${fnMid()})"

        txt.text = Html.fromHtml(s)
        txt.setBackgroundColor(Color.parseColor("#FFF0A0"))
        txt.setTextColor(Color.parseColor("#555555"))
        txt.setPadding(
            dipToPixels(10f).toInt(),
            dipToPixels(10f).toInt(),
            dipToPixels(10f).toInt(),
            dipToPixels(10f).toInt()
        )

        lnScrollBoard.addView(txt)
    }

    private fun askGoogle(s: String) {
        val google  = "https://www.google.com/search?q=${s}"

        val b = Button(this).apply {
            isAllCaps = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            var message = if ( isKorea() ) "검색" else "Search"
            text = "\uD83D\uDCD7 ${message}(${s}) "
            //alpha =0.7f
            setTextColor(Color.parseColor("#333000"))
            setBackgroundColor(Color.parseColor("#00000000"))
            //setBackgroundResource(R.drawable.back)
            setOnClickListener {
                val uris = Uri.parse(google)
                val intents = Intent(Intent.ACTION_VIEW, uris)
                startActivity(intents)
            }
        }

        lnScrollBoard.addView(b)
    }


    private fun subProcess() {
        mathSub = VedicSub(
            { obj -> displayLines(obj.toString(), "-") },
            { obj, n ->
                obj.toString().forEach { c ->
                    addDigitTextView(inflater, c, n)
                }
            }
        )

        if(firstValue < secondValue){
            mathSub!!.op1 = firstValue
            mathSub!!.op2 = secondValue

        } else {
            mathSub!!.op1 = secondValue
            mathSub!!.op2 = firstValue
        }

        mathSub!!.process()

        resultValue = mathSub!!.sub

        text_operator.text = "${mathSub!!.op1} - ${mathSub!!.op2}"
        text_answer.text = MESSAGE_READ_ALL

        scrollBoard.smoothScrollTo(0, 0)
        tmr.start()

        if(mathSub!!.op1 < 100 && mathSub!!.op2 < 100){
            kindlyDescriptTrick(mathSub!!)
        }

        askGoogle("${mathSub!!.op1} minus ${mathSub!!.op2}")
    }

    fun onPress(v: View){
        (v as Button).text.let{

            when{
                it.toString() == "#" -> {
                    askCount()
                    return
                }

                it.toString().toLowerCase() == "rnd" ->{
                    randomQuiz()
                    return
                }

                resultValue > -1 ->{
                    if(inputResult(it)) return
                }

                resultValue == -1 && operationMath(it) ->{
                    return
                }
            }

            displayButtons()

        }

    }

    private fun askCount(){
        val array = arrayOf("90", "150", "200")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (isKorea()) "카운터" else "counter")

        builder.setItems(array, { _, which ->
            tmr.cancel()
            LIMIT_COUNT = array[which].toLong()
            tmr = setCountDownTimer(LIMIT_COUNT)
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun randomQuiz() {
        initOperation()
        val bAdd = if ( ( random() * 100 ).toInt() % 2 == 0 ) true else false
        if(bAdd){
            firstValue  = ( random() * 10000 ) .toInt()
            secondValue = ( random() * 10000 ) .toInt()
            addProcess()
        } else {
            firstValue  = ( random() * 10000 ) .toInt()
            secondValue = ( random() * 10000 ) .toInt()
            subProcess()
        }

    }

    private fun operationMath(it: CharSequence): Boolean {
        when {

            it.matches(Regex("\\d+(?:\\.\\d+)?")) -> {
                if(touchedbuttons.length > MAX_DIGIT_SIZE){
                    showCustom(if (isKorea()) "너무 큰 숫자입니다" else "too big", 3);
                    touchedbuttons = ""
                } else {
                    touchedbuttons += it
                }
            }

            it == "+" || it == "-" -> {
                if (touchedbuttons.length < 1) return true
                operation = it.toString()
                firstValue = touchedbuttons.toInt()
                touchedbuttons = ""
            }

            it == "C" -> {
                initOperation()
            }

            it == "=" -> {
                if (touchedbuttons.length < 1) return true
                secondValue = touchedbuttons.toInt()
                touchedbuttons = ""
                calculate()
            }

        }
        return false
    }

    private fun inputResult(it: CharSequence): Boolean {
        when {

            it.matches(Regex("\\d+(?:\\.\\d+)?")) -> {
                if(touchedbuttons.length > MAX_DIGIT_SIZE + 1){
                    showCustom("too big", 3);
                    touchedbuttons = ""
                } else {
                    touchedbuttons += it
                }
            }

            it == "=" -> {
                if (touchedbuttons.length < 1) return true
                if(resultValue == touchedbuttons.toInt()){
                    showCustom(if (isKorea()) "맞춤\n${getPassedTime()}초 " else "correct\n${getPassedTime()} sec")
                    initOperation()
                    return true

                } else {
                    showCustom(if (isKorea()) "틀림" else "wrong", 1)
                    touchedbuttons = ""
                }
            }

            it == "C" -> {
                initOperation()
            }

        }
        return false
    }

    private fun getPassedTime() = LIMIT_COUNT - text_counter.text.toString().toInt()

    private fun displayButtons() {
        text_operator.text = touchedbuttons
    }

    private fun calculate() {
        clearBoard()
        when (operation){
            "+" -> {
                addProcess()
            }
            "-" -> {
                subProcess()
            }
        }
        operation =""
    }


    private fun displayLines(s: String, op: String) {
        when{
            s == VERTICAL_SPACE -> {
                addSpace()
                return
            }

            s == VERTICAL_RESULT -> {
                addResultLine()
                return
            }

            currentLine != null -> {
                lnScrollBoard.addView(currentLine)
            }
        }

        var inflater = getNewLine()

        val nCount = s.split("\n").count()
        var nIndex = 0

        s.forEach {
            if(it != '\n'){
                addDigitTextView(inflater, it, if (nCount - 2 == nIndex) 1 else 0)
            }
            else{
                nIndex++
                lnScrollBoard.addView(currentLine)
                inflater = getNewLine()
            }
        }

        lnScrollBoard.addView(currentLine)
        currentLine = null

        // 1줄이상이면 숫자계산을 한 것임
        if(nCount > 1){
            addComment(op, nCount)
        }

    }

    private fun addComment(op: String, nCount: Int) {
        val txt = TextView(this)

        var p = if (op == "+") {
            mathAdd!!.sArrayOp1[nCount - 2]
        } else mathSub!!.sArrayOp1[nCount - 2]

        var p2 = if (op == "+") {
            mathAdd!!.sArrayOp2[nCount - 2]
        } else mathSub!!.sArrayOp2[nCount - 2]

        if (op == "-") {
            when {
                p < p2 -> {
                    val s = if(isKorea()) "\uD83D\uDC49 1을 가져오고 ${p + 10 }<br>=10+${p}-${p2}<br>=10-${p2} + ${p}<br><font color = '#FFFFFF'>=${p} + (10-${p2})</font>"
                    else "\uD83D\uDC49 1 carry then ${p + 10 }<br>=10+${p}-${p2}<br>=10-${p2} + ${p}<br><font color = '#FFFFFF'>=${p} + (10-${p2})</font>"
                    txt.text = Html.fromHtml(s)

                }
                else -> {
                    txt.text = "${p} ${op} ${p2}"
                }
            }

        } else {
            when {
                p + p2 > 9  -> {
                    txt.text = if(isKorea() ) "\uD83D\uDC49 1을 넘기고 ${Math.min(p, p2)} - (10 - ${Math.max(
                        p,
                        p2
                    )})"
                    else "\uD83D\uDC49 1 carry and ${Math.min(p, p2)} - (10 - ${Math.max(p, p2)})"
                }
                else -> {
                    txt.text = "${p} ${op} ${p2}"
                }
            }
        }

        txt.gravity = Gravity.RIGHT
        lnScrollBoard.addView(txt)
    }

    private fun addDigitTextView(inflater: LayoutInflater, s: Char, n: Int) {
        val digit = inflater.inflate(R.layout.digit_char, null) as TextView
        digit.text = "${s}"
        if(n !=0 && digit.text.toString().isDigitsOnly() == true ) {
            digit.setTextColor(Color.parseColor("#FF3322"))
            digit.setBackgroundColor(Color.parseColor("#55FFFF22"))

        }else{
            digit.setTextColor(Color.parseColor("#333333"))
        }

        digit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25.0f)
        val width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, resources.displayMetrics)
                .toInt()

        digit.layoutParams = ViewGroup.LayoutParams(width, ActionBar.LayoutParams.WRAP_CONTENT)

        if ( currentLine == null ) getNewLine()
        currentLine!!.addView(digit)

    }

    private fun getNewLine(): LayoutInflater {
        currentLine = inflater.inflate(R.layout.item_digit_line, null) as LinearLayout
        return inflater
    }

    private fun addSpace (){
        val ln = inflater.inflate(R.layout.space_line, null) as LinearLayout
        val height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, resources.displayMetrics)
                .toInt()

        ln.layoutParams = ViewGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, height)

        lnScrollBoard.addView(ln)
    }

    private fun addResultLine (){
        val ln = inflater.inflate(R.layout.space_line, null) as LinearLayout
        val height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
                .toInt()

        ln.layoutParams = ViewGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, height)
        ln.setBackgroundColor(Color.parseColor("#222222"))

        lnScrollBoard.addView(ln)
    }

    private fun clearBoard(){
        lnScrollBoard.removeAllViews()
    }

    private fun initOperation(){
        clearBoard()
        text_operator.text  = ""
        text_answer.text    = ""
        text_counter.text   = ""

        resultValue = -1
        firstValue  = 0
        secondValue = 0
        operation   = ""
        touchedbuttons = ""

        tmr.cancel()
    }

    override fun onBackPressed() {
        showSelectDialog(this)
    }

    fun Context.dipToPixels(dipValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)

    companion object{
        val MAX_DIGIT_SIZE = 5
    }

}
