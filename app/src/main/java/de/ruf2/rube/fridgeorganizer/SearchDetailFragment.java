package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchDetailFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.edit_text_search_product_name)
    EditText mEditTextProductName;
    @Bind(R.id.edit_text_search_buy_from)
    EditText mEditTextBuyFrom;
    @Bind(R.id.edit_text_search_buy_to)
    EditText mEditTextBuyTo;
    @Bind(R.id.edit_text_search_expiry_from)
    EditText mEditTextExpiryFrom;
    @Bind(R.id.edit_text_search_expiry_to)
    EditText mEditTextExpiryTo;

    private Activity mContext;

    private DatePickerDialog mBuyFromDatePickerDialog;
    private DatePickerDialog mBuyToDatePickerDialog;
    private DatePickerDialog mExpiryFromDatePickerDialog;
    private DatePickerDialog mExpiryToDatePickerDialog;
    private FirebaseAnalytics mFirebaseAnalytics;


    public SearchDetailFragment() {
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
        setFragmentTitle(getString(R.string.title_search_product));
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_search_detail, container, false);
        ButterKnife.bind(this, fragmentView);

        setDateTimeField();

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

    @Override
    public void onClick(View view) {
        if (view == mEditTextBuyFrom) {
            mBuyFromDatePickerDialog.show();
        } else if (view == mEditTextBuyTo) {
            mBuyToDatePickerDialog.show();
        } else if (view == mEditTextExpiryTo) {
            mExpiryToDatePickerDialog.show();
        } else if (view == mEditTextExpiryFrom) {
            mExpiryFromDatePickerDialog.show();
        }
    }

    @OnClick(R.id.button_search_product)
    public void onClickSearchProduct(View view) {
        Timber.d("onClickSearchProduct");
        String productName= mEditTextProductName.getText().toString();
        String expiryFrom = mEditTextExpiryFrom.getText().toString();
        String expiryTo = mEditTextExpiryTo.getText().toString();
        String buyFrom = mEditTextBuyFrom.getText().toString();
        String buyTo = mEditTextBuyTo.getText().toString();
        if(productName.isEmpty()) {
            mEditTextProductName.setError(getString(R.string.field_required));
        }else {
            mEditTextProductName.setError(null);
            SearchResultFragment newFragment = SearchResultFragment.newInstance(productName,
                    expiryFrom,expiryTo,buyFrom,buyTo);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Utilities.hideKeyboard(getActivity());
        }
    }

    private void setDateTimeField() {

        mEditTextBuyFrom.setOnClickListener(this);
        mEditTextBuyTo.setOnClickListener(this);
        mEditTextExpiryFrom.setOnClickListener(this);
        mEditTextExpiryTo.setOnClickListener(this);

        mBuyFromDatePickerDialog = Utilities.initDatePicker(
                mBuyFromDatePickerDialog, mEditTextBuyFrom, getActivity());
        mBuyToDatePickerDialog = Utilities.initDatePicker(
                mBuyToDatePickerDialog, mEditTextBuyTo, getActivity());
        mExpiryFromDatePickerDialog = Utilities.initDatePicker(
                mBuyToDatePickerDialog, mEditTextExpiryFrom, getActivity());
        mExpiryToDatePickerDialog = Utilities.initDatePicker(
                mBuyToDatePickerDialog, mEditTextExpiryTo, getActivity());


        //init dates
        mEditTextExpiryFrom.setText(Utilities.getZeroDateString());
        mEditTextExpiryTo.setText(Utilities.getTodayDateString());
        mEditTextBuyFrom.setText(Utilities.getZeroDateString());
        mEditTextBuyTo.setText(Utilities.getTodayDateString());
    }

}
