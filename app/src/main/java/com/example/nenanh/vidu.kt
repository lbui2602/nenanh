//package com.example.nenanh
//
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.media.MediaScannerConnection
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.util.Log
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//class MainActivity1 : AppCompatActivity() {
//    lateinit var image1: ImageView
//    lateinit var image2: ImageView
//
//    @SuppressLint("MissingInflatedId", "ResourceType")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        image2 = findViewById(R.id.img2)
//        image1 = findViewById(R.id.img1)
//
//        // Kiểm tra và xin quyền ghi vào bộ nhớ (nếu cần)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    1
//                )
//            }
//        }
//        Log.d("size",getDrawableSizeInMB(this,R.drawable.anh).toString())
//        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.anh)
//
//        Log.d("size2",getSize(bitmap).toString())
//
//        val compressedImage = getCompressedImageFromDrawable(R.drawable.anh, 3)
//        compressedImage?.let {
//            saveImageToFile(it, "compressed_image1.jpg")
//            Log.d("end", (it.size).toString())
//        } ?: run {
//            Log.d("ImageSize", "Failed")
//        }
//    }
//    fun getDrawableSizeInMB(context: Context, drawableId: Int): Double {
//        val inputStream = context.resources.openRawResource(drawableId)
//        val sizeInBytes = inputStream.available().toLong()
//        inputStream.close()
//        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
//        return sizeInMB
//    }
//    fun getSize(bitmap: Bitmap) : Double {
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
//        return outputStream.toByteArray().size / (1024.0 * 1024.0)
//    }
//    // Nén dung lượng bitmap
//    fun compressImageUntilSize(bitmap: Bitmap, max: Int): ByteArray {
//        val maxSizeInBytes = max * 1024 * 1024
//        var quality = 100
//
//        var compressedImage: ByteArray
//        val outputStream = ByteArrayOutputStream()
//
//        do {
//            outputStream.reset()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
//            compressedImage = outputStream.toByteArray()
//            val size = compressedImage.size / (1024.0 * 1024.0)
//            Log.d("ImageSize", "Current size: ${"%.2f".format(size)} MB")
//            quality -= 10
//        } while (compressedImage.size > maxSizeInBytes && quality > 0)
//        return compressedImage
//    }
//
//    fun getCompressedImageFromDrawable(drawableResId: Int, maxSizeInMB: Int): ByteArray? {
//        val bitmap = BitmapFactory.decodeResource(resources, drawableResId)
//        return bitmap?.let {
//            compressImageUntilSize(it, maxSizeInMB) // nén ảnh
//        }
//    }
//
//    // Hàm để lưu ảnh nén vào bộ nhớ
//    fun saveImageToFile(imageData: ByteArray, fileName: String) {
//        // Kiểm tra trạng thái bộ nhớ ngoài
//        val externalStorageState = Environment.getExternalStorageState()
//        if (externalStorageState != Environment.MEDIA_MOUNTED) {
//            Log.d("FileSave", "External storage is not mounted.")
//            return
//        }
//
//        // Lấy đường dẫn tới thư mục Pictures
//        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val imageFile = File(storageDir, fileName)
//
//        try {
//            // Lưu tệp vào thư mục
//            val fos = FileOutputStream(imageFile)
//            fos.write(imageData)
//            fos.flush()
//            fos.close()
//            Log.d("FileSave", "Image saved to: ${imageFile.absolutePath}")
//
//            // Cập nhật Media Scanner
//            MediaScannerConnection.scanFile(this, arrayOf(imageFile.absolutePath), null, null)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.d("FileSave", "Failed to save image.")
//        }
//    }
//
//
//}
