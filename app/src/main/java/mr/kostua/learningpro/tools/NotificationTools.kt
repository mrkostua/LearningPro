package mr.kostua.learningpro.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import mr.kostua.learningpro.R
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
class NotificationTools @Inject constructor(private val context: Context) {
    private val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChanel()
        }
    }

    fun createNewCourseNotification(courseTitle: String): NotificationCompat.Builder = NotificationCompat.Builder(context, ConstantValues.NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentTitle(context.getString(R.string.newCourseNotificationContentTitle, courseTitle))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setTicker(context.getString(R.string.newCourseNotificationTicker))
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)

    fun updateNewCourseNotificationProgress(notification: NotificationCompat.Builder, maxProgress: Int, progress: Int) {
        if (maxProgress == progress) {
            notification.setTicker("")
                    .setContentTitle(context.getString(R.string.newCourseNotificationContentTitleComplete))
        }
        notification.setProgress(maxProgress, progress, false)

        notificationManager.notify(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, notification.build())
    }


    fun cancelNotification(notificationID: Int) {
        notificationManager.cancel(notificationID)
    }

    fun showToastMessage(messageText: String) {
        Toast.makeText(context, messageText, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val mChannel = NotificationChannel(ConstantValues.NOTIFICATION_CHANNEL_ID,
                ConstantValues.NOTIFICATION_CHANEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
        with(mChannel) {
            description = ConstantValues.NOTIFICATION_CHANEL_DESCRIPTION
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(mChannel)
    }
}