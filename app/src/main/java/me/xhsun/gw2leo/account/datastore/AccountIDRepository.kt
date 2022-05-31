package me.xhsun.gw2leo.account.datastore

import android.content.Context
import android.content.SharedPreferences
import me.xhsun.gw2leo.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.config.SHARED_PREFERENCES_FILE_NAME
import javax.inject.Inject


class AccountIDRepository @Inject constructor(private val context: Context) : IAccountIDRepository {
    override fun getCurrent(): String {
        val sharedPref =
            context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(ACCOUNT_ID_KEY, "") ?: ""
    }

    override fun updateCurrent(accountID: String): Boolean {
        val sharedPref =
            context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(ACCOUNT_ID_KEY, accountID)
        editor.apply()
        return editor.commit()
    }
}