package de.ruf2.rube.fridgeorganizer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.FridgeRecyclerViewAdapter;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Realm mRealm;

    private RecyclerView mFridgeRecyclerView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        mRealm = Realm.getDefaultInstance();

        //Set up fridge list
        mFridgeRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fridge);
        setUpRecyclerView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onStop();
        mRealm.close();
    }

    private void setUpRecyclerView() {
        mFridgeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFridgeRecyclerView.setAdapter(new FridgeRecyclerViewAdapter(getActivity(), mRealm.where(Fridge.class).findAll()));
        mFridgeRecyclerView.setHasFixedSize(true);
        mFridgeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }



}
