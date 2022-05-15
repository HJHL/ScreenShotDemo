package me.hjhl.app.screenshottest

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import me.hjhl.app.libs.ScreenShotListener
import me.hjhl.app.libs.ScreenShotMonitor
import me.hjhl.app.screenshottest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!

    private val mScreenShotMonitor by lazy {
        ScreenShotMonitor(this)
    }

    private val mScreenShotListener = object : ScreenShotListener {
        override fun onScreenShot(path: String?) {
            mBinding.display.text = path
            // TODO: check path is valid
            Glide.with(mBinding.image).load(path).into(mBinding.image)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        initView(mBinding)
        setContentView(mBinding.root)
        // TODO: check permission granted or not
        requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_REQUEST_PERMISSIONS
        )
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        mScreenShotMonitor.startObserve()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        mScreenShotMonitor.stopObserve()
    }

    private fun initView(binding: ActivityMainBinding) {
        mScreenShotMonitor.registerListener(mScreenShotListener)
        binding.info.text = String.format(
            getString(R.string.info),
            Build.DEVICE,
            Build.MODEL,
            Build.VERSION.SDK_INT
        )
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        mScreenShotMonitor.unregisterListener(mScreenShotListener)
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity-ljh"

        private const val REQUEST_CODE_REQUEST_PERMISSIONS = 1
    }
}