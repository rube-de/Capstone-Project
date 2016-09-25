package de.ruf2.rube.fridgeorganizer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class FridgeRecyclerViewAdapter extends RealmRecyclerViewAdapter<Fridge, FridgeRecyclerViewAdapter.MyViewHolder>  {
    private final Context mContext;

    public FridgeRecyclerViewAdapter(Context context, RealmResults<Fridge> data){
        super(context, data,true);
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_fridge, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Fridge fridge = getData().get(position);
        holder.data = fridge;
        holder.fridgeName.setText(fridge.getName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView fridgeName;
        public Fridge data;

        public MyViewHolder(View view) {
            super(view);
            fridgeName = (TextView) view.findViewById(R.id.text_view_item_fridge_name);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
//            activity.deleteFridgeItem(data);
            return true;
        }
    }
}
