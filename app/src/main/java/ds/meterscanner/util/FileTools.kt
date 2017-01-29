package ds.meterscanner.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import ds.meterscanner.activity.Requests
import ds.meterscanner.rx.applySchedulers
import io.reactivex.Completable


object FileTools {

    val CSV_FILE_NAME = "meter_data.csv"

    fun chooseDir(activity: Activity, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        activity.startActivityForResult(intent, Requests.SAVE_FILE)
    }

    fun saveFile(cr: ContentResolver, uri: Uri, data: List<String>): Completable {
        return Completable.fromAction {
            cr.openOutputStream(uri).bufferedWriter().use { writer ->
                data.forEach {
                    writer.write(it)
                    writer.newLine()
                }
            }
        }.applySchedulers()
    }

}