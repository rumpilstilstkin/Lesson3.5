package com.example.rumpilstilstkin.lesson5;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context app;

    AppModule(Context app){
        this.app = app;
    }

    @Provides
    @Singleton
    public Context provideAppContext(){
        return app;
    }
}
