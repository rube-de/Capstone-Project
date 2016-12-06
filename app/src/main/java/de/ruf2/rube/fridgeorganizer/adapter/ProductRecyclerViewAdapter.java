package de.ruf2.rube.fridgeorganizer.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.ruf2.rube.fridgeorganizer.R;
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

    public ProductRecyclerViewAdapter(Context context, RealmResults<Product> data){
        super(context, data,true);
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Product product= getData().get(position);
        holder.data = product;
        if (product != null)
         holder.productName.setText(product.getName());
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
        public TextView productName;
        public Product data;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.text_view_item_product_name);
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
