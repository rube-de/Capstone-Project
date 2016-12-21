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
 * Use the {@link ExpiringProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpiringProductsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Realm mRealm;

    @Bind(R.id.recycler_view_product)
    RecyclerView mProductRecyclerView;

    private OnFragmentInteractionListener mListener;

    public ExpiringProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpiringProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpiringProductsFragment newInstance(String param1, String param2) {
        ExpiringProductsFragment fragment = new ExpiringProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
