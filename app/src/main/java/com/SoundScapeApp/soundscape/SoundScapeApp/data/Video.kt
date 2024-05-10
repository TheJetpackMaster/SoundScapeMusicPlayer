import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Immutable


@Immutable
data class Video(
    val uri: String = "",
    val displayName: String = "",
    val data:String = "",
    val id: Long = 0L,
    val dateAdded: String = "",
    val duration: Int = 0,
    val sizeMB: String = "",
    val thumbnail: String = "",
    val album: String = "",
    val bucketName: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!, // Read size
        parcel.readString()!!, // Read thumbnail
        parcel.readString()!!, // Read album
        parcel.readString()!! // Read bucket name
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
        parcel.writeString(displayName)
        parcel.writeString(data)
        parcel.writeLong(id)
        parcel.writeString(dateAdded)
        parcel.writeInt(duration)
        parcel.writeString(sizeMB) // Write size
        parcel.writeString(thumbnail) // Write thumbnail
        parcel.writeString(album) // Write album
        parcel.writeString(bucketName) // Write bucket name
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}
