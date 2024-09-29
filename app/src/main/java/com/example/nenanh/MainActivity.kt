package com.example.nenanh

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.nenanh.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var photoFile: File
    private lateinit var compressFile: File
    private lateinit var photoUri: Uri
    private val REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCameraPermission()
        checkStoragePermission()

//        val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//            if (success) {
//                binding.img.setImageURI(photoUri)
//                binding.tvDungLuong.text = "Dung lượng ảnh là : "+String.format("%.2f MB", compressImageAndReturnSize(photoFile))
//            } else {
//                Log.d("Camera", "Failed to capture image")
//            }
//        }
        // Đăng ký activity result để mở máy ảnh





        binding.btnChup.setOnClickListener {
            photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                photoFile
            )
            binding.img.setOnClickListener {
                showImageDialog(photoFile)
            }
            takePictureLauncher.launch(photoUri)
        }
        binding.btnNen.setOnClickListener {
            if(binding.edtQuality.text.toString().trim().equals("")){
                Toast.makeText(this,"Vui lòng nhập vào % nén",Toast.LENGTH_SHORT).show()
            }else{
                val quality = binding.edtQuality.text.toString().toInt()
                compressImage(photoFile,quality)
            }
            binding.imgCompress.setOnClickListener {
                showImageDialog(compressFile)
            }
        }
        binding.btnOpenFile.setOnClickListener {
            openFolder1()
        }
    }

    val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            // Nén ảnh với chất lượng 80
            val compressedImage = compressImage(it, 80)
        }
    }

    fun openCamera() {
        takePicturePreview.launch(null)
    }

    fun compressImage(image: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
    fun getSize(byteArray: ByteArray): Float {
        return byteArray.size / (1024f * 1024f)
    }

    private fun compressImageAndReturnSize(imageFile: File): Double? {
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val outputStream = ByteArrayOutputStream()

            // Nén hình ảnh với chất lượng 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val compressedFile = File(picturesDir, "original_${System.currentTimeMillis()}.jpg")

            try {
                val fos = FileOutputStream(compressedFile)
                fos.write(outputStream.toByteArray())
                fos.flush()
                fos.close()

                Log.d("Original Image", "Saved to: ${compressedFile.absolutePath}")

                // Gọi hàm để tính dung lượng của tệp tin nén
                return getFileSizeInMB(compressedFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null // Trả về null nếu tệp không tồn tại
    }
//    private fun compressImage(imageFile: File, quality : Int) {
//        binding.edtQuality.isEnabled = false
//        binding.btnNen.isClickable = false
//        if (imageFile.exists()) {
//            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
//            val outputStream = ByteArrayOutputStream()
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100-quality, outputStream)
//
//            val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            val compressedFile = File(picturesDir, "compressed_${System.currentTimeMillis()}.jpg")
//
//            try {
//                val fos = FileOutputStream(compressedFile)
//                fos.write(outputStream.toByteArray())
//                fos.flush()
//                fos.close()
//
//                Log.d("Compressed Image", "Saved to: ${compressedFile.absolutePath}")
//                MediaScannerConnection.scanFile(this, arrayOf(compressedFile.absolutePath), null, null)
//                compressFile = compressedFile
//                val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
//                binding.imgCompress.setImageBitmap(compressedBitmap)
//                binding.tvDungLuongCompress.setText("Ảnh sau khi nén có dung lượng: "+String.format("%.2f MB", getFileSizeInMB(compressedFile)))
//                binding.tvPath.setText("Saved to: ${compressedFile.absolutePath}\n Size sau khi nén: "+String.format("%.2f MB", getFileSizeInMB(compressedFile)))
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            Log.d("Image Compress", "Image compressed and saved to Pictures.")
//        }
//        binding.edtQuality.isEnabled = true
//        binding.btnNen.isClickable = true
//    }
    private fun openFolder1() {
        val location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        Log.d("open",location)
        val intent = Intent(Intent.ACTION_VIEW)
        val myDir: Uri = FileProvider.getUriForFile(applicationContext, applicationContext.applicationContext.packageName + ".provider", File(location))
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            intent.setDataAndType(myDir,  DocumentsContract.Document.MIME_TYPE_DIR)
        else  intent.setDataAndType(myDir,  "*/*")
        if (intent.resolveActivityInfo(applicationContext.packageManager, 0) != null)
        {
            this.startActivity(intent)
        }
        else
        {

        }
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }

    }
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 102)
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
    // Hàm tính dung lượng tệp ảnh
    private fun getFileSizeInMB(file: File): Double {
        val sizeInBytes = file.length()
        return sizeInBytes / (1024.0 * 1024.0)
    }
    private fun openImage(file: File) {
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/jpeg")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val chooser = Intent.createChooser(intent, "Open Image with")
        startActivity(chooser)
    }
    private fun showImageDialog(imageFile: File) {
        // Tạo Dialog mới
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_view)

        // Tìm ImageView và Button trong dialog
        val imageView: ImageView = dialog.findViewById(R.id.image_view)
        val closeButton: Button = dialog.findViewById(R.id.btnClose)

        // Chuyển File thành Bitmap và đặt vào ImageView
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        imageView.setImageBitmap(bitmap)

        // Xử lý sự kiện nhấn nút "Đóng"
        closeButton.setOnClickListener {
            dialog.dismiss() // Đóng dialog
        }

        // Hiển thị dialog
        dialog.show()
    }



}
