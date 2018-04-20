package com.tue.parking;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mert.testproj2.R;
import com.tue.tools.Handler;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Mert on 18-3-2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    ArrayList<Pair<Float, ParkingArea>> data;

    Handler<ParkingArea> handler ;

    NearestParkingDialog npd;




    public RVAdapter(TreeSet<Pair<Float,ParkingArea>> data,  Handler<ParkingArea> handler, NearestParkingDialog npd) {
        this.data = new ArrayList<>(data);
        this.handler = handler;
        this.npd = npd;
    }


    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView size;
        TextView distance;
        ParkingArea pa;

        PersonViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.nearest_parking_title);
            size = (TextView) itemView.findViewById(R.id.nearest_parking_size);
            distance = (TextView) itemView.findViewById(R.id.nearest_parking_distance);

        }
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_nearest_parking_item, viewGroup, false);
       final PersonViewHolder pvh = new PersonViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SEARCH", "CLICKED on "+pvh.pa.getName());
                Toast.makeText(v.getContext(), "a", Toast.LENGTH_LONG);
                npd.dismiss();;
                handler.handle(pvh.pa);


            }
        });
        return pvh;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        ParkingArea pa = data.get(i).second;
        personViewHolder.title.setText(pa.getName());

        int dist = data.get(i).first.intValue();

        personViewHolder.distance.setText("distance: " + (dist >= 1000 ? (dist / 1000) + "km" : dist + "m"));
        personViewHolder.size.setText("free slots: " + (pa.getMaxsize() - pa.getCurrentLoad()) + "/" + pa.getMaxsize());
        personViewHolder.pa = pa;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}