package com.example.barvius.parsingxml.service;

import android.content.Context;

import com.example.barvius.parsingxml.utils.OpenFileDialog;

import java.util.function.Consumer;

public class FileService {
    public static void openFileDialog(Context context, Consumer<Object> listener) {
        OpenFileDialog openFileDialog = new OpenFileDialog(context);
        openFileDialog.setOpenDialogListener(listener::accept).show();
    }
}
