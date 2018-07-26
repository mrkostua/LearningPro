package mr.kostua.learningpro.tools

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
fun TextView.setUnderlineText(text: String) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = spannableString

}
