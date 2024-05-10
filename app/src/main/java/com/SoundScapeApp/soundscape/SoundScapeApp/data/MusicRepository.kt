package com.SoundScapeApp.soundscape.SoundScapeApp.data

import Video
import android.os.Build
import androidx.annotation.RequiresApi
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.ContentResolverHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val contentResolver: ContentResolverHelper,
    private val scope: CoroutineScope
) {
    suspend fun getAudioData(): List<Audio> = withContext(Dispatchers.IO) {
        contentResolver.getAudioData()
    }
//    fun getAudioDataPaging(): Flow<PagingData<Audio>> {
//        return Pager(
//            config = PagingConfig(pageSize = 15),
//            pagingSourceFactory = { com.example.soundscape.SoundScapeApp.helperClasses.AudioPagingSource(contentResolver) }
//        ).flow.cachedIn(scope)
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getVideoData(): List<Video> = withContext(Dispatchers.IO) {
        contentResolver.getVideoData()
    }
}