package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class ExtractedItem(val name: String, val amount: Double)

data class ReceiptAnalysisResult(
    val title: String,
    val totalAmount: Double,
    val date: Long,
    val items: List<ExtractedItem>,
    val error: String? = null
)

object GeminiOcr {
    private const val TAG = "GeminiOcr"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun analyzeReceipt(base64Image: String): ReceiptAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ReceiptAnalysisResult(
                "", 0.0, System.currentTimeMillis(), emptyList(),
                "API key is missing. Please configure GEMINI_API_KEY in the Secrets panel."
            )
        }

        try {
            val promptText = """
                Analyze this receipt image. Extract:
                1. A short title (like store name or restaurant name)
                2. The total amount as a decimal number
                3. The date of purchase as a Unix epoch timestamp in milliseconds (if not found or unclear, use the current time)
                4. List of items with their names and individual amounts
                
                Return ONLY a valid JSON object matching this schema precisely without markdown wrappers:
                {
                  "title": "string",
                  "totalAmount": 0.0,
                  "date": 0,
                  "items": [
                    {"name": "string", "amount": 0.0}
                  ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Request failed: ${response.code} - $responseBodyString")
                return@withContext ReceiptAnalysisResult("", 0.0, System.currentTimeMillis(), emptyList(), "HTTP error ${response.code}: $responseBodyString")
            }

            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            var rawText = firstPart?.optString("text")?.trim() ?: ""

            // Clean markdown blocks if present
            if (rawText.startsWith("```json")) {
                rawText = rawText.removePrefix("```json").trim()
            }
            if (rawText.endsWith("```")) {
                rawText = rawText.removeSuffix("```").trim()
            }

            if (rawText.isEmpty()) {
                return@withContext ReceiptAnalysisResult("", 0.0, System.currentTimeMillis(), emptyList(), "Empty response from AI")
            }

            val extractedJson = JSONObject(rawText)
            val title = extractedJson.optString("title", "Scanned Receipt")
            val totalAmount = extractedJson.optDouble("totalAmount", 0.0)
            val date = extractedJson.optLong("date", System.currentTimeMillis())
            val itemsArray = extractedJson.optJSONArray("items")
            val itemsList = mutableListOf<ExtractedItem>()

            if (itemsArray != null) {
                for (i in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(i)
                    val name = itemObj.optString("name", "Item ${i + 1}")
                    val amount = itemObj.optDouble("amount", 0.0)
                    itemsList.add(ExtractedItem(name, amount))
                }
            }

            ReceiptAnalysisResult(title, totalAmount, date, itemsList)

        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing receipt", e)
            ReceiptAnalysisResult("", 0.0, System.currentTimeMillis(), emptyList(), e.message ?: "Unknown error")
        }
    }

    suspend fun suggestCategory(title: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Other"
        }

        try {
            val promptText = "Classify the expense with title \"$title\" into exactly one of these categories: Food, Transport, Utilities, Entertainment, Other. Return ONLY the category name."

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        })
                    })
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) return@withContext "Other"

            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val rawText = firstPart?.optString("text")?.trim() ?: "Other"

            if (rawText in listOf("Food", "Transport", "Utilities", "Entertainment", "Other")) {
                rawText
            } else {
                "Other"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error suggesting category", e)
            "Other"
        }
    }
}
