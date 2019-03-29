package ru.nubby.notificatr.app.ui.base;

public interface MvpView<P extends MvpPresenter> {
    void setPresenter(P presenter);
}
