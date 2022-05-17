package com.crude.onlinestore.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class User extends SugarRecord {

    public String first_name = "";
    public String last_name = "";
    public String address = "";
    public String mobile_no= "";
    public String email = "";
    public String password = "";
    public String profile_photo = "";

    @Unique
    public String customer_id = "";


}
