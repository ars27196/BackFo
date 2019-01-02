package project.example.com.bacfocamera

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.bumptech.glide.Glide.init

class AlertDialogBox :DialogFragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }
    fun createBuilder( context: Context
                       ,errorTitle:String,errorMessage:String,positiveMessage:String) {


        val builder = AlertDialog.Builder(context)
        builder.setTitle(errorTitle)
        builder.setMessage(errorMessage)
        builder.setPositiveButton(positiveMessage, DialogInterface.OnClickListener { dialogInterface, i ->

        }

        )

        builder.create().show()

    }
}