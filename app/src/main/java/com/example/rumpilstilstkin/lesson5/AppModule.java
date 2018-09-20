package com.example.rumpilstilstkin.lesson5;


import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {
    private Application app;

    AppModule(Application app){
        this.app = app;
    }

    @Provides
    public Context provideAppContext(){
        return app;
    }
}
