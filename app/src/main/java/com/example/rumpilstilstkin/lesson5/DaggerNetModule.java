package com.example.rumpilstilstkin.lesson5;


import android.content.Context;
import android.net.ConnectivityManager;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class DaggerNetModule {

    private Context context;

    public DaggerNetModule(Context context) {
        this.context = context;
    }

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

    @Provides
    ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private String getUrl(){
        ///мега сложная работа
        return "https://api.github.com/";
    }
}

