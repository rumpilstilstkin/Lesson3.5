package com.example.rumpilstilstkin.lesson5;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponentSingleton {

    Context appContext();

    void inject(MainApp mainApp);
}