package com.example.rumpilstilstkin.lesson5;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        MainApp.getComponentSingleton().inject(this);
        Context context = MainApp.getComponentSingleton().appContext();
        Log.d("Dto", context.getPackageCodePath());
    }
}
