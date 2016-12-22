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

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductRecyclerViewAdapter;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PRODUCT_NAME = "productName";
    private static final String EXPIRY_FROM = "expiryFrom";
    private static final String EXPIRY_TO = "expiryTo";
    private static final String BUY_FROM = "buyFrom";
    private static final String BUY_TO = "buyTo";

    // TODO: Rename and change types of parameters
    private String mParamName;
    private String mParamExpiryFrom;
    private String mParamExpiryTo;
    private String mParamBuyFrom;
    private String mParamBuyTo;

    private Activity mContext;
    private Realm mRealm;
    @Bind(R.id.recycler_view_product)
    RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productName Parameter 1.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String productName){
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(PRODUCT_NAME, productName);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productName Parameter 1.
     * @param expiryFrom  Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        //get Realm
        mRealm = Realm.getDefaultInstance();
        //set up recycler view
        setUpRecyclerView();
        return fragmentView;
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
        mRealm.close();
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
        RealmQuery<Product> query = mRealm.where(Product.class);
        query.contains("name", mParamName, Case.INSENSITIVE);
        try {
            if (!StringUtils.isBlank(mParamExpiryFrom) && !StringUtils.isBlank(mParamExpiryTo)) {
                query.between("expiryDate", Utilities.parseDate(mParamExpiryFrom),
                        Utilities.parseDate(mParamExpiryTo));
            }
            if (!StringUtils.isBlank(mParamBuyFrom) && !StringUtils.isBlank(mParamBuyTo)) {
                query.between("buyDate", Utilities.parseDate(mParamBuyFrom),
                        Utilities.parseDate(mParamBuyTo));
            }
        } catch (ParseException e) {
            Timber.d("could not parse date");
            e.printStackTrace();
        }
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), query.findAll(), false));
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
