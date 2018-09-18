package mr.kostua.learningpro.tools

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import android.widget.Toast
import mr.kostua.learningpro.R
import mr.kostua.learningpro.main.MainReceiver
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

    //TODO some new updates about displaying icons on notif, read and make them visible (old and new APIs)
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

    fun displaySavedCourseNotification(courseTitle: String, courseId: Int) {
        notificationManager.notify(ConstantValues.SAVED_COURSE_NOTIFICATION_ID, NotificationCompat.Builder(context, ConstantValues.NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContentTitle(context.getString(R.string.courseSavedNotificationContentTitle, courseTitle))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(context.getString(R.string.courseSavedNotificationContentTicker))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setWhen(System.currentTimeMillis())
                .setColorized(true)
                .setColor(ResourcesCompat.getColor(context.resources, R.color.main_buttons_color, null))
                .addAction(R.drawable.ic_arrow_continue, context.getString(R.string.courseSavedNotificationActionContinue),
                        PendingIntent.getBroadcast(context, 0,
                                Intent(ConstantValues.ACTION_CONTINUE_COURSE_CREATION)
                                        .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                                        .setClass(context, MainReceiver::class.java),
                                0))
                .setAutoCancel(true).build())

    }

    private fun isNotificationVisible(notfId: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications.forEach {
                return it.id == notfId
            }
            return false
        } else {
            throw UnsupportedOperationException()
        }
    }

    fun updateNewCourseNotificationProgress(notification: NotificationCompat.Builder, maxProgress: Int, progress: Int) {
        notification.setProgress(maxProgress, progress, false)
        notificationManager.notify(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, notification.build())
    }


    fun cancelNotification(notificationID: Int) {
        notificationManager.cancel(notificationID)
    }

    fun showToastMessage(messageText: String) {
        Toast.makeText(context, messageText, Toast.LENGTH_LONG).show()
    }

    fun showCustomAlertDialog(activity: Activity, title: String, message: String,
                              buttonsClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity, R.style.CustomAlertDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.customDialogPositiveButtonText), buttonsClickListener)
                .setNegativeButton(activity.getString(R.string.customDialogNegativeButtonText), buttonsClickListener)
                .create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val mChannel = NotificationChannel(ConstantValues.NOTIFICATION_CHANNEL_ID,
                ConstantValues.NOTIFICATION_CHANEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
        with(mChannel) {
            description = context.getString(R.string.notificationsChanelDescription)
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(mChannel)
    }
}