package org.tracker.prashu.savedRoutes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tracker.prashu.maps.SavedMapActivity;
import org.tracker.prashu.navigationApp.R;

import java.util.ArrayList;

// adapter class for saved routes that holds the recycler view.
public class SavedRoutesAdapter extends RecyclerView.Adapter<SavedRoutesAdapter.MyViewHolder> {

    View view;
    LayoutInflater inflater;
    Context context;
    ArrayList<SavedRoutesData> arrayList = new ArrayList<>();
    private int identity;

    // inflating the inflater with the context and arraylist as the passed arraylist.
    SavedRoutesAdapter(Context context, ArrayList<SavedRoutesData> arrayList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating the view with the layout for saved routes
        view = inflater.inflate(R.layout.saved_routes_layout, parent, false);
        // adding the view to the view holder class that is created down.
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        // creating an object of the data storing pojo class for the data stored in the arraylist at a given position
        final SavedRoutesData dataPojo = arrayList.get(position);
        // setting the values in the text views.
        holder.tvName.setText(dataPojo.getName());
        holder.tvDate.setText(dataPojo.getDate());
        holder.tvTime.setText(dataPojo.getTime());
        holder.imgMap.setImageResource(R.drawable.splash_icon);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on click of the cardveiw it will fire a intent to SavedMapActivity and passing values in the intent.
                Intent intent = new Intent(context, SavedMapActivity.class);
                intent.putExtra("id", dataPojo.getId());
                intent.putExtra("name", dataPojo.getName());
                intent.putExtra("startLat", dataPojo.getStart_lat());
                intent.putExtra("startLon", dataPojo.getStart_lon());
                intent.putExtra("currentLat", dataPojo.getCurrent_lat());
                intent.putExtra("currentLon", dataPojo.getCurrent_lon());
                intent.putExtra("polylineArray", dataPojo.getPolyline_array());
                intent.putExtra("duration", dataPojo.getDuration());
                intent.putExtra("distance", dataPojo.getDistance());
                intent.putExtra("color", dataPojo.getColor());
                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // set the identity of the item clicked on long pressed.
                setIdentity(dataPojo.getId());
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.cardView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    // method that returns the size of the array list.
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    // method to get the identity
    public int getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = Integer.parseInt(identity);
    }

    // custom class that extends the RecyclerView.viewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView tvName;
        TextView tvDate;
        TextView tvTime;
        ImageView imgMap;
        CardView cardView;

        public MyViewHolder(View v) {
            super(v);

            // creating the object of the views by referencing to their id in xml file.
            tvName = (TextView) v.findViewById(R.id.tvNameSavedRoute);
            tvDate = (TextView) v.findViewById(R.id.tvDateSavedRoute);
            tvTime = (TextView) v.findViewById(R.id.tvTimeSavedRoute);
            imgMap = (ImageView) v.findViewById(R.id.imgSavedRoute);
            cardView = (CardView) v.findViewById(R.id.savedMapCardView);
            v.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // create teh context menu.
            menu.add(Menu.NONE, R.id.deleteRoute,
                    Menu.NONE, "Delete Route");
        }
    }
}
