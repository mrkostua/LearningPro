package mr.kostua.learningpro.data.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Database(entities = [(QuestionDo::class), (CourseDo::class)], version = 3)
abstract class LocalDB : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
    abstract fun coursesDao(): CourseDao
    abstract fun courseWithQuestionsDao(): CourseWithQuestionsDao

    companion object {
        private val migrationFrom2To3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE courses ADD 'reviewed' INTEGER NOT NULL default 0")
            }
        }
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
                                .addMigrations(migrationFrom2To3)
                                .build()
                        instance = created
                        created
                    }
                }
            }
        }
    }
}