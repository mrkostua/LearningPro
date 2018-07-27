package mr.kostua.learningpro.data.local

import android.arch.persistence.room.*

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun addQuestion(questionDo: QuestionDo): Long
}