package ds.meterscanner.coroutines

import L
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.withTimeoutOrNull
import java.util.concurrent.TimeUnit

object Locker {
    private val locks = mutableMapOf<Int, Channel<Boolean>>()

    suspend fun lock(id: Int, millis: Long): Boolean {
        val channel = locks.getOrPut(id, { Channel(1) })
        L.i("threads: lock the $id job thread")
        return withTimeoutOrNull(millis, TimeUnit.MILLISECONDS) {
            channel.receive()
        } ?: false
    }

    fun release(id: Int) {
        L.i("threads: release the $id job thread")
        locks[id]?.offer(true)
    }
}