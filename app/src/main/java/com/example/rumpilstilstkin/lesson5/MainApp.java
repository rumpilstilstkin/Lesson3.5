package com.example.rumpilstilstkin.lesson5;


import android.app.Application;


public class MainApp extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder().daggerNetModule(new DaggerNetModule(this)).build();


    }

    public static AppComponent getComponent() {
        return component;
    }

}

