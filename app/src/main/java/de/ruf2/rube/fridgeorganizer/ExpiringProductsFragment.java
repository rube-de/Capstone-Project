package de.ruf2.rube.fridgeorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductAdapter;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpiringProductsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ExpiringProductsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{



    @Bind(R.id.recycler_view_product)
    RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    static final int PRODUCT_LOADER = 2;

    private ProductAdapter mProductAdapter;

    public ExpiringProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setFragmentTitle(getString(R.string.title_expiring_products));
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_expiring_products, container, false);
        ButterKnife.bind(this, fragmentView);
        //set up recycler view
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
        String sortOrder = FridgeContract.ProductEntry.TABLE_NAME + "." + FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " ASC";
        Date expiryDate = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), 1);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isCustomDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
        Integer expiryInt = NumberUtils.toInt(preferences.getString(getString(R.string.key_expiry_date), "0"));
        if (isCustomDate) {
            expiryDate = Utilities.changeDate(expiryInt);
        }
        Uri fridgeUri = FridgeContract.ProductEntry.buildProductWithExpiryEndDate(expiryDate.getTime());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void setUpRecyclerView() {
       mProductAdapter = new ProductAdapter(getActivity(), false);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(mProductAdapter);
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
