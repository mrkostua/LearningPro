package mr.kostua.learningpro.data.local

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertCourse(course: CourseDo): Long

    @Transaction
    @Query("SELECT * FROM courses ORDER BY id DESC")
    fun getAllCourses(): Flowable<List<CourseDo>>

    @Query("UPDATE courses SET questionsAmount = :questionsAmount WHERE id = :courseId")
    fun updateCourseQuestionsAmount(courseId: Int, questionsAmount: Int): Int

    @Query("UPDATE courses SET reviewed = 1 WHERE id = :courseId")
    fun updateCourseIsReviewedState(courseId: Int): Int

    @Query("UPDATE courses SET doneQuestionsAmount =doneQuestionsAmount + :doneQuestionsAmount WHERE id = :courseId")
    fun updateCourseDoneQuestionsAmount(courseId: Int, doneQuestionsAmount: Int): Int

    @Query("SELECT doneQuestionsAmount FROM courses WHERE id = :courseId")
    fun getCourseDoneQuestionsAmount(courseId: Int): Int

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourse(courseId: Int): CourseDo

}