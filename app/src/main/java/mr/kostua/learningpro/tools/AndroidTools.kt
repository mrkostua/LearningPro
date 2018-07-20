package mr.kostua.learningpro.tools

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
fun Resources.getSimpleDrawable(@DrawableRes id: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getDrawable(id, null)
    } else {
        getDrawable(id)
    }
}

fun TextView.setUnderlineText(text: String) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = spannableString

}
