package ds.meterscanner.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import ds.meterscanner.mvvm.activity.Requests
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run


object FileTools {

    val CSV_FILE_NAME = "meter_data.csv"

    fun chooseDir(activity: Activity, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        activity.startActivityForResult(intent, Requests.SAVE_FILE)
    }

    suspend fun saveFile(cr: ContentResolver, uri: Uri, data: List<String>) = run(CommonPool) {
        cr.openOutputStream(uri).bufferedWriter().use { writer ->
            data.forEach {
                writer.write(it)
                writer.newLine()
            }
        }
    }

}