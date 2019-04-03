package com.example.barvius.parsingxml;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.barvius.parsingxml.component.IdentifiersTagEntity;
import com.example.barvius.parsingxml.component.InstitutionEntity;
import com.example.barvius.parsingxml.component.LocationTagEntity;
import com.example.barvius.parsingxml.component.NameTagEntity;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    public DBHandler(Context context) {
        super(context, "parsing_data", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=on");
        db.execSQL("CREATE TABLE Institution (" +
                "id INTEGER PRIMARY KEY," +
                "url TEXT," +
                "i_key TEXT" +
                ")");
        db.execSQL("CREATE TABLE Name (" +
                "id INTEGER PRIMARY KEY," +
                "institution_id INTEGER NOT NULL," +
                "name TEXT," +
                "label TEXT," +
                "type TEXT," +
                "FOREIGN KEY (institution_id) REFERENCES Institution(id) ON DELETE CASCADE" +
                ")");
        db.execSQL("CREATE TABLE Location (" +
                "id INTEGER PRIMARY KEY," +
                "institution_id INTEGER NOT NULL," +
                "location TEXT," +
                "country TEXT," +
                "state TEXT," +
                "city TEXT," +
                "lat DOUBLE," +
                "lon DOUBLE," +
                "FOREIGN KEY (institution_id) REFERENCES Institution(id)" +
                ")");
        db.execSQL("CREATE TABLE Identifiers (" +
                "id INTEGER PRIMARY KEY," +
                "institution_id INTEGER NOT NULL," +
                "identifier TEXT," +
                "base TEXT," +
                "FOREIGN KEY (institution_id) REFERENCES Institution(id)" +
                ")");
//        db.execSQL("CREATE INDEX institution_id on InstitutionEntity(id)");
//        db.execSQL("CREATE INDEX institution_id_name on Name(institution_id)");
//        db.execSQL("CREATE INDEX institution_id_location on Location(institution_id)");
//        db.execSQL("CREATE INDEX institution_id_identifiers on Identifiers(institution_id)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Institutio");
        db.execSQL("DROP TABLE IF EXISTS Name");
        db.execSQL("DROP TABLE IF EXISTS Location");
        db.execSQL("DROP TABLE IF EXISTS Identifiers");
        onCreate(db);
    }

    public void insert(List<InstitutionEntity> data) {
        Log.d("xmlParseLog", "insert start");
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        for (InstitutionEntity entity : data) {
            ContentValues institutionValues = new ContentValues();
            institutionValues.put("url", entity.getUrl());
            institutionValues.put("key", entity.getKey());
            entity.setId(db.insert("Institution", null, institutionValues));

            ContentValues nameValues = new ContentValues();
            NameTagEntity nameTagEntity = entity.getNameTagEntity();
            nameValues.put("name", nameTagEntity.getName());
            nameValues.put("label", nameTagEntity.getLabel());
            nameValues.put("type", nameTagEntity.getType());
            nameValues.put("institution_id", entity.getId());
            db.insert("Name", null, nameValues);

            ContentValues locationValues = new ContentValues();
            LocationTagEntity locationTagEntity = entity.getLocationTagEntity();
            locationValues.put("location", locationTagEntity.getLocation());
            locationValues.put("country", locationTagEntity.getCountry());
            locationValues.put("state", locationTagEntity.getState());
            locationValues.put("city", locationTagEntity.getCity());
            locationValues.put("lat", locationTagEntity.getLat());
            locationValues.put("lon", locationTagEntity.getLon());
            locationValues.put("institution_id", entity.getId());
            db.insert("Location", null, locationValues);

            ContentValues identifiersValues = new ContentValues();
            IdentifiersTagEntity identifiersTagEntity = entity.getIdentifiersTagEntity();
            identifiersValues.put("identifier", identifiersTagEntity.getIdentifier());
            identifiersValues.put("base", identifiersTagEntity.getBase());
            identifiersValues.put("institution_id", entity.getId());
            db.insert("Identifiers", null, identifiersValues);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
        Log.d("xmlParseLog", "insert end");
    }

    public List<InstitutionEntity> select() {
        Log.d("xmlParseLog", "select start");
        List<InstitutionEntity> list = new ArrayList<InstitutionEntity>();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT Institution.id, Institution.i_key, Institution.url, " +
                "Name.name, Name.label, Name.type," +
                "Location.location, Location.country, Location.state, Location.city, Location.lat, Location.lon," +
                "Identifiers.identifier, Identifiers.base " +
                "FROM Institution, Name, Location, Identifiers " +
                "WHERE Institution.id = Name.institution_id and Institution.id = Location.institution_id and Institution.id = Identifiers.institution_id " +
                "GROUP BY Institution.id ", null);


        if (cursor.moveToFirst()) {
            do {
                InstitutionEntity institution = new InstitutionEntity(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        new NameTagEntity(
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5)
                        ),
                        new LocationTagEntity(
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(8),
                                cursor.getString(9),
                                cursor.getDouble(10),
                                cursor.getDouble(11)
                        ),
                        new IdentifiersTagEntity(
                                cursor.getString(12),
                                cursor.getString(13)
                        )
                );

                list.add(institution);
            } while (cursor.moveToNext());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        cursor.close();
        db.close();
        Log.d("xmlParseLog", "select end");
        return list;
    }

    public void truncate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Institution");
        db.execSQL("delete from Name");
        db.execSQL("delete from Location");
        db.execSQL("delete from Identifiers");
        db.close();
    }
}
