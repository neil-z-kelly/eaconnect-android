package com.ea.connect.data

import com.ea.connect.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class BackendException(val statusCode: Int, val rawBody: String, url: String) :
    IOException("HTTP $statusCode from $url\n$rawBody")

data class PartyInviteResult(
    val partyId: String,
    val gamertag: String,
    val network: String,
    val expiresInSeconds: Int,
)

class EaConnectApi(private val baseUrl: String = BuildConfig.BACKEND_BASE_URL) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = "application/json; charset=utf-8".toMediaType()

    fun sendPartyInvite(accountId: String, friend: Friend, game: String): PartyInviteResult {
        val url = "${baseUrl.trimEnd('/')}/api/eaconnect/party/invite"
        val body = JSONObject()
            .put("account_id", accountId)
            .put("friend_id", friend.id)
            .put("game", game)
            .apply { if (BuildConfig.DEVIN_ORG_ID.isNotBlank()) put("devinOrgId", BuildConfig.DEVIN_ORG_ID) }
            .toString()
            .toRequestBody(json)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Accept", "application/json")
            .header("X-EAConnect-Client", "android/${BuildConfig.VERSION_NAME}")
            .apply {
                if (BuildConfig.DEMO_TOKEN.isNotBlank()) {
                    header("X-EAConnect-Demo-Token", BuildConfig.DEMO_TOKEN)
                }
            }
            .build()

        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw BackendException(response.code, text, url)
            }
            val obj = JSONObject(text)
            return PartyInviteResult(
                partyId = obj.getString("partyId"),
                gamertag = obj.getJSONObject("to").getString("gamertag"),
                network = obj.getJSONObject("to").getString("network"),
                expiresInSeconds = obj.getInt("expiresInSeconds"),
            )
        }
    }
}
