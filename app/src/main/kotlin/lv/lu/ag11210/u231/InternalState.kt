package lv.lu.ag11210.u231

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import java.util.*

object InternalState {

    val bitmap: Bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.WHITE)
    }
    val history = Stack<ByteArray>()
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        isAntiAlias = true
    }
}