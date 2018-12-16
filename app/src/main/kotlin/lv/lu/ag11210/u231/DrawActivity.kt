package lv.lu.ag11210.u231

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream
import kotlin.math.roundToInt

const val COLOR_PICKER_REQUEST = 1

class DrawActivity : AppCompatActivity() {

    private val canvas = Canvas(InternalState.bitmap)
    private var prevSnapshotJob: Job? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            COLOR_PICKER_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    color.supportImageTintList = ColorStateList.valueOf(InternalState.paint.color)
                }
            }
        }
    }

    private fun saveSnapshot() {
        val buffer = ByteBuffer.allocate(2_560_000)
        InternalState.bitmap.copyPixelsToBuffer(buffer)
        prevSnapshotJob = GlobalScope.launch(Dispatchers.Default) {
            val deflater = DeflaterInputStream(ByteArrayInputStream(buffer.array()))
            InternalState.history.push(deflater.readBytes())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)
        setTitle(R.string.drawing)

        if (savedInstanceState == null) {
            saveSnapshot()
        }

        imageView.setImageBitmap(InternalState.bitmap)

        var prevPoint: Pair<Float, Float>? = null
        imageView.setOnTouchListener { _, event ->
            canvas.drawCircle(event.x, event.y, InternalState.paint.strokeWidth / 2, InternalState.paint)
            prevPoint?.let {
                if (event.action != MotionEvent.ACTION_DOWN) {
                    canvas.drawLine(it.first, it.second, event.x, event.y, InternalState.paint)
                }
            }
            prevPoint = Pair(event.x, event.y)
            imageView.invalidate()
            if (event.action == MotionEvent.ACTION_UP) {
                saveSnapshot()
                undoButton.isEnabled = true
            }
            true
        }

        if (InternalState.history.size == 1) {
            undoButton.isEnabled = false
        }
        undoButton.setOnClickListener {
            it.isEnabled = false
            GlobalScope.launch(Dispatchers.Main) {
                prevSnapshotJob?.join()
                InternalState.history.pop()
                val inflater = InflaterInputStream(ByteArrayInputStream(InternalState.history.peek()))
                InternalState.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(inflater.readBytes()))
                imageView.invalidate()
                if (InternalState.history.size > 1) {
                    undoButton.isEnabled = true
                }
            }
        }

        color.supportImageTintList = ColorStateList.valueOf(InternalState.paint.color)
        colorButton.setOnClickListener { _: View ->
            startActivityForResult(Intent(this, ColorPickerActivity::class.java), COLOR_PICKER_REQUEST)
        }

        strokeWidth.text = InternalState.paint.strokeWidth.roundToInt().toString()
        strokeWidthButton.setOnClickListener {
            val popupMenu = PopupMenu(this, strokeWidthLabel)
            popupMenu.inflate(R.menu.stroke_width)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                InternalState.paint.strokeWidth = menuItem.title.toString().toFloat()
                strokeWidth.text = menuItem.title
                true
            }
            popupMenu.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_draw, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> startActivity(Intent(this, ResultActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}