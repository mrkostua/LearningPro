package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Entity(tableName = "questions", foreignKeys = [ForeignKey(entity = CourseDo::class, parentColumns = ["id"], childColumns = ["courseId"], onDelete = ForeignKey.CASCADE)])
data class QuestionDo(@PrimaryKey(autoGenerate = true) var id: Int? = null, var question: String,
                      var answer: String, var isAccepted: Boolean = false, var courseId: Int, var viewsCount: Int = 0, var isLearned: Boolean = false)
