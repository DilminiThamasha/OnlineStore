package com.crude.onlinestore.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crude.onlinestore.R
import com.crude.onlinestore.model.User
import com.crude.onlinestore.utilities.Tool
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var user = User()
    var signInUser: User? = null
    var firstName: EditText? = null
    var lastName: EditText? = null
    var address: EditText? = null
    var mobileNo: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var profilePhoto: ImageView? = null
    var register: Button?=null


    private val PICKIMAGEREQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        signInUser = Tool.get_signInUser()
        if (signInUser != null){
            Toast.makeText(this,"You Are already logged",Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        initComponent()
    }



    private fun initComponent(){
        user.customer_id = db.collection("CUSTOMER_TABLE").document().id
        firstName=findViewById<EditText>(R.id.f_name)
        lastName=findViewById<EditText>(R.id.l_name)
        address=findViewById<EditText>(R.id.address)
        mobileNo=findViewById<EditText>(R.id.mobile_no)
        email=findViewById<EditText>(R.id.email)
        password=findViewById<EditText>(R.id.password)
        profilePhoto=findViewById(R.id.profile_photo)
        register=findViewById(R.id.btn_reg)

        register?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
            validate() })

        profilePhoto?.setOnClickListener(View.OnClickListener { selectImage() })

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type ="image/*"
        intent.action =  Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent,"Select Image"),
            PICKIMAGEREQUEST
        )

    }

    private var imageurl: Uri? = null
    var storageRef: StorageReference? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICKIMAGEREQUEST && resultCode == RESULT_OK && data != null){
            imageurl = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageurl)
                    profilePhoto?.setImageBitmap(bitmap)


            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }


    private fun validate() {
        user.first_name = firstName!!.text.toString()
        if (user.first_name.isEmpty()) {
            Toast.makeText(this, "Please Enter Required Fields", Toast.LENGTH_SHORT).show()
        }


        user.last_name = lastName?.getText().toString()
        user.email = email?.getText().toString()
        user.password = password?.getText().toString()
        user.address = address?.getText().toString()
        user.mobile_no = mobileNo?.getText().toString()


        storageRef = FirebaseStorage.getInstance().reference
        storageRef!!.child("customers/" + user.customer_id).putFile(imageurl!!)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(this,
                        "Uploaded Successfully", Toast.LENGTH_SHORT).show()

                    storageRef!!.child("customers/" + user.customer_id).downloadUrl.addOnSuccessListener(
                        OnSuccessListener { uri ->
                            user.profile_photo = uri.toString()
                            sendData()
                            return@OnSuccessListener
                        }

                    ).addOnFailureListener(OnFailureListener {
                        user.profile_photo =
                            "https://www.bing.com/images/search?view=detailV2&ccid=xNnpJQdd&id=3E14CE2B6F9165F6675AFDCE676CE85FAF6DADAD&thid=OIP.xNnpJQddrN1KPDfRFjIvjAHaD4&mediaurl=https%3a%2f%2fm3f9w5j3.stackpathcdn.com%2fwp-content%2fuploads%2f404-Pages.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.c4d9e925075dacdd4a3c37d116322f8c%3frik%3dra1tr1%252fobGfO%252fQ%26pid%3dImgRaw%26r%3d0&exph=1008&expw=1920&q=404+not+found&simid=608007781331834974&FORM=IRPRST&ck=BC3DD12B3D30DB1F9F3F39592D06B4CF&selectedIndex=58&ajaxhist=0&ajaxserp=0"
                        sendData()
                        return@OnFailureListener

                    })
                }
            ).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(this, "failed to upload" + e.message, Toast.LENGTH_SHORT).show()
                Log.d("err", e.message.toString())
                user.profile_photo =
                    "https://www.bing.com/images/search?view=detailV2&ccid=xNnpJQdd&id=3E14CE2B6F9165F6675AFDCE676CE85FAF6DADAD&thid=OIP.xNnpJQddrN1KPDfRFjIvjAHaD4&mediaurl=https%3a%2f%2fm3f9w5j3.stackpathcdn.com%2fwp-content%2fuploads%2f404-Pages.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.c4d9e925075dacdd4a3c37d116322f8c%3frik%3dra1tr1%252fobGfO%252fQ%26pid%3dImgRaw%26r%3d0&exph=1008&expw=1920&q=404+not+found&simid=608007781331834974&FORM=IRPRST&ck=BC3DD12B3D30DB1F9F3F39592D06B4CF&selectedIndex=58&ajaxhist=0&ajaxserp=0"
                sendData()
                return@OnFailureListener

            })
    }

    private fun sendData() {
        db.collection("CUSTOMER_TABLE").whereEqualTo("email", user.email).get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots ->
                if (!queryDocumentSnapshots.isEmpty) {
                    Toast.makeText(this,
                        "Email already exist",
                        Toast.LENGTH_SHORT).show()
                    return@OnSuccessListener
                }
                user.customer_id = db.collection("CUSTOMER_TABLE").document().id
                db.collection("CUSTOMER_TABLE").document(user.customer_id)
                    .set(user)
                    .addOnSuccessListener(OnSuccessListener {
                        Toast.makeText(this,
                            "Account Created Successfully",
                            Toast.LENGTH_SHORT).show()

                        if (loggedUserToSql()) {
                            Toast.makeText(this, "login successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "login Failed", Toast.LENGTH_SHORT).show()
                        }


                    }).addOnFailureListener {
                        Toast.makeText(this,
                            "failed to create an account",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


            }).addOnFailureListener {
                Toast.makeText(this,
                    "failed to create an account",
                    Toast.LENGTH_SHORT).show()
            }


    }

          private  fun loggedUserToSql(): Boolean {
                 return try {
                     User.save(user)
                     true
                 } catch (e: Exception) {
                     false
                 }
          }
}



