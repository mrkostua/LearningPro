package mr.kostua.learningpro.data.local.sharedPref

/**
 * @author Kostiantyn Prysiazhnyi on 7/24/2018.
 */
enum class ConstantsPreferences {
    APP_SP_NAME() {
        override fun getKeyValue() = "learningProSharedPRef"
        override fun getDefaultStringValue() = emptyCreatingCourseData
    },
    CREATING_COURSE_TITLE() {
        override fun getDefaultStringValue() = emptyCreatingCourseData
        override fun getKeyValue() = "CREATING_COURSE_TITLE_KEY"
    },
    CREATING_COURSE_DESCRIPTION() {
        override fun getDefaultStringValue() = emptyCreatingCourseData
        override fun getKeyValue() = "CREATING_COURSE_DESCRIPTION_KEY"
    },
    CREATING_COURSE_URI() {
        override fun getDefaultStringValue() = emptyCreatingCourseData
        override fun getKeyValue() = "CREATING_COURSE_URL_KEY"
    };

    abstract fun getKeyValue(): String
    abstract fun getDefaultStringValue(): String

    val emptyCreatingCourseData = ""
}