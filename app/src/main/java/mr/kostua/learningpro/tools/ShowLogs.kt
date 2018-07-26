package mr.kostua.learningpro.tools

import android.util.Log

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
object ShowLogs {
    private var isDebugging = true

    fun log(TAG: String, logMessage: String) {
        if (isDebugging)
            Log.i("KOKO $TAG", " $logMessage")
    }

}