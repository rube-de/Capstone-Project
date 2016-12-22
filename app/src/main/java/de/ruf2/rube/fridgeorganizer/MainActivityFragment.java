package de.ruf2.rube.fridgeorganizer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ruf2.rube.fridgeorganizer.adapter.DividerItemDecoration;
import de.ruf2.rube.fridgeorganizer.adapter.FridgeRecyclerViewAdapter;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import io.realm.Realm;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ScanProductFragment.OnFragmentInteractionListener mListener;
    private Realm mRealm;
    private RecyclerView mFridgeRecyclerView;
    @Bind(R.id.edit_text_search)
    EditText mSearchText;
    private FirebaseAnalytics mFirebaseAnalytics;

    public MainActivityFragment() {
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
        setFragmentTitle(getString(R.string.app_name));
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        //get db
        mRealm = Realm.getDefaultInstance();

        //listen to search on keyboard
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onClickSearchProduct(view);
                    handled = true;
                }
                return handled;
            }
        });

        //Set up fridge list
        mFridgeRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fridge);
        setUpRecyclerView();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void setFragmentTitle(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScanProductFragment.OnFragmentInteractionListener) {
            mListener = (ScanProductFragment.OnFragmentInteractionListener) context;
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

    private void setUpRecyclerView() {
        mFridgeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFridgeRecyclerView.setAdapter(new FridgeRecyclerViewAdapter(getActivity(), mRealm.where(Fridge.class).findAll()));
        mFridgeRecyclerView.setHasFixedSize(true);
        mFridgeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    @OnClick(R.id.image_button_search)
    public void onClickSearchProduct(View view) {
        Timber.d("onClickSearchProduct");
        String productName = mSearchText.getText().toString();
        if (productName.isEmpty()) {
            mSearchText.setError(getString(R.string.field_required));
        } else {
            mSearchText.setError(null);
            SearchResultFragment newFragment = SearchResultFragment.newInstance(productName);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Utilities.hideKeyboard(getActivity());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }


}
