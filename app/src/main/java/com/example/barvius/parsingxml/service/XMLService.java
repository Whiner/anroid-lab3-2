package com.example.barvius.parsingxml.service;

import android.util.Log;

import com.example.barvius.parsingxml.component.InstitutionEntity;
import com.example.barvius.parsingxml.component.LocationTagEntity;
import com.example.barvius.parsingxml.component.NameTagEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

public class XMLService {
    private final static String XML_PARSE_LOG = "xml_parsing";

    public static List<InstitutionEntity> parse(String file) {
        List<InstitutionEntity> list = new LinkedList<>();
        String tagText = "";
        InstitutionEntity institution = null;
        long start = 0;
        long end;
        try {
            XmlPullParser xmlPullParser = getXmlPullParser(file);
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xmlPullParser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(XML_PARSE_LOG, "Считывание начато");
                        start = System.currentTimeMillis();
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("institution")) {
                            institution = new InstitutionEntity();
                            institution.setKey(
                                    xmlPullParser.getAttributeValue(null, "key")
                            );
                        }
                        if(institution != null) {
                            if (xmlPullParser.getName().equals("name")) {
                                if (xmlPullParser.getAttributeValue(null, "type") != null) {
                                    institution.getNameTagEntity().setType(
                                            xmlPullParser.getAttributeValue(null, "type")
                                    );
                                }
                            }
                            if (xmlPullParser.getName().equals("location")) {
                                LocationTagEntity locationTagEntity = institution.getLocationTagEntity();
                                locationTagEntity.setLocation(xmlPullParser.getAttributeValue(null, "country"));
                                if (xmlPullParser.getAttributeValue(null, "lat") != null) {
                                    locationTagEntity.setLat(
                                            Double.parseDouble(
                                                    xmlPullParser.getAttributeValue(null, "lat")
                                            )
                                    );
                                }
                                if (xmlPullParser.getAttributeValue(null, "lon") != null) {
                                    locationTagEntity.setLon(
                                            Double.parseDouble(
                                                    xmlPullParser.getAttributeValue(null, "lon")
                                            )
                                    );
                                }
                            }
                            if (xmlPullParser.getName().equals("id")) {
                                if (xmlPullParser.getAttributeValue(null, "base") != null) {
                                    institution.getIdentifiersTagEntity().setBase(
                                            xmlPullParser.getAttributeValue(null, "base")
                                    );
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(institution != null) {
                            String name = xmlPullParser.getName();
                            if (name.equals("institution")) {
                                list.add(institution);
                            }
                            NameTagEntity nameTagEntity = institution.getNameTagEntity();
                            if (name.equals("name")) {
                                if (nameTagEntity.getType() != null) {
                                    nameTagEntity.setLabel(tagText);
                                } else {
                                    nameTagEntity.setName(tagText);
                                }
                            }
                            if (name.equals("label")) {
                                nameTagEntity.setLabel(tagText);
                            }
                            if (name.equals("url")) {
                                institution.setUrl(tagText);
                            }
                            if (name.equals("location")) {
                                String spl[] = tagText.split(",");
                                LocationTagEntity locationTagEntity = institution.getLocationTagEntity();
                                switch (spl.length) {
                                    case 2:
                                        locationTagEntity.setCountry(spl[1].trim());
                                        locationTagEntity.setCity(spl[0].trim());
                                        break;
                                    case 3:
                                        locationTagEntity.setCountry(spl[2].trim());
                                        locationTagEntity.setState(spl[1].trim());
                                        locationTagEntity.setCity(spl[0].trim());
                                        break;
                                }
                            }
                            if (name.equals("id")) {
                                institution.getIdentifiersTagEntity().setIdentifier(tagText);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        tagText = xmlPullParser.getText();
                        break;

                    default:
                        break;
                }
                xmlPullParser.next();
            }
            Log.d(XML_PARSE_LOG, "Считывание окончено");
            end = System.currentTimeMillis();
            Log.d(XML_PARSE_LOG, "Время операции: " + (double)(end - start) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(XML_PARSE_LOG, "Размер списка = " + list.size());
        return list;
    }

    private static XmlPullParser getXmlPullParser(String file) {
        XmlPullParser xmlPullParser = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new FileInputStream(new File(file)), null);
            Log.d(XML_PARSE_LOG, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }
}
