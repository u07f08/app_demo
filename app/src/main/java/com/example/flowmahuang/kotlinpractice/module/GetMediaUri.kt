package com.example.flowmahuang.kotlinpractice.module

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GetMediaUri(val mContext: Context, callback: (ArrayList<Map<String, String>>) -> Unit) {

    companion object {
        fun create(context: Context, callback: (ArrayList<Map<String, String>>) -> Unit) {
            GetMediaUri(context, callback)
        }
    }

    init {
        val mediaDetailArray: ArrayList<Map<String, String>> = ArrayList()

//        mediaDetailArray.addAll(getUri(getAudioProjection(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI))
//        mediaDetailArray.addAll(getUri(getVideoProjection(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI))
        mediaDetailArray.addAll(getUri(getImageProjection(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

        Collections.sort(mediaDetailArray) { o1, o2 ->
            o2["date_added"]!!.compareTo(o1["date_added"]!!)
        }

        callback(mediaDetailArray)
    }

    private fun getUri(projection: Array<String>, uri: Uri): ArrayList<Map<String, String>> {
        val dataArrayList: ArrayList<Map<String, String>> = ArrayList()
        val cursor = mContext.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
        )

        if (cursor.count > 0) {
            if (cursor.moveToFirst()) {
                var id: String
                var dateAdded: String

                val dataId = cursor.getColumnIndex(
                        MediaStore.MediaColumns._ID)

                val dateAddedColumn = cursor.getColumnIndex(
                        MediaStore.MediaColumns.DATE_ADDED)

                do {
                    val dataMap = HashMap<String, String>()
                    when (uri) {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                            dataMap.put("type", "image")
                        }
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                            val pathColumn = cursor.getColumnIndex(
                                    MediaStore.Video.Media.DATA)
                            val path = cursor.getString(pathColumn)

                            val durationColumn = cursor.getColumnIndex(
                                    MediaStore.Video.VideoColumns.DURATION)
                            val duration = cursor.getString(durationColumn)

                            dataMap.put("type", "video")
                            dataMap.put("path", path)
                            dataMap.put("duration", duration)
                        }
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> {
                            val pathColumn = cursor.getColumnIndex(
                                    MediaStore.Audio.Media.DATA)
                            val path = cursor.getString(pathColumn)

                            val displayNameColumn = cursor.getColumnIndex(
                                    MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                            val displayName = cursor.getString(displayNameColumn)

                            dataMap.put("type", "audio")
                            dataMap.put("path", path)
                            dataMap.put("display_name", displayName)
                        }

                    }
                    id = cursor.getString(dataId)
                    dateAdded = cursor.getString(dateAddedColumn)

                    dataMap.put("uri", Uri.withAppendedPath(uri, id).toString())
                    dataMap.put("id", id)
                    dataMap.put("date_added", dateAdded)
                    dataArrayList.add(dataMap)

                } while (cursor.moveToNext())
            }

            cursor.close()
        }
        return dataArrayList
    }

    private fun getImageProjection(): Array<String> {
        return arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATE_ADDED
        )
    }

    private fun getVideoProjection(): Array<String> {
        return arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.VideoColumns.DURATION
        )
    }

    private fun getAudioProjection(): Array<String> {
        return arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME
        )
    }

    private fun musicFilter(filePath: String): Boolean {
        var re = false
        val ext = arrayOf(".mp3", ".awv")
        val file = File(filePath)
        val fileName = file.name
        for (element in ext) {
            if (fileName.endsWith(element)) {
                re = true
                break
            }
        }
        return re
    }
}