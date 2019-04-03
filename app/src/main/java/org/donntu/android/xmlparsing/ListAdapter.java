package org.donntu.android.xmlparsing;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.donntu.android.xmlparsing.R;

import org.donntu.android.xmlparsing.component.InstitutionEntity;
import org.donntu.android.xmlparsing.component.LocationTagEntity;
import org.donntu.android.xmlparsing.component.NameTagEntity;

import java.util.List;

public class ListAdapter extends ArrayAdapter<InstitutionEntity> {
    public ListAdapter(Context context, List<InstitutionEntity> data) {
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
            name.setTextColor(Color.BLACK);
            parentLayout.addView(name);

            if (nameTagEntity.getLabel() != null) {
                TextView labelTextView = new TextView(parent.getContext());
                labelTextView.setText(nameTagEntity.getLabel());
                parentLayout.addView(labelTextView);
            }

            LocationTagEntity locationTagEntity = entity.getLocationTagEntity();

            if (locationTagEntity.getCity() != null && locationTagEntity.getCountry() != null) {
                TextView locationTextView = new TextView(parent.getContext());
                String locationText = "Location: ";
                if (locationTagEntity.getState() == null) {
                    locationText = locationText
                            + locationTagEntity.getCountry() + ", "
                            + locationTagEntity.getCity();
                } else {
                    locationText = locationText
                            + locationTagEntity.getCountry()
                            + ", " + locationTagEntity.getState()
                            + ", " + locationTagEntity.getCity();
                }
                locationTextView.setText(locationText);
                parentLayout.addView(locationTextView);
            }

            if (entity.getUrl() != null) {
                TextView urlTextView = new TextView(parent.getContext());
                String text = "URL: " + entity.getUrl();
                urlTextView.setText(text);
                urlTextView.setTextColor(Color.BLUE);
                parentLayout.addView(urlTextView);
            }
        }
        return convertView;
    }
}
