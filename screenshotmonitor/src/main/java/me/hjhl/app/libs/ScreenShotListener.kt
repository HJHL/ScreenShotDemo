package me.hjhl.app.libs

interface ScreenShotListener {
    /**
     * will be called when screenshot taken on main thread
     *
     * @param path path to screenshot image file
     *             null indicates that there maybe a screenshot event
     * */
    fun onScreenShot(path: String?)
}