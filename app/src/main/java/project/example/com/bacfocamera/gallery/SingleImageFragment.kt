package project.example.com.bacfocamera.gallery


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_single_image.*
import project.example.com.bacfocamera.AlertDialogBox
import project.example.com.bacfocamera.R
    import kotlin.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SingleImageFragment : Fragment(){
    private var  imgURLList:List<String> ?=null
    private var imgNameList: List<String> ?=null
    private  var imgPosition:Int?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_image, container, false)

        val bundle=arguments
        val toolbar=view.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val imageView=view.findViewById<ImageView>(R.id.imageFullScreenView)


        // Inflate the layout for this fragment




        this.imgNameList=bundle?.getStringArrayList("names")
        this.imgURLList=bundle?.getStringArrayList("urls")
        this.imgPosition=bundle?.getInt("position")

        try {
            toolbar.setTitle(imgNameList!![imgPosition!!])
            Glide.with(view.context)
                .load(imgURLList!![imgPosition!!])
                .into(imageView!!)

        }catch (e:Exception){
            AlertDialogBox().createBuilder(view.context, "Gallery", ""+e.stackTrace, "ok")
        }

        return view
    }

}
