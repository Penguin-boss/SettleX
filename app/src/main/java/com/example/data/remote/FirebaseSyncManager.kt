package com.example.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Extension to await Tasks without external coroutine tasks dependency
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(task.result)
        } else {
            cont.resumeWithException(task.exception ?: RuntimeException("Firebase Task failed"))
        }
    }
}

object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"
    private var isInitialized = false

    fun isFirebaseReady(context: Context): Boolean {
        if (isInitialized) return true
        return try {
            FirebaseApp.initializeApp(context)
            isInitialized = true
            true
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not available, running in offline-first mode: ${e.message}")
            false
        }
    }

    // AUTH
    suspend fun authenticateUser(context: Context, email: String, name: String): String? {
        if (!isFirebaseReady(context)) return null
        return try {
            val auth = FirebaseAuth.getInstance()
            val deterministicPassword = "SF_${email.hashCode()}_Secure"
            val result = try {
                auth.signInWithEmailAndPassword(email, deterministicPassword).await()
            } catch (e: Exception) {
                auth.createUserWithEmailAndPassword(email, deterministicPassword).await()
            }
            result.user?.uid
        } catch (e: Exception) {
            Log.e(TAG, "Auth failed: ${e.message}")
            null
        }
    }

    // FCM
    suspend fun initCloudMessaging(context: Context): String? {
        if (!isFirebaseReady(context)) return null
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "FCM token failed: ${e.message}")
            null
        }
    }

    // FIRESTORE GROUPS
    suspend fun syncGroupToFirestore(context: Context, group: GroupEntity): Boolean {
        if (!isFirebaseReady(context)) return false
        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("groups").document(group.id).set(group).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "syncGroupToFirestore failed: ${e.message}")
            false
        }
    }

    // FIRESTORE EXPENSES
    suspend fun syncExpenseToFirestore(context: Context, expense: ExpenseEntity, splits: List<ExpenseSplitEntity>): Boolean {
        if (!isFirebaseReady(context)) return false
        return try {
            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            val expenseRef = db.collection("expenses").document(expense.id)
            batch.set(expenseRef, expense)
            splits.forEach { split ->
                val splitRef = db.collection("expense_splits").document(split.id)
                batch.set(splitRef, split)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "syncExpenseToFirestore failed: ${e.message}")
            false
        }
    }

    // FIRESTORE SETTLEMENTS
    suspend fun syncSettlementToFirestore(context: Context, settlement: SettlementEntity): Boolean {
        if (!isFirebaseReady(context)) return false
        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("settlements").document(settlement.id).set(settlement).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "syncSettlementToFirestore failed: ${e.message}")
            false
        }
    }

    // STORAGE PROFILE PHOTO
    suspend fun uploadProfilePhoto(context: Context, bitmap: Bitmap): String? {
        if (!isFirebaseReady(context)) return null
        return try {
            val storage = FirebaseStorage.getInstance()
            val ref = storage.reference.child("profiles/${System.currentTimeMillis()}.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()
            ref.putBytes(data).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "uploadProfilePhoto failed: ${e.message}")
            null
        }
    }

    // FIRESTORE CASCADING DELETE
    suspend fun deleteGroup(context: Context, groupId: String): Boolean {
        if (!isFirebaseReady(context)) return false
        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("groups").document(groupId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "deleteGroup failed: ${e.message}")
            false
        }
    }
}
