package mr.kostua.learningpro.tools

import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.plattysoft.leonids.ParticleSystem
import mr.kostua.learningpro.R

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
fun TextView.setUnderlineText(text: String) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = spannableString
}

fun EditText.showSoftInputOnFocusAllAPI(isShown: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        showSoftInputOnFocus = isShown
    } else {
        try {
            val method = EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(this, isShown)
        } catch (e: Exception) {
            // ignore
        }
    }
}

fun AlertDialog.setSlideWindowAnimation() {
    this.window.attributes.windowAnimations = R.style.CustomAlertDialogAnimation
}

fun Activity.showFireWorkAnimation(viewEmitter: View, timeToLive: Long, numParticles: Int) {
    ParticleSystem(this, numParticles, R.drawable.yellow_star_icon, timeToLive)
            .setSpeedRange(0.2f, 0.5f)
            .oneShot(viewEmitter, numParticles)
}

