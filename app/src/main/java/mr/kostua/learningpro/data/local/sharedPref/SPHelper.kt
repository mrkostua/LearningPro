package mr.kostua.learningpro.data.local.sharedPref

import android.content.SharedPreferences

/**
 * @author Kostiantyn Prysiazhnyi on 7/24/2018.
 */
open class SPHelper(private val sharedPreferences: SharedPreferences) {
    fun saveCreatingCourseTitle(courseTitle: String) {
        sharedPreferences[ConstantsPreferences.CREATING_COURSE_TITLE.getKeyValue()] = courseTitle
    }

    fun saveCreatingCourseDescription(courseDescription: String) {
        sharedPreferences[ConstantsPreferences.CREATING_COURSE_DESCRIPTION.getKeyValue()] = courseDescription
    }

    fun saveCreatingCourseUri(courseUri: String) {
        sharedPreferences[ConstantsPreferences.CREATING_COURSE_URI.getKeyValue()] = courseUri
    }

    fun isCreatingCourseDataExists(): Boolean = sharedPreferences[ConstantsPreferences.CREATING_COURSE_TITLE.getKeyValue(),
            ConstantsPreferences.CREATING_COURSE_TITLE.getDefaultStringValue()] == ConstantsPreferences.CREATING_COURSE_TITLE.emptyCreatingCourseData

    fun getCreatingCourseTitle() = sharedPreferences[ConstantsPreferences.CREATING_COURSE_TITLE.getKeyValue(),
            ConstantsPreferences.CREATING_COURSE_TITLE.getDefaultStringValue()]

    fun getCreatingCourseDescription() = sharedPreferences[ConstantsPreferences.CREATING_COURSE_DESCRIPTION.getKeyValue(),
            ConstantsPreferences.CREATING_COURSE_DESCRIPTION.getDefaultStringValue()]

    fun getCreatingCourseStringUri() = sharedPreferences[ConstantsPreferences.CREATING_COURSE_URI.getKeyValue(),
            ConstantsPreferences.CREATING_COURSE_URI.getDefaultStringValue()]


}