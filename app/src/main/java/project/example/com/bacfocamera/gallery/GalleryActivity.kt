package project.example.com.bacfocamera.gallery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.support.v4.app.FragmentManager
import project.example.com.bacfocamera.R
import java.io.File


class GalleryActivity() : AppCompatActivity(), onGalleryItemClicked {

    var iOnGalleryClick: onGalleryItemClicked = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<View>(R.id.rv_images) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        val adapter = GalleryAdapter(
            this,
            getAllShownImagesPath(),
            getAllShownImagesName(),
            iOnGalleryClick
        )
        recyclerView.setAdapter(adapter)



    }


    /*
    * This method is responsible is for accessing paths of
    * all images in specific folder
    * */
    private fun getAllShownImagesPath(): ArrayList<String> {

        var filePath: ArrayList<String> = ArrayList<String>()
        val path = File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path, "BacFo Camera")
        if (path.exists()) {

            for (i in path.list().iterator()) {

                filePath?.add("" + path.toString() + "/" + i)
            }
        }

        return filePath!!
    }

    /*
  * This method is responsible is for accessing names of
  * all images in specific folder
  * */
    private fun getAllShownImagesName(): ArrayList<String> {

        var fileNames: ArrayList<String> = ArrayList<String>()
        val path = File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path, "BacFo Camera")
        if (path.exists()) {

            for (i in path.list().iterator()) {
                fileNames?.add(i)
            }
        }

        return fileNames!!
    }



    override fun getImageInformation(position: Int, pathList: ArrayList<String>, nameList: ArrayList<String>) {

        var intent = Intent(this@GalleryActivity, SingleImageActivity::class.java)
        intent.putStringArrayListExtra("urls", pathList)
        intent.putExtra("names", nameList)
        intent.putExtra("position", position)
        startActivity(intent)

    }

}
