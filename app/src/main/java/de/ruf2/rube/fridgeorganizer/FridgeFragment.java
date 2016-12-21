package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductRecyclerViewAdapter;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import io.realm.Realm;

/**
 * Created by Bernhard Ruf on 06.10.2016.
 */
public class FridgeFragment extends Fragment {
    private Activity mContext;
    private Realm mRealm;


    private RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;

    private static final String FRIDGE_ID = "fridgeId";
    private Integer mFridgeId;
    private Fridge mFridge;


    public FridgeFragment() {
    }

    public static FridgeFragment newInstance(Integer fridgeId) {
        FridgeFragment fragment = new FridgeFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(FRIDGE_ID, fridgeId);
        fragment.setArguments(arguments);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_fridge, container, false);
        //get Realm and fridge
        mRealm = Realm.getDefaultInstance();
        mFridgeId = getArguments().getInt(FRIDGE_ID);
        mFridge = mRealm.where(Fridge.class)
                .equalTo("_id", mFridgeId).findFirst();

        setFragmentTitle(mFridge.getName());
        //Set up fridge list
        mProductRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view_product);
        setUpRecyclerView();
        return fragmentView;
    }
    public void setFragmentTitle(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRealm.close();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void setUpRecyclerView() {
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), mRealm.where(Product.class).equalTo("fridge._id", mFridgeId).findAll(),true));
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
