package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFridgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddFridgeFragment extends Fragment {
    @Bind(R.id.button_add_fridge)
    Button mButtonAdd;
    @Bind(R.id.edit_text_new_fridge_name)
    EditText mEditTextNewFridge;

        private OnFragmentInteractionListener mListener;
    private Activity mContext;

    private FirebaseAnalytics mFirebaseAnalytics;

    public AddFridgeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
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
        //set actionbar title
        setFragmentTitle(getString(R.string.title_add_fridge));
        // Inflate the layout for this fragment
       View fragmentView = inflater.inflate(R.layout.fragment_add_fridge, container, false);
        ButterKnife.bind(this, fragmentView);
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
        ButterKnife.unbind(this);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    @OnClick(R.id.button_add_fridge)
    public void onClickCreateFridge(View view){
        Timber.d(" create fridge from favs");
        //hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == mContext.getCurrentFocus()) ? null :
                mContext.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        //get fridge values
        String fridgeName = mEditTextNewFridge.getText().toString();

        //content values
        ContentValues fridgeValues = new ContentValues();
        fridgeValues.put(FridgeContract.FridgeEntry.COLUMN_NAME, fridgeName);

        //insert into content provider
        mContext.getContentResolver().insert(FridgeContract.FridgeEntry.CONTENT_URI, fridgeValues);


        Snackbar.make(view, getString(R.string.snackbar_new_fridge) + fridgeName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
