package mr.kostua.learningpro.tools

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
object FileTools {
    private val TAG = this.javaClass.simpleName
    fun getFileNameFromUri(uri: Uri, defaultName: String, contentResolver: ContentResolver): String {
        var result = defaultName
        with(contentResolver.query(uri, null, null, null, null)) {
            if (this != null) {
                val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
                moveToFirst()
                result = getString(nameIndex)
            }
        }
        return result
    }
}