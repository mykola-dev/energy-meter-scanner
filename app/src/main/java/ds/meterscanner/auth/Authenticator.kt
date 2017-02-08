package ds.meterscanner.auth

import L
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable
import javax.inject.Inject


class Authenticator @Inject constructor(
    @Inject val auth: FirebaseAuth,
    @Inject val firebaseAnalytics: FirebaseAnalytics
) {

    private val authListeners = mutableMapOf<String, FirebaseAuth.AuthStateListener>()

    init {
        L.i("::: AuthManager initialized")
    }

    fun startListen(obj: Any, callback: (Boolean) -> Unit) {
        val listener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            callback(user != null)
            firebaseAnalytics.setUserId(user?.email ?: user?.uid)
            firebaseAnalytics.setUserProperty("email", user?.email)
        }
        authListeners.put(obj.toString(), listener)
        auth.addAuthStateListener(listener)
    }

    fun stopListen(obj: Any) {
        auth.removeAuthStateListener(authListeners[obj.toString()]!!)
        authListeners.remove(obj.toString())
    }

    fun signInRx(login: String, pass: String): Completable = Completable.create({ s ->
        auth.signInWithEmailAndPassword(login, pass)
            .addOnSuccessListener { s.onComplete() }
            .addOnFailureListener { s.onError(it) }
    })

    fun signUpRx(login: String, pass: String): Completable = Completable.create({ s ->
        auth.createUserWithEmailAndPassword(login, pass)
            .addOnSuccessListener { s.onComplete() }
            .addOnFailureListener { s.onError(it) }
    })

    fun signOut() {
        auth.signOut()
    }

    fun getUser(): FirebaseUser? = auth.currentUser

    fun isLoggedIn() = getUser() != null
}