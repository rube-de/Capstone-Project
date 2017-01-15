package de.ruf2.rube.fridgeorganizer.adapter;

import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.ruf2.rube.fridgeorganizer.FridgeFragment;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.Utilities;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 15.01.2017.
 */

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.MyViewHolder> {

    private Cursor mCursor;
    private final FragmentActivity mContext;
//    final private View mEmptyView;


    public FridgeAdapter(FragmentActivity context) {
        mContext = context;
    }

    public interface FridgeAdapterOnClickHandler {
        void onClick(Long date, MyViewHolder vh);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fridge, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Timber.d("bind fridge recyclerview");
        mCursor.moveToPosition(position);
        final long fridgeId = mCursor.getLong(DataUtilities.COL_FRIDGE_ID);
        final String fridgeName = mCursor.getString(DataUtilities.COL_FRIDGE_NAME);

        holder.fridgeName.setText(fridgeName);
        holder.fridgeId = fridgeId;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("on click called: " + fridgeName);

                FridgeFragment newFragment = FridgeFragment.newInstance(fridgeId);
                FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Utilities.hideKeyboard(mContext);
            }
        });
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView fridgeName;
        public Long fridgeId;

        public MyViewHolder(View view) {
            super(view);
            fridgeName = (TextView) view.findViewById(R.id.text_view_item_fridge_name);
            view.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            Snackbar.make(v, "deleted fridge: " + fridgeName.getText(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            String[] selectionArgs = new String[]{Long.toString(fridgeId)};
            String selection = "_ID = ?";
            mContext.getContentResolver().delete(FridgeContract.FridgeEntry.CONTENT_URI, selection, selectionArgs);
            return true;
        }


        @Override
        public void onClick(View v) {
            Timber.d("on click called: " + fridgeName.getText());

            FridgeFragment newFragment = FridgeFragment.newInstance(fridgeId);
            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Utilities.hideKeyboard(mContext);
        }
    }
    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
//        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof MyViewHolder ) {
            MyViewHolder vfh = (MyViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }
}
