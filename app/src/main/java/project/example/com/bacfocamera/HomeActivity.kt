package project.example.com.bacfocamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.PermissionChecker.PERMISSION_DENIED
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import kotlinx.android.synthetic.main.activity_home.*
import project.example.com.bacfocamera.alertDialog.AlertDialogBox
import project.example.com.bacfocamera.camera.CameraActivity
import project.example.com.bacfocamera.gallery.*
class HomeActivity : AppCompatActivity() {
    var camera=false
    var gallery=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cardView.setOnClickListener {
            camera=true
          if(getPermission()) {
              val intent = Intent(this@HomeActivity, CameraActivity::class.java)
              startActivity(intent)
          }
        }

        cardView2.setOnClickListener{
            camera=false
           if(getPermission()){

                val intent=Intent(this@HomeActivity, GalleryActivity::class.java)
            startActivity(intent)
               }else{

           }
        }
        cardView4.setOnClickListener{
            shareIt()
        }



        cardView5.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/dev?id=5779521440255184778")
            startActivity(intent)
        }
    }

    /*
    * This method is responsible for creating a share option in our application
    * */
    private fun shareIt() {

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=com.developine.bacfo.camera"
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    /*
    * This method is responsible for checking
     * if user have accepted run time permission
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==1){
            if(grantResults[0]==PERMISSION_DENIED && grantResults[1]== PERMISSION_DENIED){
                AlertDialogBox()
                    .createBuilder(this,"Permission","Camera and Storage permission denied","ok")
            }else if(grantResults[1]== PERMISSION_DENIED){
                AlertDialogBox()
                    .createBuilder(this,"Permission","Storage permission denied","ok")
            }else if(grantResults[0]== PERMISSION_DENIED){
                AlertDialogBox()
                    .createBuilder(this,"Permission","Camera permission denied","ok")
            }else if(grantResults[0]== PERMISSION_GRANTED && grantResults[1]== PERMISSION_GRANTED){
                if(camera){
                    val intent = Intent(this@HomeActivity, CameraActivity::class.java)
                    startActivity(intent)
                }else{
                    val intent=Intent(this@HomeActivity, GalleryActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    /*
    * This method is responsible is for asking for
    * runtime permission
    * */
    private fun getPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )&&
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                requestPermissions(listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(), 1)


            }
        }
        else{
            return true
        }

        return false

    }
}
