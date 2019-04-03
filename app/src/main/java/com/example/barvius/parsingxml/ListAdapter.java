package com.example.barvius.parsingxml;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.barvius.parsingxml.component.InstitutionEntity;
import com.example.barvius.parsingxml.component.LocationTagEntity;
import com.example.barvius.parsingxml.component.NameTagEntity;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<InstitutionEntity> {
    public ListAdapter(Context context, ArrayList<InstitutionEntity> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        InstitutionEntity entity = getItem(position);

        if (entity != null) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            LinearLayout parentLayout = convertView.findViewById(R.id.l_data);
            parentLayout.removeAllViews();

            TextView name = new TextView(parent.getContext());
            NameTagEntity nameTagEntity = entity.getNameTagEntity();
            name.setText(nameTagEntity.getName());
            parentLayout.addView(name);

            if (nameTagEntity.getLabel() != null) {
                TextView label = new TextView(parent.getContext());
                label.setText(nameTagEntity.getLabel());
                label.setTextColor(0xff81C784);
                parentLayout.addView(label);
            }
            LocationTagEntity locationTagEntity = entity.getLocationTagEntity();

            if (locationTagEntity.getCity() != null && locationTagEntity.getCountry() != null) {
                TextView location = new TextView(parent.getContext());
                location.setText(
                        locationTagEntity.getState() == null ?
                                locationTagEntity.getCountry() + ", " + locationTagEntity.getCity()
                                : locationTagEntity.getCountry()
                                + ", " + locationTagEntity.getState()
                                + ", " + locationTagEntity.getCity());
                location.setTextColor(0xff7E57C2);
                parentLayout.addView(location);
            }
            if (locationTagEntity.getLat() != 0 && locationTagEntity.getLon() != 0) {
                TextView loc = new TextView(parent.getContext());
                loc.setText("Показать на карте ->");
                loc.setTextColor(0xff2196F3);
                loc.setOnClickListener(v -> {
                    Uri gmmIntentUri = Uri.parse("geo:" + locationTagEntity.getLat() + "," + locationTagEntity.getLon());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        getContext().startActivity(mapIntent);
                    }
                });
                parentLayout.addView(loc);
            }
            if (entity.getUrl() != null) {
                TextView url = new TextView(parent.getContext());
                url.setText(entity.getUrl());
                url.setTextColor(0xff2196F3);
                parentLayout.addView(url);
            }
        }
        return convertView;
    }
}
