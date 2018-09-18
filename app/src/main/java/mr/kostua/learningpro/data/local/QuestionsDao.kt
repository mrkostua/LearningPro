package mr.kostua.learningpro.data.local

import android.arch.persistence.room.*
import io.reactivex.Single

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun addQuestion(questionDo: QuestionDo): Long

    @Update
    fun updateQuestion(questionDo: QuestionDo): Int

    @Delete
    fun deleteQuestion(questionDo: QuestionDo): Int

    @Query("SELECT * FROM questions WHERE courseId = :courseId AND isAccepted = 0")
    fun getAllNotAcceptedQuestionsFromCourse(courseId: Int): Single<List<QuestionDo>>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    fun getQuestionFromCourse(questionId: Int): Single<List<QuestionDo>>

    @Query("SELECT * FROM questions WHERE courseId = :courseId")
    fun getAllQuestionsFromCourse(courseId: Int): Single<List<QuestionDo>>

    @Query("SELECT * FROM questions WHERE courseId = :courseId AND isLearned = 0")
    fun getAllNotLearnedQuestionsFromCourse(courseId: Int): Single<List<QuestionDo>>

    @Query("SELECT COUNT(id) FROM questions WHERE courseId = :courseId")
    fun countQuestionsAmountInCourse(courseId: Int): Int

}