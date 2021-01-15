package com.psw.calcultrainer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.custom_toast.view.*
import java.util.*

fun isKorea() : Boolean{
    var lang = Locale.getDefault().getLanguage()
    return if(lang == "ko") true else false
}

fun Context.setLang(kStr: String, eStr: String) : String{
    return if( isKorea()) kStr else eStr
}

fun Context.showCustom(s: String, spec: Int = 0) {
    val layoutInflater =
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = layoutInflater.inflate(R.layout.custom_toast, null)

    // layout 안의 txtMessage
    layout.txtMessage.text = s

    when{
        spec == 0 -> {layout.image_status.setImageResource(R.mipmap.ic_correct)}
        spec == 1 -> {layout.image_status.setImageResource(R.mipmap.ic_wrong)}
        spec == 3 -> {layout.image_status.setImageResource(android.R.drawable.btn_radio)}
    }

    val toast = Toast(applicationContext)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout
    toast.show()
}

fun showAskUpdateDialog(act: Activity){
    val builder = AlertDialog.Builder(act)
    builder.setTitle("Do you want to Update?")
    builder.setCancelable(false)

    builder.setPositiveButton("O", { dialog, which ->
        val it = Intent(Intent.ACTION_VIEW)
        it.data = Uri.parse("https://play.google.com/store/apps/details?id=com.psw.calcultrainer")
        act.startActivity(it)
    })
    builder.setNegativeButton("X", { dialog, which -> showSelectDialog(act) })

    val dialog = builder.create()
    dialog.show()
}

fun showSelectDialog(act: Activity){
    val builder = AlertDialog.Builder(act)
    builder.setTitle(act.setLang("레벨선택", "select level"))
    builder.setCancelable(false)

    val animals = arrayOf(
        act.setLang("쉬운", "easy"),
        act.setLang("보통(베다수학)", "normal(Vedic)"),
        act.setLang("숫자맞추기", "how many~"),
        act.setLang("끝내기", "close")
    )
    builder.setItems(animals) { dialog, which ->
        when (which) {
            0 -> {
                val mainIntent = Intent(act, ChildCalculator::class.java)
                act.startActivity(mainIntent)

            }
            1 -> {
                val mainIntent = Intent(act, BasicCalculator::class.java)
                act.startActivity(mainIntent)

            }
            2 -> {
                val mainIntent = Intent(act, ChildCalculator2::class.java)
                act.startActivity(mainIntent)

            }
            3 -> {

            }
        }
        act.finish()
    }

    val dialog = builder.create()
    dialog.show()
}

fun Context.processUpdate(fnOk: (Boolean) -> Unit){
    val appUpdateManager = AppUpdateManagerFactory.create(this)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    appUpdateInfoTask.addOnSuccessListener { apInfo ->
        if (apInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
            fnOk(true)
        } else {
            fnOk(false)
        }
    }

}