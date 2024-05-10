package com.SoundScapeApp.soundscape.SoundScapeApp.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri

@Suppress("DEPRECATION")
@Immutable
data class Audio(
    val uri: Uri = "".toUri(),
    val displayName: String = "",
    val id: Long = 0L,
    val artist: String = "",
    val data: String = "",
    val dateAdded:String = "",
    val duration: Int = 1,
    val title: String = "",
    val artwork: String = "",
    val albumId: String = "",
    val albumName: String = "",
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeString(displayName)
        parcel.writeLong(id)
        parcel.writeString(artist)
        parcel.writeString(data)
        parcel.writeInt(duration)
        parcel.writeString(title)
        parcel.writeString(artwork)
        parcel.writeString(albumId)
        parcel.writeString(albumName)
        parcel.writeString(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Audio> {
        override fun createFromParcel(parcel: Parcel): Audio {
            return Audio(parcel)
        }

        override fun newArray(size: Int): Array<Audio?> {
            return arrayOfNulls(size)
        }
    }
}
