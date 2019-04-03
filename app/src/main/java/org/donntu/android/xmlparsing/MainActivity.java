package org.donntu.android.xmlparsing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.donntu.android.xmlparsing.R;

import org.donntu.android.xmlparsing.component.InstitutionEntity;
import org.donntu.android.xmlparsing.service.DatabaseService;
import org.donntu.android.xmlparsing.service.FileService;
import org.donntu.android.xmlparsing.service.XMLService;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DatabaseService databaseService = new DatabaseService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateListViewData();
    }

    protected void updateListViewData() {
        List<InstitutionEntity> arr = new LinkedList<>(databaseService.select());
        ListAdapter listAdapter = new ListAdapter(this, arr);
        final ListView listView = findViewById(R.id.list_area);
        listView.setAdapter(listAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Загрузить файл").setOnMenuItemClickListener(
                item -> {
                    FileService.openFileDialog(this, o -> {
                        Log.d("xmlParseLog", "File path: " + o);
                        databaseService.insert(XMLService.parse((String) o));
                        updateListViewData();
                    });
                    return true;
                });
        menu.add(0, 2, 0, "Очистить базу").setOnMenuItemClickListener(item -> {
            databaseService.truncate();
            updateListViewData();
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }
}
