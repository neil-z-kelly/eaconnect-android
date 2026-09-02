package com.ea.connect.data

import org.json.JSONException
import org.json.JSONObject

/**
 * Structured view of a backend failure response. The invite endpoint replies with
 * `{ success, error, message, code, requestId }`; any of those may be missing, and a
 * gateway or proxy may return a non-JSON body altogether.
 */
data class BackendError(
    val statusCode: Int,
    val error: String?,
    val message: String?,
    val code: String?,
    val requestId: String?,
) {
    companion object {
        fun parse(statusCode: Int, rawBody: String): BackendError {
            val obj = try {
                JSONObject(rawBody)
            } catch (_: JSONException) {
                null
            }
            return BackendError(
                statusCode = statusCode,
                error = obj?.optStringOrNull("error"),
                message = obj?.optStringOrNull("message"),
                code = obj?.optStringOrNull("code"),
                requestId = obj?.optStringOrNull("requestId"),
            )
        }

        private fun JSONObject.optStringOrNull(key: String): String? =
            if (has(key) && !isNull(key)) optString(key).takeIf { it.isNotBlank() } else null
    }
}
