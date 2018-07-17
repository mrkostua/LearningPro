package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Database(entities = [(QuestionDo::class)], version = 1)
abstract class LocalDB : RoomDatabase() {
    abstract fun questionsDao() : QuestionsDao
    companion object {
        @Volatile
        private var instance: LocalDB? = null

        fun getInstance(context: Context): LocalDB {
            val inst = instance
            if (inst != null) {
                return inst
            } else {
                return synchronized(this) {
                    val inst2 = instance
                    if (inst2 != null) {
                        inst2
                    } else {
                        val created = Room.databaseBuilder(context.applicationContext,
                                LocalDB::class.java, "LearningPro.db")
                                .build()
                        instance = created
                        created
                    }
                }
            }
        }
    }
}