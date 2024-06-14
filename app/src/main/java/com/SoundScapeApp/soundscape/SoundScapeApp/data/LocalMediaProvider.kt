package com.SoundScapeApp.soundscape.SoundScapeApp.data

import Video
import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.io.File

class LocalMediaProvider(
    private val applicationContext: Application
) {
    fun getVideoItemFromContentUri(uri: Uri): Video? {
        var displayName: String? = null

        if (uri.scheme == "content") {
            Log.d(TAG, "Uri scheme is content")
            applicationContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    displayName =
                        cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        } else {
            Log.d(TAG, "Uri scheme is file")
            displayName = uri.path?.split("/")?.lastOrNull().toString()
        }

        return if (displayName != null) {
            getMediaVideo().firstOrNull { displayName == it.displayName }
        } else {
            Log.d(TAG, "display name is null")
            null
        }
    }

    private fun getMediaVideo(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    ): List<Video> {
        val videoItems = mutableListOf<Video>()
        applicationContext.contentResolver.query(
            VIDEO_COLLECTION_URI,
            VIDEO_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val absolutePath = cursor.getString(dataColumn)
                val displayName = cursor.getString(displayNameColumn) ?: "Unknown"
                videoItems.add(
                    Video(
                        id = id,
                        displayName = displayName,
                        data = absolutePath,
                        duration = cursor.getLong(durationColumn).toInt(),
                        uri = ContentUris.withAppendedId(VIDEO_COLLECTION_URI, id).toString()
                    )
                )
            }
        }
        return videoItems.filter { File(it.data).exists() }
    }

    fun getAudioItemFromContentUri(uri: Uri): Audio? {

        var displayName: String? = null

        if (uri.scheme == "content") {
            Log.d(TAG, "Uri scheme is content")
            applicationContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                displayName =
                    cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } else {
            Log.d(TAG, "Uri scheme is file")
            displayName = uri.path?.split("/")?.lastOrNull().toString()
        }

        return if (displayName != null) {
            getMediaAudio().firstOrNull { displayName == it?.displayName }
        } else {
            Log.d(TAG, "display name is null")
            null
        }
    }

    private fun getMediaAudio(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
    ): List<Audio> {
        val audioItems = mutableListOf<Audio>()
        applicationContext.contentResolver.query(
            AUDIO_COLLECTION_URI,
            AUDIO_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn) ?: 0L
                val absolutePath = cursor.getString(dataColumn) ?: "empty"
                val displayName = cursor.getString(displayNameColumn) ?: "Unknown"
                val title = cursor.getString(titleColumn) ?: "Unknown"
                val artist = cursor.getString(artistColumn) ?: "Unknown"
                val albumId = cursor.getString(albumIdColumn) ?: "0"

                val artworkUri = loadArtworkUri(albumId).toString()

                audioItems.add(
                    Audio(
                        id = id,
                        displayName = displayName,
                        data = absolutePath,
                        duration = cursor.getLong(durationColumn).toInt(),
                        uri = ContentUris.withAppendedId(AUDIO_COLLECTION_URI, id),
                        title = title,
                        artist = artist,
                        artwork = artworkUri
//                    size = cursor.getLong(sizeColumn),
//                    dateModified = cursor.getLong(dateModifiedColumn)
                    )
                )
            }
        }
        return audioItems.filter { File(it.data).exists() }
    }


    companion object {

        const val TAG = "Local Media Provider"

        val VIDEO_COLLECTION_URI: Uri
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED
        )


        val AUDIO_COLLECTION_URI: Uri
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val AUDIO_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_MODIFIED
        )
    }

}

private fun loadArtworkUri(albumId: String): Uri? {
    return Uri.parse("content://media/external/audio/albumart/$albumId")
}