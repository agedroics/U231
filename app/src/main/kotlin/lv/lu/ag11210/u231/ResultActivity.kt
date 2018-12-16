package lv.lu.ag11210.u231

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File
import java.io.FileOutputStream

const val PERMISSIONS_REQUEST = 1

class ResultActivity : AppCompatActivity() {

    private var resultBitmap = Bitmap.createBitmap(3200, 3200, Bitmap.Config.ARGB_8888)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        title = getString(R.string.result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val canvas = Canvas(resultBitmap)
        for (i in 0..3) {
            for (j in 0..3) {
                canvas.drawBitmap(InternalState.bitmap, i * 800f, j * 800f, null)
            }
        }
        imageView.setImageBitmap(resultBitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_result, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.save -> {
                if (ContextCompat.checkSelfPermission(this@ResultActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            this@ResultActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSIONS_REQUEST
                    )
                } else {
                    showSaveDialog()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this).apply {
            this.setMessage(R.string.file_name_prompt)

            val input = EditText(this@ResultActivity)
            val layout = FrameLayout(this@ResultActivity)
            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.marginStart = resources.getDimensionPixelSize(R.dimen.space)
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.space)
            input.layoutParams = params
            layout.addView(input)
            setView(layout)

            setPositiveButton(R.string.save) { _, _ ->
                val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), File.separator + "U231")
                path.mkdirs()
                val file = File(path, input.text.toString() + ".png")
                FileOutputStream(file).use {
                    resultBitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                MediaScannerConnection.scanFile(this@ResultActivity, arrayOf(file.toString()), null) {
                    filePath, _ -> this@ResultActivity.runOnUiThread {
                        Toast.makeText(this@ResultActivity, "Saglabāts $filePath", Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSaveDialog()
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}