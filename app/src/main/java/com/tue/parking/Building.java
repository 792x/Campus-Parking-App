package com.tue.parking;

/**
 * Created by Mert on 13-3-2016.
 */

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * @author Mert
 */
public class Building {

    private int id;

    private final String name;

    private final ArrayList<ArrayList<LatLng>> area;

    private final HashSet<Polygon> google_maps_polygons = new HashSet<Polygon>();

    private String description;

    public Building(int id, String name, ArrayList<ArrayList<LatLng>> area) {
        this.id = id;
        this.name = name;
        this.area = area;
    }

    public Building(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.area = new ArrayList<>();
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Building(int id, String name) {
        this.id = id;
        this.name = name;
        this.area = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addArea(ArrayList<LatLng> ar) {
        area.add(ar);
        System.out.println(area.size());
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public ArrayList<ArrayList<LatLng>> getArea() {
        return area;
    }

    public ArrayList<PolygonOptions> createPolygonOptions() {
        ArrayList<PolygonOptions> ret = new ArrayList<PolygonOptions>(area.size());

        for (ArrayList<LatLng> _areas : area) {
            PolygonOptions pol = new PolygonOptions();
         //   pol.fillColor(Color.GRAY);
            pol.fillColor(Color.argb(200,171,234,255));
            pol.strokeWidth(5);
            pol.strokeColor(Color.argb(50, 0, 0, 0));
            pol.addAll(_areas);
            pol.clickable(false);
            ret.add(pol);
        }

        return ret;
    }

    public void register(Polygon pol) {
        google_maps_polygons.add(pol);
    }

    public HashSet<Polygon> getPolygons() {
        return google_maps_polygons;
    }
}
