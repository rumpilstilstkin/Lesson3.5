package com.example.rumpilstilstkin.lesson5;


import java.util.List;

import dagger.Module;
import dagger.Provides;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class DaggerNetModule {
    @Provides
    Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    };
    @Provides
    Call<List<Model>> getCall(Retrofit retrofit){
        Endpoints restAPI = retrofit.create(Endpoints.class);
        return restAPI.loadUsers();
    }
}

