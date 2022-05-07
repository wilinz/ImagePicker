package com.wilinz.imagepicker.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AlbumUtil {
    private val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val orderBy = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"

    suspend fun getImages(
        contentResolver: ContentResolver,
        bucketId: String,
        mimeType: List<String>
    ): List<Uri> {
        return withContext(Dispatchers.IO) {
            val projections = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
            )

            val bucketWhere = MediaStore.Images.ImageColumns.BUCKET_ID + " =? "

            val list = mutableListOf<Uri>()

            val cursor = if (bucketId != "-1")
                contentResolver.query(
                    contentUri,
                    projections,
                    bucketWhere,
                    arrayOf(bucketId),
                    orderBy
                )
            else contentResolver.query(contentUri, projections, null, null, orderBy)

            cursor?.use {
                if (cursor.moveToFirst()) {
                    do {
                        val uri = cursor.getUri()
                        list.add(uri)
                    } while (cursor.moveToNext())
                }
            }
            list
        }

    }

    private fun Cursor.getBucketId(): String =
        getString(getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))


    private fun Cursor.getBucketDisplayName(): String =
        getString(getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))

    private fun Cursor.getUri(): Uri =
        Uri.parse(getString(getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)))

    suspend fun getAlbums(contentResolver: ContentResolver): List<Album> {
        return withContext(Dispatchers.IO) {
            val t1 = System.currentTimeMillis()
            val list = mutableListOf<Album>()
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projections = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
            )


            val albums = HashMap<String, Int>()

            val cur = contentResolver.query(contentUri, projections, null, null, orderBy)

            cur?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val bucketId = cursor.getBucketId()

                        if (albums[bucketId] == null) {
                            val bucketName = cursor.getBucketDisplayName()
                            val lastImageUri = cursor.getUri()
                            val album = Album(
                                id = bucketId,
                                displayName = bucketName,
                                uri = lastImageUri
                            )
                            album.count++
                            list.add(album)
                            albums[bucketId] = list.lastIndex
                        } else {
                            list[albums[bucketId]!!].count++
                        }
                    } while (cursor.moveToNext())
                    val allImage = Album(
                        id = "-1",
                        displayName = "All",
                        uri = list.firstOrNull()?.uri,
                        count = list.sumOf { it.count }
                    )
                    list.add(0,allImage)
                }
            }

            val t2 = System.currentTimeMillis()
            Log.d("getAlbums time: ", ((t2 - t1).toString()))
            list
        }
    }
}

data class Album(
    var id: String = "",
    var uri: Uri? = null,
    var displayName: String = "",
    var count: Int = 0
)