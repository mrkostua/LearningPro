package mr.kostua.learningpro.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Kostiantyn Prysiazhnyi on 7/21/2018.
 */
class CourseWithQuestions {
    @Embedded
    var course: CourseDo? = null
    @Relation(parentColumn = "id", entityColumn = "courseId")
    var questionsList: List<QuestionDo> = ArrayList()
}