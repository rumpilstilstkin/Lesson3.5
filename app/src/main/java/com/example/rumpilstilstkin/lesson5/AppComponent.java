package com.example.rumpilstilstkin.lesson5;


import dagger.Component;


@Component(modules = {DaggerNetModule.class})
public interface AppComponent {
    void injectsToMainActivity(MainActivity mainActivity);
}

