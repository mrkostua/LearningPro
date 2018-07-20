package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

/**
 * @author Kostiantyn Prysiazhnyi on 7/20/2018.
 */
@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertCourse(course: CourseDo): Long

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Single<List<CourseDo>>
}