package de.ruf2.rube.fridgeorganizer.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.Utilities;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 06.10.2016.
 */
public class ProductRecyclerViewAdapter extends RealmRecyclerViewAdapter<Product, ProductRecyclerViewAdapter.MyViewHolder> {
    private final Context mContext;

    private final PublishSubject<Product> onClickSubject = PublishSubject.create();
    private boolean mHideFridgeName;

    public ProductRecyclerViewAdapter(Context context, RealmResults<Product> data, boolean hideFrigeName){
        super(context, data,true);
        this.mContext = context;
        this.mHideFridgeName = hideFrigeName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Timber.d("bind product recyclerview");
        final Product product= getData().get(position);
        holder.data = product;
        if (product != null) {
            holder.productFridge.setText(product.getFridge().getName());
            //toggle fridge name column
            if (mHideFridgeName) {
                holder.productFridge.setVisibility(View.INVISIBLE);
               LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.productFridge.getLayoutParams();
                params.weight = 0;
                params.height = 0;
                holder.productFridge.setLayoutParams(params);
            }
            holder.productExpiry.setText(Utilities.getDateString(product.getExpireDate()));
            holder.productAmount.setText(product.getAmount().toString());
            holder.productName.setText(product.getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(product);

                Timber.d("on click called: " + product.getName());

                Snackbar.make(v, "click product: " + product.getName() , Snackbar.LENGTH_LONG)
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

        public Product data;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Snackbar.make(v, "deleted fridge: " + data.getName() , Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Realm realm = Realm.getDefaultInstance();
            // All changes to data must happen in a transaction
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // remove a single object
                    data.deleteFromRealm();
                }
            });
            return true;
        }
    }
}
