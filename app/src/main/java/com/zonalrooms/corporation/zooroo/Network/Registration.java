package com.zonalrooms.corporation.zooroo.Network;

import com.zonalrooms.corporation.zooroo.POJO.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by DELL on 5/6/2017.
 */

public interface Registration {
    @GET("/ZooRoo/signup.php")
    Call<Users> insertUser(@Query("name") String name, @Query("mobile") String mobile, @Query("email") String email,
                           @Query("password") String password, @Query("cnfmpassword") String cnfmpassword);
}
