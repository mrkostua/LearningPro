package mr.kostua.learningpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
@Entity(tableName = "courses")
data class CourseDo(@PrimaryKey(autoGenerate = true) var id: Int? = null, var title: String,
                    var description: String, var questionsAmount: Int = 0,
                    var doneQuestionsAmount: Int = 0, var reviewed: Boolean = false)