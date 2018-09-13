package mr.kostua.learningpro.data.local

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * @author Kostiantyn Prysiazhnyi on 7/21/2018.
 */
class CourseWithQuestions {
    @Embedded
    var course: CourseDo? = null
    @Relation(parentColumn = "id", entityColumn = "courseId")
    var questionsList: List<QuestionDo> = ArrayList()
}