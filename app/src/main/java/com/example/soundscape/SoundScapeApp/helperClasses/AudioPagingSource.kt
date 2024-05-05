package com.example.soundscape.SoundScapeApp.helperClasses

//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.soundscape.SoundScapeApp.data.Audio

//class AudioPagingSource(
//    private val contentResolverHelper: ContentResolverHelper
//) : PagingSource<Int, Audio>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Audio> {
//        try {
//            // Load data from content resolver helper
//            val audioList = contentResolverHelper.getAudioData()
//
//            // Page key to indicate next page
//            val nextPageNumber = params.key ?: 0
//
//            // Return data and page information
//            return LoadResult.Page(
//                data = audioList,
//                prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1,
//                nextKey = nextPageNumber + 1
//            )
//        } catch (e: Exception) {
//            // Handle errors
//            return LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Audio>): Int? {
//        // Use null as the key for the initial load
//        return null
//    }
//}
