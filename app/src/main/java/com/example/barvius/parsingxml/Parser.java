package com.example.barvius.parsingxml;

import android.util.Log;

import com.example.barvius.parsingxml.component.InstitutionEntity;
import com.example.barvius.parsingxml.component.LocationTagEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final static String LOG_TAG = "xmlParseLog";

    public static List<InstitutionEntity> parse(String file) {
        ArrayList<InstitutionEntity> list = new ArrayList<>();
        String tagText = "";
        InstitutionEntity institution = null;
        try {
            XmlPullParser xpp = prepareXpp(file);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(LOG_TAG, "START_DOCUMENT ");
                        break;
                    // начало тэга
                    case XmlPullParser.START_TAG:
                        institution = new InstitutionEntity();
                        if (xpp.getName().equals("institution")) {
                            institution.setKey(
                                    xpp.getAttributeValue(null, "key")
                            );
                        }
                        if (xpp.getName().equals("name")) {
                            if (xpp.getAttributeValue(null, "type") != null) {
                                institution.getNameTagEntity().setType(
                                        xpp.getAttributeValue(null, "type")
                                );
                            }
                        }
                        if (xpp.getName().equals("location")) {
                            LocationTagEntity locationTagEntity = institution.getLocationTagEntity();
                            locationTagEntity.setLocation(xpp.getAttributeValue(null, "country"));
                            if (xpp.getAttributeValue(null, "lat") != null) {
                                locationTagEntity.setLat(
                                        Double.parseDouble(
                                                xpp.getAttributeValue(null, "lat")
                                        )
                                );
                            }
                            if (xpp.getAttributeValue(null, "lon") != null) {
                                locationTagEntity.setLon(
                                        Double.parseDouble(
                                                xpp.getAttributeValue(null, "lon")
                                        )
                                );
                            }
                        }
                        if (xpp.getName().equals("id")) {
                            if (xpp.getAttributeValue(null, "base") != null) {
                                institution.getIdentifiersTagEntity().setBase(
                                        xpp.getAttributeValue(null, "base")
                                );
                            }
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if(institution != null) {
                            if (xpp.getName().equals("institution")) {
                                list.add(institution);
                            }
                            if (xpp.getName().equals("name")) {
                                if (institution.getNameTagEntity().getType() != null) {
                                    institution.getNameTagEntity().setLabel(tagText);
                                } else {
                                    institution.getNameTagEntity().setName(tagText);
                                }
                            }
                            if (xpp.getName().equals("label")) {
                                institution.getNameTagEntity().setLabel(tagText);
                            }
                            if (xpp.getName().equals("url")) {
                                institution.setUrl(tagText);
                            }
                            if (xpp.getName().equals("location")) {
                                String spl[] = tagText.split(",");
                                switch (spl.length) {
                                    case 2:
                                        institution.getLocationTagEntity().setCountry(spl[1].trim());
                                        institution.getLocationTagEntity().setCity(spl[0].trim());
                                        break;
                                    case 3:
                                        institution.getLocationTagEntity().setCountry(spl[2].trim());
                                        institution.getLocationTagEntity().setState(spl[1].trim());
                                        institution.getLocationTagEntity().setCity(spl[0].trim());
                                        break;
                                }
                            }
                            if (xpp.getName().equals("id")) {
                                institution.getIdentifiersTagEntity().setIdentifier(tagText);
                            }
                        }
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        tagText = xpp.getText();
                        break;

                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
            Log.d(LOG_TAG, "END_DOCUMENT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "SIZE = " + list.size());
        return list;
    }

    private static XmlPullParser prepareXpp(String file) {
        XmlPullParser xmlPullParser = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new FileInputStream(new File(file)), null);
            Log.d(LOG_TAG, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }
}
