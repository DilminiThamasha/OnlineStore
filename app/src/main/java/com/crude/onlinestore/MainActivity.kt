package com.crude.onlinestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.crude.onlinestore.activities.LoginActivity
import com.crude.onlinestore.activities.RegisterActivity
import com.crude.onlinestore.model.User
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var login: Button? = null
    private var register: Button?=null
    private var logOut: Button?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init_component();
    }

    private fun init_component() {
        login = findViewById(R.id.button1)
        register=findViewById(R.id.button2)
        logOut = findViewById(R.id.button3)

        logOut?.setOnClickListener(View.OnClickListener {
            try{
                User.deleteAll(User::class.java)
                Toast.makeText(this,"You are logged Out",Toast.LENGTH_SHORT).show()

            }catch (e:Exception){
                Toast.makeText(this,"Failed to logOut",Toast.LENGTH_SHORT).show()
            }
        })

        login?.setOnClickListener (View.OnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            this.startActivity(intent)
        })
        register?.setOnClickListener (View.OnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            this.startActivity(intent)
        })
    }
}