package ds.meterscanner.rx

import L
import android.support.annotation.MainThread
import com.google.firebase.database.*
import ds.meterscanner.databinding.BaseView
import ds.meterscanner.ui.Progressable
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Flowable<T>.toggleProgress(obj: Progressable): Flowable<T> = compose {
    it
        .doOnSubscribe { obj.toggleProgress(true) }
        .doOnTerminate { obj.toggleProgress(false) }
}

fun <T> Observable<T>.toggleProgress(obj: Progressable): Observable<T> = compose {
    it
        .doOnSubscribe { obj.toggleProgress(true) }
        .doOnTerminate { obj.toggleProgress(false) }
}

fun <T> Maybe<T>.toggleProgress(obj: Progressable): Maybe<T> = compose {
    it
        .doOnSubscribe { obj.toggleProgress(true) }
        .doOnSuccess { obj.toggleProgress(false) }
        .doOnError { obj.toggleProgress(false) }
}

fun <T> Single<T>.toggleProgress(obj: Progressable): Single<T> = compose {
    it
        .doOnSubscribe { obj.toggleProgress(true) }
        .doOnSuccess { obj.toggleProgress(false) }
        .doOnError { obj.toggleProgress(false) }
}

fun Completable.toggleProgress(obj: Progressable): Completable = compose {
    it
        .doOnSubscribe { obj.toggleProgress(true) }
        .doOnTerminate { obj.toggleProgress(false) }
}

fun <T> Flowable<T>.applySchedulers() = compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
fun <T> Observable<T>.applySchedulers() = compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
fun <T> Single<T>.applySchedulers() = compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
fun <T> Maybe<T>.applySchedulers() = compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
fun Completable.applySchedulers() = compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }

@MainThread
fun onErrorSnackbar(view: BaseView): (Throwable) -> Unit = { t ->
    t.printStackTrace()
    view.showSnackbar(t.message ?: "Unknown Error")
}

inline fun <reified T : Any> Query.listenValues(): Observable<List<T>> {
    return Observable.create<List<T>> {
        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                L.i("count ${snapshot.childrenCount}")
                val values = snapshot.children.map { it.getValue(T::class.java) }
                it.onNext(values)
            }

            override fun onCancelled(error: DatabaseError) {
                it.onError(error.toException())
            }
        }

        addValueEventListener(listener)
        it.setCancellable { removeEventListener(listener) }
    }
}

inline fun <reified T : Any> Query.getValues(): Single<List<T>> {
    return Single.create<List<T>> {
        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                L.i("count ${snapshot.childrenCount}")
                val values = snapshot.children.map { it.getValue(T::class.java) }
                it.onSuccess(values)
            }

            override fun onCancelled(error: DatabaseError) {
                it.onError(error.toException())
            }
        }

        addListenerForSingleValueEvent(listener)
        it.setCancellable { removeEventListener(listener) }
    }
}

inline fun <reified T : Any> DatabaseReference.getValue(): Single<T> {
    return Single.create<T> {
        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                L.i("count ${snapshot.childrenCount}")
                val value = snapshot.getValue(T::class.java)
                it.onSuccess(value)
            }

            override fun onCancelled(error: DatabaseError) {
                it.onError(error.toException())
            }
        }

        addListenerForSingleValueEvent(listener)
        it.setCancellable { removeEventListener(listener) }
    }
}

inline fun <reified T : Any> Query.getValue(): Maybe<T> {
    return Maybe.create<T> {
        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val values = snapshot.children.map { it.getValue(T::class.java) }
                if (!values.isEmpty()) {
                    L.v("latest value $values")
                    it.onSuccess(values[0])
                } else {
                    L.v("latest value empty")
                    it.onComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                it.onError(error.toException())
            }
        }

        addListenerForSingleValueEvent(listener)
        it.setCancellable { removeEventListener(listener) }
    }
}