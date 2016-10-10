package de.ruf2.rube.fridgeorganizer.adapter;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.ruf2.rube.fridgeorganizer.FridgeFragment;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class FridgeRecyclerViewAdapter extends RealmRecyclerViewAdapter<Fridge, FridgeRecyclerViewAdapter.MyViewHolder>  {
    private final FragmentActivity mContext;

    private final PublishSubject<Fridge> onClickSubject = PublishSubject.create();


    public FridgeRecyclerViewAdapter(FragmentActivity context, RealmResults<Fridge> data){
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
        final Fridge fridge = getData().get(position);
        holder.data = fridge;
        holder.fridgeName.setText(fridge.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(fridge);

                Timber.d("on click called: " + fridge.getName());

                FridgeFragment newFragment = new FridgeFragment();
            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            }
        });
    }

    public Observable<Fridge> getPositionClicks(){
        return onClickSubject.asObservable();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView fridgeName;
        public Fridge data;

        public MyViewHolder(View view) {
            super(view);
            fridgeName = (TextView) view.findViewById(R.id.text_view_item_fridge_name);
            view.setOnLongClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            Timber.d("on click frdige called");
//            FridgeFragment newFragment = new FridgeFragment();
//            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }

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
