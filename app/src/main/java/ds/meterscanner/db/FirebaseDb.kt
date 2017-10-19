package ds.meterscanner.db

import L
import android.graphics.Bitmap
import android.text.format.DateFormat
import android.util.Log
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.coroutines.getChildValue
import ds.meterscanner.coroutines.getValue
import ds.meterscanner.coroutines.getValues
import ds.meterscanner.data.Prefs
import ds.meterscanner.db.model.Snapshot
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import java.io.ByteArrayOutputStream


class FirebaseDb(override val kodein: Kodein) : KodeinAware {

    companion object {
        val USERS = "users"
        val SNAPSHOTS = "snapshots"
        val LOG = "log"
        val IMAGES = "images"
    }

    val auth: Authenticator = instance()
    val database: FirebaseDatabase = instance()
    private val storage: FirebaseStorage = instance()
    val prefs: Prefs = instance()

    private val storageRef: StorageReference
        get() = storage.getReference(IMAGES).child(auth.getUser()!!.uid)
    private val snapshotsReference: DatabaseReference
        get() = database.getReference(USERS).child(auth.getUser()!!.uid).child(SNAPSHOTS)

    suspend fun getAllSnapshots(startDate: Long): List<Snapshot> {
        return snapshotsReference
            .orderByChild(Snapshot::timestamp.name)
            .startAt(startDate.toDouble())
            .getValues()
    }

    suspend fun getSnapshotById(snapshotId: String): Snapshot = snapshotsReference.child(snapshotId).getValue()

    fun getSnapshots(): Query = snapshotsReference.orderByChild(Snapshot::timestamp.name)

    fun saveSnapshot(s: Snapshot) {
        if (s.id == null) {
            s.boilerTemp = prefs.boilerTemp
            s.id = snapshotsReference.push().key
        }
        snapshotsReference.child(s.id).setValue(s)
    }

    fun deleteSnapshots(data: List<Snapshot>) {
        for (snapshot in data) {
            snapshotsReference.child(snapshot.id).removeValue()
            removeImage(snapshot.timestamp.toString())
        }
    }

    fun keepSynced(keep: Boolean) {
        snapshotsReference.keepSynced(keep)
    }

    suspend fun uploadImage(bitmap: Bitmap, name: String): String = run(CommonPool) {
        try {
            val scaleDown = 2
            val outStream = ByteArrayOutputStream()
            val small = Bitmap.createScaledBitmap(bitmap, bitmap.width / scaleDown, bitmap.height / scaleDown, true)
            small.compress(Bitmap.CompressFormat.JPEG, prefs.jpegQuality, outStream)
            val task = storageRef.child(name).putBytes(outStream.toByteArray())
            Tasks.await(task)
            val url = task.snapshot.downloadUrl!!.toString()
            L.v("image url: $url")
            outStream.close()
            url
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun removeImage(name: String) {
        storageRef.child(name).delete()
    }

    suspend fun getLatestSnapshot(): Snapshot = snapshotsReference.orderByChild(Snapshot::timestamp.name).limitToLast(1).getChildValue()

    fun log(message: String) {
        Log.i("DBLOG", message)
        val date = DateFormat.format("hh:mm:ss dd-MM", System.currentTimeMillis()).toString()
        database.getReference(USERS).child(auth.getUser()!!.uid).child(LOG).child(date).setValue(message)
    }


}