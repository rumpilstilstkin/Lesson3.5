package com.example.rumpilstilstkin.lesson5.mvp;


import android.content.res.Resources;

import com.example.rumpilstilstkin.lesson5.R;


public class ResManagerImpl implements UserResourseManager {
    Resources res;

    ResManagerImpl(Resources res){
        this.res = res;
    }

    @Override
    public String getActiveUserStatus() {
        return res.getString(R.string.app_name);
    }

    @Override
    public String getBlockedUserStatus() {
        return res.getString(R.string.app_name);
    }

    @Override
    public String getNotIdentUserStatus() {
        return res.getString(R.string.app_name);
    }
}
