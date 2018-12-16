package com.example.rumpilstilstkin.lesson5;


import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class DaggerNetModule {
    @Provides
    Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    Endpoints getUserEndpoints(Retrofit retrofit){
        return retrofit.create(Endpoints.class);
    }

    private String getUrl(){
        ///мега сложная работа
        return "https://api.github.com/";
    }
}

