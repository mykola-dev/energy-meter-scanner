package ds.meterscanner.coroutines

import L
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

suspend inline fun <reified T : Any> Query.getValue(crossinline logic: (DataSnapshot, Class<T>) -> T) = suspendCancellableCoroutine<T> { continuation ->

    val listener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = logic(snapshot, T::class.java)
            L.v("snapshot=$value")
            continuation.resume(value)
        }

        override fun onCancelled(error: DatabaseError) {
            continuation.resumeWithException(error.toException())
        }
    }

    addListenerForSingleValueEvent(listener)

    continuation.invokeOnCompletion {
        L.v("onComplete")
        removeEventListener(listener)
    }

}

suspend inline fun <reified T : Any> Query.getChildValue() = getValue<T> { data, cls ->
    val values = data.children.map { it.getValue(cls) }
    if (!values.isEmpty()) {
        L.v("latest value $values")
        values[0]!!
    } else
        throw IllegalStateException("empty childs")
}

suspend inline fun <reified T : Any> Query.getValue(): T = getValue { data, cls ->
    data.getValue(cls)!!
}

suspend inline fun <reified T : Any> Query.getValues() = suspendCancellableCoroutine<List<T>> { continuation ->

    val listener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.children.map { it.getValue(T::class.java)!! }
            L.v("snapshot=$value")
            continuation.resume(value)
        }

        override fun onCancelled(error: DatabaseError) {
            continuation.resumeWithException(error.toException())
        }
    }

    addListenerForSingleValueEvent(listener)

    continuation.invokeOnCompletion {
        L.v("onComplete")
        removeEventListener(listener)
    }

}

suspend inline fun <reified T : Any> Query.listenValues() = produce<List<T>>(coroutineContext(), Channel.UNLIMITED) {
    val channel = Channel<List<T>>(Channel.UNLIMITED)
    val listener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            L.i("count ${snapshot.childrenCount}")
            val values = snapshot.children.map { it.getValue(T::class.java)!! }
            L.v("sent to channel? ${channel.offer(values)}")
        }

        override fun onCancelled(error: DatabaseError) {
            channel.close()
        }
    }

    addValueEventListener(listener)

    try {
        for (any in channel) {
            offer(any)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    } finally {
        L.e("coroutine finalized")
        removeEventListener(listener)
    }
}

// https://github.com/Kotlin/kotlinx.coroutines/issues/114
suspend fun coroutineContext(): CoroutineContext = suspendCoroutineOrReturn { cont -> cont.context }