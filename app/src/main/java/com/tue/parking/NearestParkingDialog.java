package com.tue.parking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mert.testproj2.R;
import com.tue.tools.Handler;

import java.util.TreeSet;

/**
 * Created by Mert on 18-3-2016.
 */
public class NearestParkingDialog extends DialogFragment {

    TreeSet<Pair<Float, ParkingArea>> data;

     Handler<ParkingArea> handler ;


    public void setHandler(Handler<ParkingArea> handler) {
        this.handler = handler;
    }

    public void setData(TreeSet<Pair<Float, ParkingArea>> data){
      this.data = data;
  }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View fragment = inflater.inflate(R.layout.fragment_nearest_parking, null);
        builder.setView(fragment) ;


        RecyclerView rv = (RecyclerView) fragment.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter( data,handler,this);
        rv.setAdapter(adapter);

        return builder.create();
    }

}
