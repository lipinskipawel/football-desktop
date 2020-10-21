package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.UserDialogPresenter;

import java.awt.*;

final class NoopUserDialogPresenter implements UserDialogPresenter {

    @Override
    public Dialog showMessage(final Component parent, final String messagee) {
        return null;
    }
}
