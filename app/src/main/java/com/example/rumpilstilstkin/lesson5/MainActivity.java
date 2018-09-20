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

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String EXT_TIME = "ext_time";
    private final static String EXT_COUNT = "ext_count";

    AppComponent appComponent;

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    Button btnLoad;
    Button btnSaveAllSugar;
    Button btnSelectAllSugar;
    Button btnDeleteAllSugar;
    Button btnSaveAllRealm;
    Button btnSelectAllRealm;
    Button btnDeleteAllRealm;

    Endpoints restAPI;
    List<Model> modelList = new ArrayList<>();

    DisposableSingleObserver<Bundle> dso;

    Realm realm;

    @Inject
    Call<List<Model>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appComponent = MainApp.getComponent();
        appComponent.injectsToMainActivity(this);

        Context context = MainApp.getComponentSingleton().appContext();
        Log.d("Dto", context.getPackageCodePath());

        mInfoTextView = (TextView) findViewById(R.id.tvLoad);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnSaveAllSugar = (Button) findViewById(R.id.btnSaveAllSugar);
        btnSelectAllSugar = (Button) findViewById(R.id.btnSelectAllSugar);
        btnDeleteAllSugar = (Button) findViewById(R.id.btnDeleteAllSugar);
        btnSaveAllRealm = (Button) findViewById(R.id.btnSaveAllRealm);
        btnSelectAllRealm = (Button) findViewById(R.id.btnSelectAllRealm);
        btnDeleteAllRealm = (Button) findViewById(R.id.btnDeleteAllRealm);
        btnLoad.setOnClickListener(this);
        btnSaveAllSugar.setOnClickListener(this);
        btnSelectAllSugar.setOnClickListener(this);
        btnDeleteAllSugar.setOnClickListener(this);
        btnSaveAllRealm.setOnClickListener(this);
        btnSelectAllRealm.setOnClickListener(this);
        btnDeleteAllRealm.setOnClickListener(this);

        SugarContext.init(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    private DisposableSingleObserver<Bundle> CreateObserver() {
        return new DisposableSingleObserver<Bundle>() {

            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }

            @Override
            public void onSuccess(@NonNull Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + bundle.getInt(EXT_COUNT) +
                                     "\n милисекунд = " + bundle.getLong(EXT_TIME));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }

    private void loadData(){
        mInfoTextView.setText("");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

        if (networkinfo != null && networkinfo.isConnected()) {
            // запускаем
            progressBar.setVisibility(View.VISIBLE);
            downloadOneUrl(call);
        }
        else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                loadData();
                break;
            case R.id.btnSaveAllSugar:
                Single<Bundle> singleSaveAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        Date first = new Date();
                        for (Model curItem : modelList) {
                            new SugarModel(
                                    curItem.getLogin(),
                                    curItem.getUserId(),
                                    curItem.getAvatar()
                            ).save();
                        }
                        Date second = new Date();
                        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, tempList.size());
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

                singleSaveAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllSugar:
                Single<Bundle> singleSelectAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        Date first = new Date();
                        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, tempList.size());
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllSugar:
                Single<Bundle> singleDeleteAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        Date first = new Date();
                        int count = SugarModel.deleteAll(SugarModel.class);
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, count);
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAll.subscribeWith(CreateObserver());
                break;

            case R.id.btnSaveAllRealm:
                Single<Bundle> singleSaveAllRealm = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        realm = Realm.getDefaultInstance();
                        Date first = new Date();

                        for (Model curItem : modelList) {
                            try {
                                realm.beginTransaction();
                                RealmModel realmModel = realm.createObject(RealmModel.class);
                                realmModel.setUserID(curItem.getUserId());
                                realmModel.setLogin(curItem.getLogin());
                                realmModel.setAvatarUrl(curItem.getAvatar());
                                realm.commitTransaction();
                            }
                            catch (Exception e) {
                                realm.cancelTransaction();
                                emitter.onError(e);
                            }
                        }
                        Date second = new Date();
                        long count = realm.where(RealmModel.class).count();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, (int) count);
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                        realm.close();
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllRealm:
                Single<Bundle> singleSelectAllRealm = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        realm = Realm.getDefaultInstance();
                        Date first = new Date();
                        RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, tempList.size());
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                        realm.close();
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllRealm:
                Single<Bundle> singleDeleteAllRealm = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        realm = Realm.getDefaultInstance();
                        final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                        Date first = new Date();
                        realm.executeTransaction(realm -> tempList.deleteAllFromRealm());
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXT_COUNT, tempList.size());
                        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                        realm.close();
                    }
                    catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAllRealm.subscribeWith(CreateObserver());
                break;
        }
    }

    private void downloadOneUrl(Call<List<Model>> call) {
        call.enqueue(new Callback<List<Model>>() {

            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        Model curModel = null;
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
                }
                else {
                    System.out.println("onResponse error: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                System.out.println("onFailure " + t);
                mInfoTextView.setText("onFailure " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
