package com.psw.calcultrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        processUpdate{
                bUpdate ->
            if(bUpdate){
                showAskUpdateDialog(this)
            } else{
                setUpUIComplte()
            }
        }

    }

    private fun setUpUIComplte() {

        val SPLASH_DISPLAY_LENGTH = 500

        /* SPLASH_DISPLAY_LENGTH 뒤에 메뉴 액티비티를 실행시키고 종료한다.*/
        Handler().postDelayed({
            showSelectDialog(this)
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }


    fun onPress(v : View){
        (v as Button).text.let{
        }
    }

}
