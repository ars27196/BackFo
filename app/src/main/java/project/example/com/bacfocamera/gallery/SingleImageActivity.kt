package project.example.com.bacfocamera.gallery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import project.example.com.bacfocamera.R

class SingleImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_fragment_container)
        val intent=getIntent()
        val pathList=intent.getStringArrayListExtra("urls")
        val nameList=intent.getStringArrayListExtra("names")
        val position=intent.getIntExtra("position",0)

        val bundle = Bundle()
        bundle.putStringArrayList("urls", pathList)
        bundle.putStringArrayList("names", nameList)
        bundle.putInt("position", position)
        val fragment = SingleImageFragment()
        fragment.setArguments(bundle)
        supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.commit()
    }
}
