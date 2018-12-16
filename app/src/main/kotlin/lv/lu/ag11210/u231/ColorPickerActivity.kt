package lv.lu.ag11210.u231

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_picker.*

class ColorPickerActivity : AppCompatActivity() {

    private var color: Int = Color.BLACK
        set(color) {
            colorIndicator.setBackgroundColor(color)
            rgbText.text = String.format("#%02x%02x%02x", Color.red(color), Color.green(color), Color.blue(color))
            rgbText.setTextColor(legibleTextColor(color))
            field = color
        }

    companion object {
        // https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
        private fun legibleTextColor(bgColor: Int): Int {
            val luminance = (.299 * Color.red(bgColor) + .587 * Color.green(bgColor) + .114 * Color.blue(bgColor)) / 255
            return if (luminance > .5) Color.BLACK else Color.WHITE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        color = InternalState.paint.color
        red.progress = Color.red(color)
        green.progress = Color.green(color)
        blue.progress = Color.blue(color)

        red.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    color = Color.rgb(progress, Color.green(color), Color.blue(color))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            thumb.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }

        green.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    color = Color.rgb(Color.red(color), progress, Color.blue(color))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            thumb.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }

        blue.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    color = Color.rgb(Color.red(color), Color.green(color), progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            progressDrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            thumb.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        }

        ok.setOnClickListener {
            InternalState.paint.color = color
            setResult(Activity.RESULT_OK)
            finish()
        }

        cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}