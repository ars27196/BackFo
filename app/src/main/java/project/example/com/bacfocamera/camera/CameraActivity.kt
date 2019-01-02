package project.example.com.bacfocamera.camera


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.os.Environment.getExternalStoragePublicDirectory
import android.support.v7.app.AppCompatActivity
import android.support.annotation.RequiresApi
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import kotlinx.android.synthetic.main.activity_camera.*
import project.example.com.bacfocamera.alertDialog.AlertDialogBox
import project.example.com.bacfocamera.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraActivity : AppCompatActivity() {

    private val cameraManager by lazy {
        this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val ORIENTATIONS = SparseIntArray()

    private val MAX_HEIGHT1 = 480
    private val MAX_WIDTH1 = 720

    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundThread2: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundHandler2: Handler
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var captureSession: CameraCaptureSession

    private lateinit var cameraDevice2: CameraDevice
    private lateinit var captureRequestBuilder2: CaptureRequest.Builder
    private lateinit var captureSession2: CameraCaptureSession
    private lateinit var galleryFolder: File

    private var camera_1_IsOpen = false
    private var camera_2_IsOpen = false
    private var bitmapImageFirst: Bitmap? = null

    /*
    * This variable is responsible for connecting with camera 1 (Back Camera)
    * */
    private val deviceCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
        }

        override fun onDisconnected(p0: CameraDevice) {
            p0.close()
        }


        override fun onError(p0: CameraDevice, error: Int) {
            when (error) {
                ERROR_CAMERA_IN_USE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA IN USE",
                    "ok"
                )
                ERROR_MAX_CAMERAS_IN_USE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR MAX CAMERAS IN USE",
                    "ok"
                )
                ERROR_CAMERA_DISABLED -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA DISABLED",
                    "ok"
                )
                ERROR_CAMERA_DEVICE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA DEVICE",
                    "ok"
                )
                ERROR_CAMERA_SERVICE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA SERVICE",
                    "ok"
                )
            }

        }

    }

    /*
    * This function is responsible for starting background thread for camera 1(Back Camera)
    * */
    private fun startBackgroundTheard() {
        backgroundThread = HandlerThread("Camera").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)

    }

    /*
    * This function is responsible for stoping background thread for camera 1(Back Camera)
    * */
    private fun stopBackgroundTheard() {
        if (backgroundThread.isAlive) {
            backgroundThread.quitSafely()
        }
        try {
            backgroundThread.join()
        } catch (e: Exception) {
            AlertDialogBox()
                .createBuilder(this@CameraActivity, "thread Exception", "" + e.toString(), "ok")
        }
    }

    /*
     * This function is responsible for closing connection with camera 1(Back Camera)
     * */
    private fun closeCamera() {
        if (::captureSession.isInitialized) {
            captureSession.close()
            camera_1_IsOpen = false
        }


        if ((::cameraDevice.isInitialized)) {
            cameraDevice.close()
        }
    }

    /*
     * This function is responsible for getting camera characteristic  of camera 1(Back Camera)
     * */
    private fun <T> getCameraCharacteristics(cameraId: String, key: CameraCharacteristics.Key<T>): T {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        return when (key) {
            CameraCharacteristics.LENS_FACING -> characteristics.get(key)!!
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP -> characteristics.get(key)!!
            else -> throw IllegalAccessException("Key not Recognised")
        }


    }

    /*
     * This function is responsible for getting camera Id  of camera 1(Back Camera)
     * */
    private fun getCameraId(lens: Int): String {
        var deviceId = listOf<String>()
        try {
            val cameraIdList = cameraManager.cameraIdList
            deviceId = cameraIdList.filter { lens == getCameraCharacteristics(it, CameraCharacteristics.LENS_FACING) }
        } catch (e: CameraAccessException) {
            Log.e("camera", e.toString())
        }
        return deviceId[0]
    }


    /*
     * This function is responsible for making connection with camera 1(Back Camera)
     * */
    @SuppressLint("MissingPermission")
    private fun connectCamera(
        cameraFacingSide: Int,
        deviceCallback: CameraDevice.StateCallback,
        backgroundHandler: Handler
    ) {
        val deviceId = getCameraId(cameraFacingSide)

        if (cameraFacingSide == CameraCharacteristics.LENS_FACING_FRONT) {

            camera_2_IsOpen = true
            try {
                val characteristics = cameraManager.getCameraCharacteristics(deviceId)
                camera2Orentation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                cameraManager.openCamera(deviceId, deviceCallback2, backgroundHandler2)
            } catch (e: CameraAccessException) {
                Log.e("camera", e.toString())
            } catch (e: InterruptedException) {
                Log.e("camera", e.toString())
            }

        } else {

            try {
                cameraManager.openCamera(deviceId, deviceCallback, backgroundHandler)
//            Toast.makeText(this, " " + deviceId, Toast.LENGTH_LONG).show()
                camera_1_IsOpen = true
            } catch (e: CameraAccessException) {
                Log.e("camera", e.toString())
            } catch (e: InterruptedException) {
                Log.e("camera", e.toString())
            }
        }
    }


    /*
      * This variable is responsible for checking availability of Texture Surface for camera 1(Back Camera)
      * */
    private val surfaceListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {

        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            return isDestroyed
        }

        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, MAX_WIDTH: Int, MAX_HEIGHT: Int) {


            surfaceTexture?.setDefaultBufferSize(MAX_WIDTH1, MAX_HEIGHT1)
            val surface = Surface(surfaceTexture!!)

            if (!camera_1_IsOpen) {
                startBackgroundTheard()
                connectCamera(CameraCharacteristics.LENS_FACING_BACK, deviceCallback, backgroundHandler)
            }
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    AlertDialogBox().createBuilder(
                        this@CameraActivity,
                        "Camera Error",
                        "Camera session could not be configured",
                        "ok"
                    )

                }

                @RequiresApi(Build.VERSION_CODES.P)
                override fun onConfigured(session: CameraCaptureSession) {


                    captureSession = session
                    captureRequestBuilder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                    captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)

                }

            }, null)
        }
    }

    /*
     * This method is responsible for creating  an Image file
     *
     * */
    private fun createImageFile(imageFile: File): File {
        val timeStamp: String = object : SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) {}.format(Date())

        val imageFileName = "image_" + timeStamp + "_"

        return File.createTempFile(imageFileName, ".jpg", imageFile)
    }


    /*
     * This method is responsible for creating  an directory for Image file
     *
     * */
    private fun createImageDirectory(): Boolean {
        val storage: File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        galleryFolder = object : File(storage, resources.getString(R.string.app_name)) {}
        if (galleryFolder.exists()) {
            return true
        } else {
            val wasCreated: Boolean = galleryFolder.mkdirs()
            return if (wasCreated) {
                true
            } else {
                AlertDialogBox()
                    .createBuilder(this, "Storage Error", "Directory Making Failed", "ok")
                return false

            }
        }

    }


    /*
    * <b>Methods and variables to control second camera 2 (front camera) </b>
    *
    * */

    private var camera2Orentation: Int? = null


    /*
     * This function is responsible for making connection with camera 2 (Front Camera)
     * */

    @SuppressLint("MissingPermission")
    /*   private fun connectCamera2() {
           val deviceId = getCameraId(CameraCharacteristics.LENS_FACING_FRONT)
   //        camera2Orentation= deviceId.get(CameraCharacteristics.SENSOR_ORIENTATION as Int )
           camera_2_IsOpen = true

           try {
               val characteristics = cameraManager.getCameraCharacteristics(deviceId)
               camera2Orentation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
               cameraManager.openCamera(deviceId, deviceCallback2, backgroundHandler2)
           } catch (e: CameraAccessException) {
               Log.e("camera", e.toString())
           } catch (e: InterruptedException) {
               Log.e("camera", e.toString())
           }

       }*/


    /*
     * This variable is responsible for connecting with camera 2 (Front Camera)
     * */

    private val deviceCallback2 = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice2 = camera


            textureView2.surfaceTexture.setDefaultBufferSize(MAX_WIDTH1, MAX_HEIGHT1)

            val surface = Surface(textureView2.surfaceTexture)


            captureRequestBuilder2 = cameraDevice2.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder2.addTarget(surface)
            cameraDevice2.createCaptureSession(
                Arrays.asList(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(p0: CameraCaptureSession) {

                        AlertDialogBox().createBuilder(
                            this@CameraActivity,
                            "Camera Error",
                            "Camera failed to configured",
                            "ok"
                        )
                    }


                    override fun onConfigured(session: CameraCaptureSession) {

                        captureSession2 = session

                        captureRequestBuilder2.set(
                            CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                            CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
                        )
                        captureSession2.setRepeatingRequest(captureRequestBuilder2.build(), null, null)

                        Handler().postDelayed({
                            takePictureFromCamera(textureView2.surfaceTexture,1,backgroundHandler2,cameraDevice2)
                        }
                            , 1000)

                    }

                },
                null
            )
        }

        override fun onDisconnected(p0: CameraDevice) {
            closeCamera2()
        }

        override fun onError(p0: CameraDevice, error: Int) {
            when (error) {
                ERROR_CAMERA_IN_USE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA IN USE",
                    "ok"
                )
                ERROR_MAX_CAMERAS_IN_USE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR MAX CAMERAS IN USE",
                    "ok"
                )
                ERROR_CAMERA_DISABLED -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA DISABLED",
                    "ok"
                )
                ERROR_CAMERA_DEVICE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA DEVICE",
                    "ok"
                )
                ERROR_CAMERA_SERVICE -> AlertDialogBox().createBuilder(
                    this@CameraActivity,
                    "Camera Error",
                    "ERROR CAMERA SERVICE",
                    "ok"
                )
            }

        }

    }


    /*
     * This function is responsible for starting new thread for camera 2 (Front Camera)
     * */
    private fun startBackgroundTheard2() {
        backgroundThread2 = HandlerThread("Camera2").also { it.start() }
        backgroundHandler2 = Handler(backgroundThread2.looper)

    }

    /*
         * This function is responsible for stoping new thread for camera 2 (Front Camera)
         * */
    private fun stopBackgroundTheard2() {
        if (backgroundThread2.isAlive) {
            backgroundThread2.quitSafely()
        }
        try {

            backgroundThread2.join()
        } catch (e: InterruptedException) {

        }
    }

    /*
         * This function is responsible for closing the camera connection with camera 2 (Front Camera)
         * */
    private fun closeCamera2() {
        if (::captureSession2.isInitialized) {
            captureSession2.close()
        }

        if (::cameraDevice2.isInitialized) {
            camera_2_IsOpen = false
            cameraDevice2.close()
        }
    }


    /*
     * Functions to manage activity lifecycle for smooth running of application
     * */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        progressBar.visibility = View.GONE

        camera_2_IsOpen = false
        camera_1_IsOpen = false

        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)


        if (textureView1.isAvailable) {
        } else {
            textureView1.surfaceTextureListener = surfaceListener
        }


/*
*
* Action button to capture image from camera 1 (Back Camera)
*
* */


        floatingActionButton.setOnClickListener {
            try {
                takePictureFromCamera(textureView1.surfaceTexture,0,backgroundHandler,cameraDevice)
            } catch (e: Exception) {
                AlertDialogBox()
                    .createBuilder(this@CameraActivity, "Error", "" + e.message, "okk")

            }
        }

    }


    override fun onResume() {

        super.onResume()

        if (textureView1.isAvailable) {

        } else {


            if (!camera_1_IsOpen) {
                startBackgroundTheard()
                connectCamera(CameraCharacteristics.LENS_FACING_BACK, deviceCallback, backgroundHandler)

                textureView1.surfaceTextureListener = (surfaceListener)
            } else {
                textureView1.surfaceTextureListener = (surfaceListener)

            }


        }
    }

    override fun onPause() {
        super.onPause()
        if (camera_1_IsOpen) {
            closeCamera()
            stopBackgroundTheard()
        }

        if (camera_2_IsOpen) {
            closeCamera2()
            stopBackgroundTheard2()
        }
    }

    override fun onStop() {
        super.onStop()
        if (camera_1_IsOpen) {
            closeCamera()
            stopBackgroundTheard()
        }

        if (camera_2_IsOpen) {
            closeCamera2()
            stopBackgroundTheard2()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        if (camera_1_IsOpen) {
            closeCamera()
            stopBackgroundTheard()
        }

        if (camera_2_IsOpen) {
            closeCamera2()
            stopBackgroundTheard2()
        }
    }


    /*
    * This method is responsible for capturing image from back camera (Camera 1)
    * */

    private fun takePictureFromCamera(textureView: SurfaceTexture, cameraId:Int,backgroundHandler:Handler,cameraDevice: CameraDevice) {
        try {
            val reader: ImageReader = ImageReader.newInstance(MAX_WIDTH1, MAX_HEIGHT1, ImageFormat.JPEG, 2)
            val outputSurface = java.util.ArrayList<Surface>()
            outputSurface.add(reader.surface)
            outputSurface.add(Surface(textureView))
            val captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(reader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))

            val readerListener = object : ImageReader.OnImageAvailableListener {

                override fun onImageAvailable(reader: ImageReader) {

                   if(cameraId==0){
                       if (textureView2.isAvailable) {
                           try
                           {
                               closeCamera()
                               startBackgroundTheard2()
                               runOnUiThread {
                                   progressBar.visibility = (View.VISIBLE)
                                   lens.visibility = (View.GONE)
                               }
                               connectCamera(CameraCharacteristics.LENS_FACING_FRONT, deviceCallback2, backgroundHandler2)
                           } catch (e: Exception) {
                               AlertDialogBox()
                                   .createBuilder(this@CameraActivity, "Capture", "" + e.toString(), "ok")
                           }
                       }
                       var image: Image? = null
                       try {
                           image = reader.acquireLatestImage()
                           val buffer = image!!.planes[0].buffer
                           val bytes = ByteArray(buffer.capacity())
                           buffer.get(bytes)


                           save(bytes,cameraId)
                       } catch (e: FileNotFoundException) {
                           e.printStackTrace()
                       } catch (e: IOException) {
                           e.printStackTrace()
                       } finally {
                           image?.close()
                       }
                   }else{

                       var image: Image? = null
                       try {
                           image = reader.acquireLatestImage()
                           val buffer = image!!.planes[0].buffer
                           val bytes = ByteArray(buffer.capacity())
                           buffer.get(bytes)
                           save(bytes,cameraId)
                       } catch (e: FileNotFoundException) {
                           e.printStackTrace()
                       } catch (e: IOException) {
                           e.printStackTrace()
                       } finally {
                           image?.close()
                       }
                   }

                   }



                }


            reader.setOnImageAvailableListener(readerListener, backgroundHandler)

            val captureListener = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureFailed(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    failure: CaptureFailure
                ) {
                    super.onCaptureFailed(session, request, failure)
                    AlertDialogBox()
                        .createBuilder(this@CameraActivity, "Bitmap", "failed", "ok")
                }
            }

            cameraDevice.createCaptureSession(outputSurface, object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(p0: CameraCaptureSession) {

                    AlertDialogBox()
                        .createBuilder(this@CameraActivity, "Bitmap", "" + p0.toString(), "ok")

                }

                override fun onConfigured(session: CameraCaptureSession) {


                    try {
                        session.capture(captureBuilder.build(), captureListener, backgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

            }, backgroundHandler)
        } catch (E: java.lang.Exception) {
            AlertDialogBox()
                .createBuilder(this@CameraActivity, "ERROR on Image Capturing", "" + E.message.toString(), "ok")


        }

    }

    private fun save(bytes: ByteArray, cameraId: Int) {
        if(cameraId==0){
            try {
                bitmapImageFirst = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (eE: java.lang.Exception) {
            }
        }else{
            try {

                val bitmapOverlay = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


                try {
                    combineImage(bitmapImageFirst!!, bitmapOverlay!!.rotate(180F))
                } catch (E: Exception) {
                    AlertDialogBox().createBuilder(
                        this@CameraActivity,
                        "Combine Bitmap",
                        "error: " + E.message,
                        "ok"
                    )
                }
            } catch (eE: java.lang.Exception) {
            }
        }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


    /*
   * This method is responsible for combining image from back camera and front camera
   * */

    private fun combineImage(bitmap1: Bitmap, overlayBitmap: Bitmap?) {

        createImageDirectory()
        val bitmap: ArrayList<Bitmap?> = arrayListOf()
        bitmap.add(bitmap1)
        bitmap.add(overlayBitmap)

        var w = 0
        var h = 0
        for (i in 0 until bitmap.size) {
            if (i < bitmap.size - 1) {
                w =
                        if (bitmap[i]?.width!! > bitmap[i + 1]?.width!!) bitmap[i]?.width!! else bitmap[
                                i + 1
                        ]?.width!!
            }
            h += bitmap[i]?.height!!
        }

        val temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(temp)
        var top: Float = (0).toFloat()

        for (i in 0 until bitmap.size) {
            Log.d("HTML", "Combine: " + i + "/" + bitmap.size + 1)

            top = (if (i == 0) (0).toFloat() else {
                top + bitmap[i]?.height?.toFloat()!!
            })
            canvas.drawBitmap(bitmap[i]!!, 0f, top, null)
        }
        runOnUiThread { progressBar.visibility = View.GONE }
        val outputPhoto3 = object : FileOutputStream(createImageFile(galleryFolder)) {}
        temp.compress(Bitmap.CompressFormat.PNG, 100, outputPhoto3)

    }


}
