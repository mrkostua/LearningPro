package mr.kostua.learningpro.data.local

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single

/**
 * @author Kostiantyn Prysiazhnyi on 7/21/2018.
 */
@Dao
interface CourseWithQuestionsDao {
    @Query("SELECT * from courses")
    fun getCoursesWithQuestions(): List<CourseWithQuestions>

    @Query("SELECT * from courses WHERE id = :courseId")
    fun getCourseWithQuestions(courseId: Int): Single<CourseWithQuestions>
}