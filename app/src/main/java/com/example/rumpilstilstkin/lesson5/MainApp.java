package com.example.rumpilstilstkin.lesson5;


import android.app.Application;


public class MainApp extends Application {

    private static AppComponent component;


    private static AppComponentSingleton singletonComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent
                .builder()
                .daggerNetModule(new DaggerNetModule(this))
                .build();

        singletonComponent = DaggerAppComponentSingleton
                .builder()
                .appModule(new AppModule(this))
                .build();

    }


    public static AppComponent getComponent() {
        return component;
    }

    public static AppComponentSingleton getComponentSingleton() {
        return singletonComponent;
    }
}

