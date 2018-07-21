package mr.kostua.learningpro.data.local

import android.arch.persistence.room.*
import io.reactivex.Single

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertCourse(course: CourseDo): Long

    @Transaction
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Single<List<CourseDo>>

    @Query("UPDATE courses SET questionsAmount = :questionsAmount WHERE id = :courseId")
    fun updateCourseQuestionsAmount(courseId : Int, questionsAmount : Int): Int
}