package com.crude.onlinestore.utilities

import com.crude.onlinestore.model.User

class Tool {

    companion object{
        fun get_signInUser(): User?
        {
            return try {
                val allCustomers: List<User> = User.listAll(User::class.java)
                    ?: return null

                if (allCustomers.isEmpty()){
                    null
                }else allCustomers[0]
            }catch (e:Exception){
                null
            }
        }
    }
}