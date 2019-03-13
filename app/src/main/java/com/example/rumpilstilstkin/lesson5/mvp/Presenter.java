package com.example.rumpilstilstkin.lesson5.mvp;


public class Presenter implements Contract.Presenter {
    private UserModel user;
    private Contract.View view;
    private UserResourseManager manager;

    Presenter(Contract.View view, UserModel model, UserResourseManager manager){
        this.user = model;
        this.view = view;
        this.manager = manager;
    }

    @Override
    public void showInfo() {
        view.showUserName(user.name);
        switch (user.status) {
            case 0: {
                view.showStatusString(manager.getBlockedUserStatus());
            }
            case 1: {
                view.showStatusString(manager.getNotIdentUserStatus());
            }
            case 2: {
                view.showStatusString(manager.getActiveUserStatus());
            }
        }
    }
}
