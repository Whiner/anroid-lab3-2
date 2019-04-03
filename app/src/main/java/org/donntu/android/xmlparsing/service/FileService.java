package org.donntu.android.xmlparsing.service;

import android.content.Context;

import org.donntu.android.xmlparsing.utils.OpenFileDialog;

import java.util.function.Consumer;

public class FileService {
    public static void openFileDialog(Context context, Consumer<Object> listener) {
        OpenFileDialog openFileDialog = new OpenFileDialog(context);
        openFileDialog.setOpenDialogListener(listener::accept).show();
    }
}
