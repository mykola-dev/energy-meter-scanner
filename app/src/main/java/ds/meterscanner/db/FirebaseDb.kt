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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.db.model.Snapshot
import ds.meterscanner.rx.applySchedulers
import ds.meterscanner.rx.getValue
import ds.meterscanner.rx.getValues
import ds.meterscanner.rx.listenValues
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
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
    val storage: FirebaseStorage = instance()
    val prefs: Prefs = instance()

    val storageRef: StorageReference
        get() = storage.getReference(IMAGES).child(auth.getUser()!!.uid)
    val snapshotsReference: DatabaseReference
        get() = database.getReference(USERS).child(auth.getUser()!!.uid).child(SNAPSHOTS)

    fun getAllSnapshots(startDate: Long): Single<List<Snapshot>> {
        return snapshotsReference
            .orderByChild(Snapshot::timestamp.name)
            .startAt(startDate.toDouble())
            .getValues()
    }

    fun getSnapshotById(snapshotId: String): Single<Snapshot> {
        return snapshotsReference.child(snapshotId).getValue()
    }

    fun listenSnapshots(): Observable<List<Snapshot>> {
        return snapshotsReference.orderByChild(Snapshot::timestamp.name).listenValues()
    }

    fun saveSnapshot(s: Snapshot) {
        if (s.id == null) {
            s.boilerTemp = prefs.boilerTemp
            snapshotsReference.push()
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

    fun uploadImage(bitmap: Bitmap, name: String): String {
        val scaleDown = 2
        val outStream = ByteArrayOutputStream()
        val small = Bitmap.createScaledBitmap(bitmap, bitmap.width / scaleDown, bitmap.height / scaleDown, true)
        small.compress(Bitmap.CompressFormat.JPEG, prefs.jpegQuality, outStream)
        val task = storageRef.child(name).putBytes(outStream.toByteArray())
        Tasks.await(task)
        val url = task.snapshot.downloadUrl!!.toString()
        L.v("image url: $url")
        outStream.close()
        return url
    }

    fun uploadImageRx(bitmap: Bitmap, name: String): Single<String> {
        return Single.create<String> {
            try {
                it.onSuccess(uploadImage(bitmap, name))
            } catch (e: Exception) {
                L.e("image upload failed!")
                it.onError(e)
            }
        }.applySchedulers()
    }

    fun removeImage(name: String) {
        storageRef.child(name).delete()
    }

    fun getLatestSnapshot(): Maybe<Snapshot> {
        return snapshotsReference.orderByChild(Snapshot::timestamp.name).limitToLast(1).getValue()
    }

    fun log(message: String) {
        Log.i("DBLOG", message)
        val date = DateFormat.format("hh:mm:ss dd-MM", System.currentTimeMillis()).toString()
        database.getReference(USERS).child(auth.getUser()!!.uid).child(LOG).child(date).setValue(message)
    }


}