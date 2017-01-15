package de.ruf2.rube.fridgeorganizer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.Utilities;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 15.01.2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Cursor mCursor;

    private final Context mContext;

    private boolean mHideFridgeName;

    public ProductAdapter(FragmentActivity context, boolean hideFridgeName) {
        this.mContext = context;
        this.mHideFridgeName = hideFridgeName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Timber.d("bind product recyclerview");
        mCursor.moveToPosition(position);

        final Long productId = mCursor.getLong(DataUtilities.COL_PRODUCT_ID);
        final String productName = mCursor.getString(DataUtilities.COL_PRODUCT_NAME);
        String amount = String.valueOf(mCursor.getInt(DataUtilities.COL_PRODUCT_AMOUNT));
        String buyDate = Utilities.getDateString(new Date(mCursor.getLong(DataUtilities.COL_PRODUCT_BUY_DATE)));
        String expiryDate = Utilities.getDateString(new Date(mCursor.getLong(DataUtilities.COL_PRODUCT_EXPIRY_DATE)));
        holder.productId = productId;
        if (productId != null) {
            Long fridgeId = mCursor.getLong(DataUtilities.COL_PRODUCT_FRIDGE_KEY);
            Cursor fridgeCursor = mContext.getContentResolver().query(FridgeContract.FridgeEntry.buildFridgeUri(fridgeId), null, null, null, null);
            fridgeCursor.moveToFirst();
            String fridgeName = fridgeCursor.getString(DataUtilities.COL_FRIDGE_NAME);
            if (!StringUtils.isBlank(fridgeName)) {
                holder.productFridge.setText(fridgeName);
            } else {
                Timber.d(" no fridge found for product:" + productName);
            }
            //toggle fridge name column
            if (mHideFridgeName) {
                holder.productFridge.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.productFridge.getLayoutParams();
                params.weight = 0;
                params.height = 0;
                holder.productFridge.setLayoutParams(params);
            }
            holder.productExpiry.setText(expiryDate);
            holder.productAmount.setText(amount);
            holder.productName.setText(productName);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timber.d("on click called: " + productName);

                Snackbar.make(v, "click product: " + productName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        @Bind(R.id.text_view_item_product_name)
        public TextView productName;
        @Bind(R.id.text_view_item_product_amount)
        public TextView productAmount;
        @Bind(R.id.text_view_item_product_expiry)
        public TextView productExpiry;
        @Bind(R.id.text_view_item_product_fridge)
        public TextView productFridge;

        public long productId;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Snackbar.make(v, "deleted product: " + productName.getText(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            String[] selectionArgs = new String[]{Long.toString(productId)};
            String selection = "_ID = ?";
            mContext.getContentResolver().delete(FridgeContract.ProductEntry.CONTENT_URI, selection, selectionArgs);
            return true;
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
        if ( viewHolder instanceof FridgeAdapter.MyViewHolder) {
            FridgeAdapter.MyViewHolder vfh = (FridgeAdapter.MyViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }
}

