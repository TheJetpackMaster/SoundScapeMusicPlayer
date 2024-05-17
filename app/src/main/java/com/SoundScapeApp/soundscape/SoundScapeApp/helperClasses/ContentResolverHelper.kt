package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses

import Video
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.media3.common.util.UnstableApi
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {

    private var mCursor: Cursor? = null

    private val audioProjection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM_ID,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.DATE_ADDED)

    private val videoProjection: Array<String> = arrayOf(
        MediaStore.Video.VideoColumns.DISPLAY_NAME,
        MediaStore.Video.VideoColumns._ID,
        MediaStore.Video.VideoColumns.ARTIST,
        MediaStore.Video.VideoColumns.DATA,
        MediaStore.Video.VideoColumns.DURATION,
        MediaStore.Video.VideoColumns.TITLE,
        MediaStore.Video.VideoColumns.ALBUM,
        MediaStore.Video.VideoColumns.DATE_ADDED,
        MediaStore.Video.VideoColumns.SIZE,
        MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)


    private val supportedAudioMimeTypes = arrayOf(
        "audio/mpeg",    // MP3
        "audio/mp3",     // MP3
        "audio/mp4",     // M4A
        "audio/flac"     // FLAC
        // Add more supported audio MIME types as needed
    )

    private val supportedVideoMimeTypes = arrayOf(
        "video/mp4",     // MP4
        "video/mpeg",    // MPEG
        "video/3gpp",    // 3GP
        "video/x-matroska"     // MKV
        // Add more supported video MIME types as needed
    )


    private val audioSelectionClause = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.AudioColumns.MIME_TYPE} IN (${supportedAudioMimeTypes.joinToString(",") { "?" }})"
    private val audioSelectionArgs = arrayOf("1") + supportedAudioMimeTypes

    // Filter selection clause for video based on MIME types
    private val videoSelectionClause = "${MediaStore.Video.VideoColumns.MIME_TYPE} IN (${supportedVideoMimeTypes.joinToString(",") { "?" }})"
    private val videoSelectionArgs = supportedVideoMimeTypes

    private val audioSortOrder = "${MediaStore.Audio.AudioColumns.TITLE} DESC"
    private val videoSortOrder = "${MediaStore.Video.VideoColumns.DATE_ADDED} DESC"



    @WorkerThread
    suspend fun getAudioData(): MutableList<Audio> {
        return withContext(Dispatchers.IO) {
            getAudioCursorData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    suspend fun getVideoData(): MutableList<Video> {
        return withContext(Dispatchers.IO) {
            getVideoCursorData()
        }
    }


    @androidx.annotation.OptIn(UnstableApi::class)
    private fun getAudioCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            audioProjection,
            audioSelectionClause,
            audioSelectionArgs,
            audioSortOrder
        )

        mCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val albumIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            val albumNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val dateAddedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)


            cursor.apply {
                if (count == 0) {
                    Log.e("Cursor", "getCursorData: Cursor is Empty")
                } else {
                    while (cursor.moveToNext()) {
                        val displayName = getString(displayNameColumn) ?:"Unknown"
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn) ?:"Unknown"
                        val data = getString(dataColumn) ?:"Unknown"
                        val duration = getInt(durationColumn) ?:0
                        val title = getString(titleColumn) ?:"Unknown"
                        val albumId = getString(albumIdColumn) ?:"Unknown"
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val artworkUri = loadArtworkUri(albumId).toString()
                        val albumName = getString(albumNameColumn)
                        val dateAdded = getString(dateAddedColumn)


                        audioList += Audio(
                            uri,
                            displayName,
                            id,
                            artist,
                            data,
                            dateAdded,
                            duration,
                            title,
                            artworkUri,
                            albumId,
                            albumName,
                        )
                    }
                }
            }
        }
        return audioList
    }

    @SuppressLint("InlinedApi", "DefaultLocale") // Suppressing warning for deprecated constant usage
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getVideoCursorData(): MutableList<Video> {
        val videoList = mutableListOf<Video>()

        mCursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoProjection,
            videoSelectionClause,
            videoSelectionArgs,
            videoSortOrder
        )

        mCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
            val dateAddedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED)
            val bucketColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)
            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn) ?: 0L
                val data = cursor.getString(dataColumn)?:"Unknown"
                val displayName = cursor.getString(displayNameColumn) ?:"Unknown"
                val duration = cursor.getInt(durationColumn)
                val dateAdded = cursor.getString(dateAddedColumn) ?:"Unknown"
                val bucketName = cursor.getString(bucketColumn)?:"Unknown"
                val sizeBytes = cursor.getLong(sizeColumn) ?: 0
                val sizeMB = String.format("%.2f", sizeBytes / (1000.0 * 1000.0))
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                videoList += Video(
                    uri = uri.toString(),
                    displayName = displayName,
                    id = id,
                    dateAdded = dateAdded,
                    duration = duration,
                    bucketName = bucketName,
                    sizeMB = sizeMB,
                    thumbnail = data
                )
            }
        }
        return videoList
    }


    private fun loadArtworkUri(albumId: String): Uri? {
        return Uri.parse("content://media/external/audio/albumart/$albumId")
    }
}