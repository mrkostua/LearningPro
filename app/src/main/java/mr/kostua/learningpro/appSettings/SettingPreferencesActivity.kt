package mr.kostua.learningpro.appSettings

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import mr.kostua.learningpro.R

/**
 * @author Kostiantyn Prysiazhnyi on 7/26/2018.
 */
class SettingPreferencesActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, FragmentPreferences()).commit()
    }

    class FragmentPreferences : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_preferences)
        }
    }

}