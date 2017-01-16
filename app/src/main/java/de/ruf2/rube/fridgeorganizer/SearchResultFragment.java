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

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductAdapter;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PRODUCT_NAME = "productName";
    private static final String EXPIRY_FROM = "expiryFrom";
    private static final String EXPIRY_TO = "expiryTo";
    private static final String BUY_FROM = "buyFrom";
    private static final String BUY_TO = "buyTo";

    private String mParamName;
    private String mParamExpiryFrom;
    private String mParamExpiryTo;
    private String mParamBuyFrom;
    private String mParamBuyTo;

    private Activity mContext;
    @Bind(R.id.recycler_view_product)
    RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    static final int PRODUCT_LOADER = 3;

    private ProductAdapter mProductAdapter;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String productName) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(PRODUCT_NAME, productName);
        fragment.setArguments(args);
        return fragment;
    }


    public static SearchResultFragment newInstance(String productName, String expiryFrom,
                                                   String expiryTo, String buyFrom, String buyTo) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(PRODUCT_NAME, productName);
        args.putString(EXPIRY_FROM, expiryFrom);
        args.putString(EXPIRY_TO, expiryTo);
        args.putString(BUY_FROM, buyFrom);
        args.putString(BUY_TO, buyTo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamName = getArguments().getString(PRODUCT_NAME);
            mParamExpiryFrom = getArguments().getString(EXPIRY_FROM);
            mParamExpiryTo = getArguments().getString(EXPIRY_TO);
            mParamBuyFrom = getArguments().getString(BUY_FROM);
            mParamBuyTo = getArguments().getString(BUY_TO);
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set actionbar title
        setFragmentTitle(getString(R.string.title_search_results));
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, fragmentView);
        //set up recycler view
        setUpRecyclerView();
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void setFragmentTitle(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onResume() {
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
        Long expiryFrom = null;
        Long expiryTo = null;
        Long buyFrom = null;
        Long buyTo = null;
        try {
            if (!StringUtils.isBlank(mParamExpiryFrom) && !StringUtils.isBlank(mParamExpiryTo)) {
                expiryFrom = Utilities.parseDate(mParamExpiryFrom).getTime();
                expiryTo = Utilities.parseDate(mParamExpiryTo).getTime();
            }
            if (!StringUtils.isBlank(mParamBuyFrom) && !StringUtils.isBlank(mParamBuyTo)) {
                buyFrom = Utilities.parseDate(mParamBuyFrom).getTime();
                buyTo = Utilities.parseDate(mParamBuyTo).getTime();
            }
        } catch (ParseException e) {
            Timber.d("could not parse date");
            e.printStackTrace();
        }
        Uri fridgeUri;
        if (expiryFrom == null || expiryTo == null || buyFrom == null || buyTo== null  ) {
            fridgeUri = FridgeContract.ProductEntry.buildProductWithName(mParamName);
        } else {
            fridgeUri = FridgeContract.ProductEntry.buildProductWithNameAndBuyAndExpiryDate(
                    mParamName,
                    buyFrom,
                    buyTo,
                    expiryFrom,
                    expiryTo);
        }

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
