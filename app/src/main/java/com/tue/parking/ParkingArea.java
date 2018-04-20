package com.tue.parking;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Created by Mert on 23-2-2016.
 */
public class ParkingArea {


    private int id;

    private final String name;
    private final int maxsize;
    private int current_load;

    private final ArrayList<ArrayList<LatLng>> area;

    public ParkingArea(int id, String name, int maxsize, int current_load, ArrayList<ArrayList<LatLng>> area) {
        this.id = id;
        this.name = name;
        this.maxsize = maxsize;
        this.current_load = current_load;
        this.area = area;
    }

    public ParkingArea(int id, String name, int maxsize, int current_load) {
        this.id = id;
        this.name = name;
        this.maxsize = maxsize;
        this.current_load = current_load;
        this.area = new ArrayList<>();
    }

    public void addArea(ArrayList<LatLng> ar) {
        area.add(ar);
    }

    public int getId() {
        return id;
    }


    public ArrayList<PolygonOptions> createPolygonOptions() {
        ArrayList<PolygonOptions> ret = new ArrayList<PolygonOptions>(area.size());

        for (ArrayList<LatLng> _areas : area) {
            PolygonOptions pol = new PolygonOptions();
            float _load = current_load * 100 / maxsize;
            if (_load >= 90) {
                pol.fillColor(Color.argb(125, 244, 68, 68)); // red
            } else if (_load >= 70) {
                pol.fillColor(Color.argb(125, 244, 147, 68)); // orange

            } else if (_load >= 20) {
                pol.fillColor(Color.argb(125, 145, 235, 100)); // green

            } else {
                pol.fillColor(Color.argb(125, 179, 246, 19)); // super green
            }
            pol.strokeWidth(5);
            pol.strokeColor(Color.argb(50, 0, 0, 0));
            pol.addAll(_areas);
            pol.clickable(true);
            ret.add(pol);
        }

        return ret;
    }


    public ArrayList<ArrayList<LatLng>> getAreas() {
        return area;
    }

    public ArrayList<LatLng> getAreas(int index) {
        return area.get(index);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxsize() {
        return maxsize;
    }

    public int getCurrentLoad() {
        return current_load;
    }

    public void setCurrentLoad(int current_load) {
        this.current_load = current_load;
    }

    public int getMaxSize() {
        return maxsize;
    }

    public String getName() {
        return name;
    }


}
