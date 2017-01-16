package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductAdapter;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;

/**
 * Created by Bernhard Ruf on 06.10.2016.
 */
public class FridgeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Activity mContext;


    private RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;

    private static final String FRIDGE_ID = "fridgeId";
    private Long mFridgeId;
    private String mFridgeName;
    private FirebaseAnalytics mFirebaseAnalytics;

    static final int PRODUCT_LOADER = 1;

    private ProductAdapter mProductAdapter;


    public FridgeFragment() {
    }

    public static FridgeFragment newInstance(Long fridgeId) {
        FridgeFragment fragment = new FridgeFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(FRIDGE_ID, fridgeId);
        fragment.setArguments(arguments);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_fridge, container, false);


        Cursor fridgeCursor = mContext.getContentResolver().query(FridgeContract.FridgeEntry.buildFridgeUri(mFridgeId), null, null, null, null);
        fridgeCursor.moveToFirst();
        mFridgeName = fridgeCursor.getString(DataUtilities.COL_FRIDGE_NAME);
        fridgeCursor.close();
        setFragmentTitle(mFridgeName);
        //Set up fridge list
        mProductRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view_product);
        setUpRecyclerView();
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(PRODUCT_LOADER, null , this);
        super.onActivityCreated(savedInstanceState);
    }

    public void setFragmentTitle(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = FridgeContract.ProductEntry.TABLE_NAME + "." + FridgeContract.ProductEntry.COLUMN_NAME + " ASC";

        Uri fridgeUri = FridgeContract.ProductEntry.buildProductsOfFridge(mFridgeId);
        return new CursorLoader(getActivity(),//context
                fridgeUri,//uri
                DataUtilities.PRODUCT_COLUMNS, //projection
                null,//selection
                null,//selection args
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void setUpRecyclerView() {
        mProductAdapter = new ProductAdapter(getActivity(),true);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(mProductAdapter);
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
