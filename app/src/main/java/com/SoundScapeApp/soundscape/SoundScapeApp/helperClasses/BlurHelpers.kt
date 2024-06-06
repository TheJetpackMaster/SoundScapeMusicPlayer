package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurHelpers {

    fun blur(context: Context, uri: Uri, radius: Float): Bitmap {
        val drawable = uriToDrawable(context, uri)
        return blurBitmap(context, drawable, radius)
    }

    private fun uriToDrawable(context: Context, uri: Uri): Drawable {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapDrawable(context.resources, BitmapFactory.decodeStream(inputStream))
    }

    @Suppress("DEPRECATION")
    private fun blurBitmap(context: Context, drawable: Drawable, radius: Float): Bitmap {
        val bitmap = drawableToBitmap(drawable)
        val inputBitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width / 8, bitmap.height / 8, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs = RenderScript.create(context)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val allocationIn = Allocation.createFromBitmap(rs, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(rs, outputBitmap)

        script.setRadius(radius.coerceAtMost(25f)) // Limit radius to avoid crashes
        script.setInput(allocationIn)
        script.forEach(allocationOut)

        allocationOut.copyTo(outputBitmap)
        rs.destroy()

        return outputBitmap
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
