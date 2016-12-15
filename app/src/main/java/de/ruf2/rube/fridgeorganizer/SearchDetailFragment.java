package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDetailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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


    public SearchDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDetailFragment newInstance(String param1, String param2) {
        SearchDetailFragment fragment = new SearchDetailFragment();
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
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_search_detail, container, false);
        ButterKnife.bind(this, fragmentView);

        setDateTimeField();
//init dates
        mEditTextBuyTo.setText(Utilities.getTodayDateString());
        mEditTextBuyFrom.setText(Utilities.getTodayDateString());

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
        mEditTextBuyTo.setText(Utilities.getTodayDateString());
        mEditTextBuyFrom.setText(Utilities.getTodayDateString());
        mEditTextExpiryFrom.setText(Utilities.getTodayDateString());
        mEditTextExpiryTo.setText(Utilities.getTodayDateString());
    }

}
