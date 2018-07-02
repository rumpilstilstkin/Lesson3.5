package com.example.rumpilstilstkin.lesson5;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
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

    AppComponent appComponent;

    @Inject
    Call<List<Model>> call;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appComponent = MainApp.getComponent();
        appComponent.injectsToMainActivity(this);

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
                mInfoTextView.append("количество = " + bundle.getInt("count") +
                                     "\n милисекунд = " + bundle.getLong("msek"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                mInfoTextView.setText("");
               /* Retrofit retrofit = null;
                try {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/") // - обратить внимание на слэш в базовом адресе
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    restAPI = retrofit.create(Endpoints.class);
                }
                catch (Exception io) {
                    mInfoTextView.setText("no retrofit: " + io.getMessage());
                    return;
                }
                // подготовили вызов на сервер
                Call<List<Model>> call = restAPI.loadUsers();*/
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

                if (networkinfo != null && networkinfo.isConnected()) {
                    // запускаем
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        downloadOneUrl(call);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        mInfoTextView.setText(e.getMessage());
                    }
                }
                else {
                    Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSaveAllSugar:
                Single<Bundle> singleSaveAll = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            String curLogin = "";
                            String curUserID = "";
                            String curAvatarUrl = "";
                            Date first = new Date();
                            for (Model curItem : modelList) {
                                curLogin = curItem.getLogin();
                                curUserID = curItem.getUserId();
                                curAvatarUrl = curItem.getAvatar();
                                SugarModel sugarModel = new SugarModel(curLogin, curUserID, curAvatarUrl);
                                sugarModel.save();
                            }
                            Date second = new Date();
                            List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

                singleSaveAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllSugar:
                Single<Bundle> singleSelectAll = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            Date first = new Date();
                            List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllSugar:
                Single<Bundle> singleDeleteAll = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            Date first = new Date();
                            int count = SugarModel.deleteAll(SugarModel.class);
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", count);
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAll.subscribeWith(CreateObserver());
                break;

            case R.id.btnSaveAllRealm:
                Single<Bundle> singleSaveAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            String curLogin = "";
                            String curUserID = "";
                            String curAvatarUrl = "";
                            realm = Realm.getDefaultInstance();
                            Date first = new Date();

                            for (Model curItem : modelList) {
                                curLogin = curItem.getLogin();
                                curUserID = curItem.getUserId();
                                curAvatarUrl = curItem.getAvatar();
                                try {
                                    realm.beginTransaction();
                                    RealmModel realmModel = realm.createObject(RealmModel.class);
                                    realmModel.setUserID(curUserID);
                                    realmModel.setLogin(curLogin);
                                    realmModel.setAvatarUrl(curAvatarUrl);
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
                            bundle.putInt("count", (int)count);
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                            realm.close();
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllRealm:
                Single<Bundle> singleSelectAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            realm = Realm.getDefaultInstance();
                            Date first = new Date();
                            RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                            realm.close();
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllRealm:
                Single<Bundle> singleDeleteAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {

                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            realm = Realm.getDefaultInstance();
                            final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                            Date first = new Date();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    tempList.deleteAllFromRealm();
                                }
                            });
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                            realm.close();
                        }
                        catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAllRealm.subscribeWith(CreateObserver());
                break;
        }
    }

    private void downloadOneUrl(Call<List<Model>> call) throws IOException {
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
