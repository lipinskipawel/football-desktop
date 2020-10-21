package com.github.lipinskipawel.gui;

import javax.swing.*;
import java.awt.*;

public final class DefaultUserDialogPresenter implements UserDialogPresenter {

    @Override
    public Dialog showMessage(final Component parent, final String message) {
        final var pane = new JOptionPane(message);
        final var dialog = pane.createDialog("Message");
        dialog.setVisible(true);
        return dialog;
    }
}
