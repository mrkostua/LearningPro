package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Entity(tableName = "questions")
data class QuestionDo(@PrimaryKey(autoGenerate = true) var id: Int? = null, var question: String,
                      var answer: String)
