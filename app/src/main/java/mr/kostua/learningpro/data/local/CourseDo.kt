package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
@Entity(tableName = "questions")
data class CourseDo(@PrimaryKey(autoGenerate = true) var id: Int? = null, var title: String,
                    var description: String, var questionsAmount: Int, var doneQuestionsAmount: Int)