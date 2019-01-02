package project.example.com.bacfocamera


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Range
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera2.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Arrays
import java.util.List

/*
 *
 * This is a demo activity to test the functionality of camera2 api and it image capturing
 * properties
 *
 * */
class Camera2Activity : AppCompatActivity() {

    private val TAG = "AndroidCameraApi"
    private var takePictureButton: Button? = null
    private var textureView: TextureView? = null
    private val ORIENTATIONS = SparseIntArray()





    private var cameraId: String? = null
    protected var cameraDevice: CameraDevice? = null
    protected var cameraCaptureSessions: CameraCaptureSession? = null
    protected var captureRequest: CaptureRequest? = null
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private val imageReader: ImageReader? = null
    private val file: File? = null
    private val REQUEST_CAMERA_PERMISSION = 200
    private val mFlashSupported: Boolean = false
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)

        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)

        textureView = findViewById<View>(R.id.tv2) as TextureView
        assert(textureView != null)
        textureView!!.surfaceTextureListener = textureListener

            photoClick.setOnClickListener{
        takePicture()
    }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun takePicture() {
        if(cameraDevice
        !=null){
            val manager=getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                val width = 640
                val height = 480

                val reader: ImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2)
                var outputSurface= ArrayList<Surface>()
                outputSurface?.add(reader.surface)
                outputSurface?.add(Surface(textureView?.surfaceTexture))
                val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureBuilder?.addTarget(reader.surface)
                captureBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                val rotation = getWindowManager().getDefaultDisplay().getRotation()
                captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                var output: Bitmap? = null

                val readerListener = object : ImageReader.OnImageAvailableListener {

                    override fun onImageAvailable(reader: ImageReader) {
                        var image: Image? = null
                        try {
                            image = reader.acquireLatestImage()
                            val buffer = image!!.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity())
                            buffer.get(bytes)
                            save(bytes)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            image?.close()
                        }
                    }


                    @Throws(IOException::class)
                    private fun save(bytes: ByteArray) {

                        try {
                          val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size);

                            AlertDialogBox().createBuilder(this@Camera2Activity, "Bitmap", ""+bmp.toString()+" " + bytes.size, "ok")


                        } catch (eE:Exception) {
                        }
                    }
                }

                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)

                val captureListener = object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                        createCameraPreview();

                    }

                    override fun onCaptureFailed(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        failure: CaptureFailure
                    ) {
                        super.onCaptureFailed(session, request, failure)
                        AlertDialogBox().createBuilder(this@Camera2Activity, "Bitmap", "failed" , "ok")
                    }
                }
                cameraDevice?.createCaptureSession(outputSurface, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(p0: CameraCaptureSession) {

                        AlertDialogBox().createBuilder(this@Camera2Activity, "Bitmap", "" + p0.toString(), "ok")

                    }

                    override fun onConfigured(session: CameraCaptureSession) {


                        try {
                            session.capture(captureBuilder?.build(), captureListener, mBackgroundHandler);
                        } catch (e: CameraAccessException) {
                            e.printStackTrace();
                        }
                    }

                }, mBackgroundHandler)
            }catch (E:Exception){
                AlertDialogBox().createBuilder(this@Camera2Activity,"ERROR", ""+E.message.toString(),"ok")


            }
        }
    }

    private val textureListener= object :SurfaceTextureListener{
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {

        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        return false
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
            openCamera()
        }
    }

    @SuppressLint("NewApi")
    private val stateCallback=object :CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera;
            createCameraPreview();

        }

        override fun onDisconnected(p0: CameraDevice) {
        }

        override fun onError(p0: CameraDevice, p1: Int) {
        }


    }

    private val captureCallbackListener= @SuppressLint("NewApi")
    object : CaptureCallback(){
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            createCameraPreview()
        }

        override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
            super.onCaptureFailed(session, request, failure)
        }
    }





    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
      private fun createCameraPreview() {

        val texture=textureView?.surfaceTexture
        texture?.setDefaultBufferSize(imageDimension!!.width,imageDimension!!.height)
        val surface=Surface(texture)
        captureRequestBuilder=cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)

        captureRequestBuilder?.addTarget(surface)

        cameraDevice?.createCaptureSession(Arrays.asList(surface),object :
            CameraCaptureSession.StateCallback(){
            override fun onConfigureFailed(p0: CameraCaptureSession) {

            }

            override fun onConfigured(p0: CameraCaptureSession) {
                cameraCaptureSessions=p0
                try {
//                   captureRequestBuilder?.set(
//                       CaptureRequest.COLOR_CORRECTION_MODE,
//                       CaptureRequest.COLOR_CORRECTION_MODE_FAST
//                   )
//                   captureRequestBuilder?.set(
//                       CaptureRequest.CONTROL_AF_MODE,
//                       CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
//                   );
//                   captureRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
//                   captureRequestBuilder?.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
//                   captureRequestBuilder?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(10,20))
                }catch (e:Exception){
                    Toast.makeText(this@Camera2Activity,""+e.message,Toast.LENGTH_LONG).show()

                }finally {
                    updatePreview()
                }

            }
        },null)

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updatePreview(){
        try{

            cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder?.build(),null,mBackgroundHandler)
        }catch (e:Exception){

        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val manager:CameraManager=getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId=manager.cameraIdList[0]
        val characteristics=manager.getCameraCharacteristics(cameraId)
        val map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
         if(map  !=null) {
             imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
         }
        manager.openCamera(cameraId,stateCallback,null)
    }

    private fun closeCamera(){
        if(cameraDevice!=null){
            cameraDevice?.close()

        }
    }
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView?.isAvailable()==true) {
            openCamera();
        } else {
            textureView?.setSurfaceTextureListener(textureListener);
        }
    }

    override fun onPause() {
        super.onPause()
        stopBackgroundThread()

    }
}
