package me.hjhl.app.libs

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import java.io.File

/**
 * Screen shot event monitor
 *
 * @author lijiahui.me@gmail.com
 * */
class ScreenShotMonitor(private val mContext: Context) {
    private var mScreenShotListener = mutableSetOf<ScreenShotListener>()

    private var mContentObserver: ContentObserver? = null

    /**
     * start observe screen shot event
     * */
    fun startObserve() {
        Log.d(TAG, "startObserve")
        if (mContentObserver == null) {
            mContentObserver = mContext.contentResolver.createAndRegisterScreenShotObserver()
        }
    }

    /**
     * stop observe screen shot event
     * */
    fun stopObserve() {
        Log.d(TAG, "stopObserve")
        mContentObserver?.let { mContext.contentResolver.unregisterContentObserver(it) }
        mContentObserver = null
    }

    /**
     * register a screen shot listener, listener will received screen shot event when screen shot
     * and [mContentObserver] is not null
     *
     * @param listener screen shot listener
     * */
    fun registerListener(listener: ScreenShotListener): Boolean =
        mScreenShotListener.add(listener)

    fun unregisterListener(listener: ScreenShotListener): Boolean =
        mScreenShotListener.remove(listener)

    @SuppressLint("InlinedApi")
    private fun handleImageChangedUri(uri: Uri) {
        val sdkInt = Build.VERSION.SDK_INT
        try {
            if (sdkInt >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                }
                mContext.contentResolver.query(
                    uri,
                    PROJECTION_LIST.toTypedArray(),
                    queryArgs,
                    null
                )
            } else {
                mContext.contentResolver.query(
                    uri,
                    PROJECTION_LIST.toTypedArray(),
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT 1"
                )
            }?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (dataColumnIndex == -1) {
                        Log.d(TAG, "data column not exist")
                        if (sdkInt < Build.VERSION_CODES.Q) {
                            Log.e(TAG, "could not get image path")
                            notifyToAllObserver(null)
                        } else {
                            val relativePathColumnIndex =
                                cursor.getColumnIndex(MediaStore.Images.ImageColumns.RELATIVE_PATH)
                            val displayNameColumnIndex =
                                cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                            if (relativePathColumnIndex == -1 || displayNameColumnIndex == -1) {
                                Log.e(TAG, "could not get image relative path or display name")
                                notifyToAllObserver(null)
                            } else {
                                val relativePath = cursor.getString(relativePathColumnIndex)
                                val displayName = cursor.getString(displayNameColumnIndex)
                                val imagePath =
                                    "${Environment.getExternalStorageDirectory()?.absolutePath}${File.separator}$relativePath$displayName"
                                Log.d(TAG, "image path: $imagePath")
                                if (isScreenShotFile(imagePath)) {
                                    notifyToAllObserver(imagePath)
                                } else {
                                    Log.w(TAG, "not a screen shot file")
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "get image path from data column")
                        val imagePath = cursor.getString(dataColumnIndex)
                        if (isScreenShotFile(imagePath)) {
                            notifyToAllObserver(imagePath)
                        } else {
                            Log.w(TAG, "not a screen shot file")
                        }
                    }
                } else {
                    Log.w(TAG, "handleImageChangedUri: cursor is empty")
                    notifyToAllObserver(null)
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun notifyToAllObserver(path: String?) {
        Log.d(TAG, "notifyToAllObserver: $path")
        mScreenShotListener.forEach {
            it.onScreenShot(path)
        }
    }

    /**
     * Get screen dimension
     * */
    private fun getScreenDimension(context: Context): Size? {
        TODO()
    }

    /**
     * Create and register a content observer to monitoring image data change.
     * */
    private fun ContentResolver.createAndRegisterScreenShotObserver(): ContentObserver {
        val contentObserver = object : ContentObserver(
            Handler(
                Looper.getMainLooper()
            )
        ) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                Log.d(TAG, "received content changed! $uri")
                super.onChange(selfChange, uri)
                uri?.let {
                    handleImageChangedUri(uri)
                }
            }
        }
        registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver)
        return contentObserver
    }

    companion object {
        private const val TAG = "ScreenShotMonitor"

        private val SCREEN_SHOT_DIR_KEYWORDS = arrayOf(
            "screenshot", "screen_shot", "screen shot", "screen-shot",
            "screencapture", "screen_capture", "screen capture", "screen-capture",
            "screencap", "screen_cap", "screen cap", "screen-cap",
        )

        @SuppressLint("InlinedApi")
        private val PROJECTION_LIST = mutableListOf(
            MediaStore.Images.ImageColumns.DISPLAY_NAME,    // image file name
            MediaStore.Images.ImageColumns.DATE_ADDED,      // image first add time (to database)
        ).also {
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= Build.VERSION_CODES.Q) {
                it.add(MediaStore.Images.ImageColumns.RELATIVE_PATH)    // image relative path, WITHOUT sdcard root
            } else {
                it.add(MediaStore.Images.ImageColumns.DATA)             // image absolute path in file system, deprecated in API 29
            }
        }

        private fun isScreenShotFile(fileName: String): Boolean {
            return SCREEN_SHOT_DIR_KEYWORDS.any {
                fileName.contains(it, true)
            }
        }
    }
}