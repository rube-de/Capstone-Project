package de.ruf2.rube.fridgeorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.ProductRecyclerViewAdapter;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import io.realm.Realm;
import io.realm.RealmQuery;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpiringProductsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ExpiringProductsFragment extends Fragment {


    private Realm mRealm;

    @Bind(R.id.recycler_view_product)
    RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        Date expiryDate = new Date();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isCustomDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
        Integer expiryInt = NumberUtils.toInt(preferences.getString(getString(R.string.key_expiry_date), "0"));
        if (isCustomDate) {
            expiryDate = Utilities.changeDate(expiryInt);
        }
        RealmQuery<Product> query = mRealm.where(Product.class);
        query.lessThanOrEqualTo("expiryDate", expiryDate);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), query.findAll(), false));
        mProductRecyclerView.setHasFixedSize(true);
        mProductRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
