package com.example.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object MlKitOcr {
    private const val TAG = "MlKitOcr"

    suspend fun recognizeText(bitmap: Bitmap): String = suspendCancellableCoroutine { cont ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    cont.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ML Kit recognition failed", e)
                    cont.resume("On-device OCR failed: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error in ML Kit OCR", e)
            cont.resume("On-device OCR error: ${e.message}")
        }
    }
}
