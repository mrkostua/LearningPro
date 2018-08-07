package mr.kostua.learningpro.tools

import android.os.Build
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.TextView

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
