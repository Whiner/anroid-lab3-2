package com.example.barvius.parsingxml;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barvius.parsingxml.component.InstitutionEntity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private DBHandler dbh = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readDb();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    Log.d("xmlParseLog", "File Uri: " + uri);
                    String path = getPath(this, uri);
                    Log.d("xmlParseLog", "File Path: " + path);
                    dbh.insert(Parser.parse(path));
                    readDb();
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    protected void readDb() {
        ArrayList<InstitutionEntity> arr = new ArrayList<>(dbh.select());
        ListAdapter listAdapter = new ListAdapter(this, arr);
        final ListView listView = findViewById(R.id.list_area);
        listView.setAdapter(listAdapter);
    }

    protected void loadDBFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Загрузить файл").setOnMenuItemClickListener(item -> {
            loadDBFromFile();
            return true;
        });
        menu.add(0, 2, 0, "Очистить базу").setOnMenuItemClickListener(item -> {
            dbh.truncate();
            readDb();
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }
}
