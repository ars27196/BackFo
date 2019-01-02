package project.example.com.bacfocamera.gallery

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import project.example.com.bacfocamera.R


class GalleryAdapter(
    context: Context,
    urlList: ArrayList<String>,
    imagesName: ArrayList<String>,
    ionGalleryItemClicked: onGalleryItemClicked
) : RecyclerView.Adapter<myHolder>() {

    private var mUrlList: ArrayList<String>? = ArrayList()
    private var mImagesName: ArrayList<String>? = ArrayList()
    private var mImagesSize: List<Int>? = ArrayList()
    private var mContext: Context? = null
    private var onGalleryItemClicked: onGalleryItemClicked? = null

    init {
        mUrlList = urlList
        mUrlList!!.reversed()
        mContext = context
        mImagesName = imagesName
        mImagesName!!.reversed()
        onGalleryItemClicked = ionGalleryItemClicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): myHolder {
        val context = parent.getContext()
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.gallery_all_item, parent, false)
        return myHolder(photoView)
    }

    override fun getItemCount(): Int {
        return mUrlList?.size!!
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val url = mUrlList?.get(holder.adapterPosition)
        val name = mImagesName?.get(holder.adapterPosition)
        val imageView: ImageView? = holder.image
        val textView: TextView? = holder.name


        textView?.setText(name)


        Glide.with(mContext!!)
            .load(url)
            .into(imageView!!)



        holder.parent_layout?.setOnClickListener() {

            try {

                onGalleryItemClicked?.getImageInformation(position, mUrlList!!, mImagesName!!)

            } catch (e: Exception) {
                Toast.makeText(
                    mContext, "e: " +
                            e.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }


}

class myHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var image: ImageView? = null
    var name: TextView? = null
    var parent_layout: ConstraintLayout? = null

    init {

        image = itemView.findViewById(R.id.photo)
        name = itemView.findViewById(R.id.img_title)
        parent_layout = itemView.findViewById(R.id.parent_layout) as ConstraintLayout
    }


}
