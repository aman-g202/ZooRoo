package com.zonalrooms.corporation.zooroo.Network;

import com.zonalrooms.corporation.zooroo.POJO.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by DELL on 5/6/2017.
 */

public interface Login {
    @GET("/ZooRoo/login.php")
    Call<Users> checkUser(@Query("mobile") String mobile, @Query("password") String password);
}
