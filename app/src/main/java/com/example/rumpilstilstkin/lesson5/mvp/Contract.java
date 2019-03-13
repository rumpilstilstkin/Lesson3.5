package com.example.rumpilstilstkin.lesson5.mvp;


interface Contract {
    interface View {
       void showUserName(String name);
       void showStatusString(String status);
    }

    interface Presenter{
        void showInfo();
    }
}
