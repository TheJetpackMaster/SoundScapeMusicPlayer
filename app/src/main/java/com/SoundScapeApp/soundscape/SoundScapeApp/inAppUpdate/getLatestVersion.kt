import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

suspend fun getLatestVersionAndUrl(token: String): Pair<String?, String?> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/TheJetpackMaster/SoundScapeMusicPlayer/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            //connection.setRequestProperty("Authorization", "token $token")

            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(inputStream)
            val version = json.optString("tag_name") // Get the tag name
            val downloadUrl = json.optJSONArray("assets")?.getJSONObject(0)?.optString("browser_download_url") // Get the download URL

            Pair(version, downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }
}

