package com.example.rumpilstilstkin.lesson5;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context app;

    AppModule(Context app){
        this.app = app;
    }

    @Provides
    public Context provideAppContext(){
        return app;
    }
}
