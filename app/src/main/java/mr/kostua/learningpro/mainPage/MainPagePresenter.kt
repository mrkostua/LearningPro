package mr.kostua.learningpro.mainPage

import android.content.ContentResolver
import android.net.Uri
import mr.kostua.learningpro.tools.ShowLogs
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
class MainPagePresenter @Inject constructor(private val contentResolver: ContentResolver) : MainPageContract.Presenter {
    private val TAG = this.javaClass.simpleName
    private lateinit var view: MainPageContract.View

    override fun start() {

    }

    override fun takeView(view: MainPageContract.View) {
        this.view = view
    }

    //TODO data must be processed in separate thread and after inserted into DB
    //RXJava and Room
    //during working in background for now show some progressBar
    override fun processData(data: Uri) {
        val text = StringBuffer()
        try {
            val contRes = contentResolver.openInputStream(data)
            val br = BufferedReader(InputStreamReader(contRes))

            var line: String? = ""
            while (line != null) {
                text.append(line)
                line = br.readLine()
            }
            br.close()
        } catch (e: FileNotFoundException) {
            ShowLogs.log(TAG, "FileNotFoundException + ${e.message}")
        } catch (e: IOException) {
            ShowLogs.log(TAG, "IOException2 + ${e.message}")

        }
        ShowLogs.log(TAG, "Text is : $text")


    }
}