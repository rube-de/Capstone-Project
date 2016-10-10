package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ruf2.rube.fridgeorganizer.data.entities.Fridge;
import io.realm.Realm;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFridgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFridgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFridgeFragment extends Fragment {
    @Bind(R.id.button_add_fridge)
    Button mButtonAdd;
    @Bind(R.id.edit_text_new_fridge_name)
    EditText mEditTextNewFridge;

        private OnFragmentInteractionListener mListener;
    private Activity mContext;

    private Realm mRealm;

    public AddFridgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFridgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFridgeFragment newInstance(String param1, String param2) {
        AddFridgeFragment fragment = new AddFridgeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View fragmentView = inflater.inflate(R.layout.fragment_add_fridge, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        ButterKnife.unbind(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        mRealm.close();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

        // Get reference to writable database
        mRealm.beginTransaction();

        //Create realm object
        Fridge fridge = mRealm.createObject(Fridge.class);

        //Set fields
        fridge.setName(fridgeName);

        //insert fridge into db
        mRealm.commitTransaction();

        Snackbar.make(view, "new fridge created: " + fridgeName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
