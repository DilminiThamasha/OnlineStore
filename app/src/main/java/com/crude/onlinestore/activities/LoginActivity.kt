package com.crude.onlinestore.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.crude.onlinestore.R
import com.crude.onlinestore.model.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    var loginEmail: EditText? = null
    var loginPassword:EditText?= null
    var loginBtn:Button?=null
    var register: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initComponent()
    }

    private fun initComponent() {
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginBtn = findViewById(R.id.login_btn)
        register = findViewById(R.id.register)

        loginBtn?.setOnClickListener (View.OnClickListener { signIn()
        })
        register?.setOnClickListener(View.OnClickListener { val i = Intent(this,RegisterActivity::class.java)
            this.startActivity(i)
        })
    }
    var eValue = ""
    var pValue = ""

    var db = FirebaseFirestore.getInstance()
    private fun signIn() {
        eValue = loginEmail!!.getText().toString().trim{it <= ' '}
        pValue = loginPassword!!.getText().toString().trim{it <= ' '}

        if(eValue.isEmpty() || pValue.isEmpty()){
            Toast.makeText(this,"fill all required feilds",Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("CUSTOMER_TABLE").whereEqualTo("email",eValue)
            .get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots ->
               if (queryDocumentSnapshots == null){
                   Toast.makeText(this,"Email not found", Toast.LENGTH_LONG).show()
                   return@OnSuccessListener

                }

                if (queryDocumentSnapshots.isEmpty){
                    Toast.makeText(this,"Email not found", Toast.LENGTH_LONG).show()
                    return@OnSuccessListener
                }
                val customers = queryDocumentSnapshots.toObjects(
                    User::class.java
                )
                if(!customers[0].password.equals(pValue)){
                    Toast.makeText(this,"Wrong Password", Toast.LENGTH_LONG).show()
                    return@OnSuccessListener
                }
                if (loggedUserToSql(customers[0])){
                    Toast.makeText(this,"Your login successfully", Toast.LENGTH_LONG).show()
                    return@OnSuccessListener
                }else{
                    Toast.makeText(this,"Failed to login",Toast.LENGTH_LONG).show()

                }

            }).addOnFailureListener { e ->
                Toast.makeText(this, "failed to login " + e.message, Toast.LENGTH_LONG).show()

            }
    }

    private fun loggedUserToSql(c:User):Boolean{
        return try {
            User.save(c)
            true
        }catch (e:Exception){
            false
        }
    }
}