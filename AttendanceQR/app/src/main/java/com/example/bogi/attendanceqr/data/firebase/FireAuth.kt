package com.example.bogi.attendanceqr.data.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.bogi.attendanceqr.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single

class FireAuth {

    companion object {
        val instance = FireAuth()
        const val GOOGLE_SIGN_IN = 193
    }

    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var userNIU: String = ""
    var userPhone: String = ""
    var userRole: String = ""

    fun signInWithGoogle(account: GoogleSignInAccount): Completable {
        return Completable.create { e ->
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) e.onComplete()
                    else {
                        Log.e("NOM", task.exception?.toString())
                        e.onError(Throwable("Authentication Failed"))
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
        }
    }

    fun getUserRole(userId: String): Single<String> {
        return Single.create { e ->
            db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val role = it["role"].toString()
                    e.onSuccess(role)
                    userRole = role
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }



    fun getUserNIU(userId: String): Single<String> {
        return Single.create { e ->
            db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val niu = it["NIU"].toString()
                    e.onSuccess(niu)
                    userNIU = niu
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun getUserPhone(userId: String): Single<String> {
        return Single.create { e ->
            db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val phone = it["phone"].toString()
                    e.onSuccess(phone)
                    userPhone = phone
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun googleSignInIntent(ctx: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ctx.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(ctx, gso).signInIntent
    }

    fun checkOut(ctx: Context) {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ctx.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(ctx, gso).signOut()
    }

}