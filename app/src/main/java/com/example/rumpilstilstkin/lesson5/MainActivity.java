package com.example.rumpilstilstkin.lesson5;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    Button btnLoad;
    List<Model> modelList = new ArrayList<>();

    @Inject
    Endpoints endpoints;
    @Inject
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDI();
        initView();
    }

    private void initDI(){
        MainApp.getComponent().injectsToMainActivity(this);

        MainApp.getComponentSingleton().inject(this);
        Context context = MainApp.getComponentSingleton().appContext();
        Log.d("Dto", context.getPackageCodePath());
    }

    private void initView(){
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(this);
    }

    private void loadData() {
        mInfoTextView.setText("");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            downloadOneUrl(endpoints.loadUsers());
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                loadData();
                //startActivity(new Intent(this, Main2Activity.class));
                break;
        }
    }

    private void downloadOneUrl(Call<List<Model>> call) {
        call.enqueue(new Callback<List<Model>>() {

            @Override
            public void onResponse(
                    @NonNull Call<List<Model>> call,
                    @NonNull Response<List<Model>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    Model curModel;
                    mInfoTextView.append("\n Size = " + response.body().size() +
                                         "\n-----------------");
                    for (int i = 0; i < response.body().size(); i++) {
                        curModel = response.body().get(i);
                        modelList.add(curModel);
                        mInfoTextView.append(
                                "\nLogin = " + curModel.getLogin() +
                                "\nId = " + curModel.getUserId() +
                                "\nURI = " + curModel.getAvatar() +
                                "\n-----------------");
                    }
                }
                else {
                    String errorText = "onResponse error: " + response.code();
                    System.out.println(errorText);
                    mInfoTextView.setText(errorText);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Model>> call,
                    @NonNull Throwable t
            ) {
                String errorText = "onFailure " + t.getMessage();
                System.out.println(errorText);
                mInfoTextView.setText(errorText);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
