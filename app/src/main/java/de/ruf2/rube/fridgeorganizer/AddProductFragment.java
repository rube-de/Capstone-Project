package de.ruf2.rube.fridgeorganizer;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import io.realm.Realm;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment implements OnClickListener {
    @Bind(R.id.edit_text_buy_date)
    EditText mEditTextBuyDate;
    @Bind(R.id.edit_text_expire_date)
    EditText mEditTextExpireDate;
    @Bind(R.id.edit_text_product_name)
    EditText mEditTextProductName;
    @Bind(R.id.edit_text_product_amount)
    EditText mEditTextProductAmount;
    @Bind(R.id.spinner_fridge)
    Spinner mSpinnerFridge;
    @Bind(R.id.button_new_fridge_product_fragment)
    Button mButtonNewFrige;

    private Realm mRealm;

    private Activity mContext;

    private DatePickerDialog mBuyDatePickerDialog;
    private DatePickerDialog mExpireDatePickerDialog;

    private SimpleDateFormat mDateFormatter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        ButterKnife.bind(this, view);
        mDateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

        setDateTimeField();

        return view;
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

    @Override
    public void onClick(View view) {
        if (view == mEditTextBuyDate) {
            mBuyDatePickerDialog.show();
        } else if (view == mEditTextExpireDate) {
            mExpireDatePickerDialog.show();
        }
    }

    @OnClick(R.id.button_add_product)
    public void onClickAddProduct(View view) {
        Timber.d("adding product");
        Utilities.hideKeyboard(mContext);

        try {
            //get product values
            String productName = mEditTextProductName.getText().toString();
            Date buyDate = mDateFormatter.parse(mEditTextBuyDate.getText().toString());
            Date expireDate = mDateFormatter.parse(mEditTextExpireDate.getText().toString());
            int amount = Integer.parseInt(mEditTextProductAmount.getText().toString());
//            Fridge fridge = mSpinnerFridge



            // Get reference to writable database
            mRealm.beginTransaction();

            //Create realm object
            Product product = mRealm.createObject(Product.class);

            //Set fields
            product.setName(productName);
            product.setAmount(amount);
            product.setBuyDate(buyDate);
            product.setExpireDate(expireDate);

            //insert fridge into db
            mRealm.commitTransaction();

            Snackbar.make(view, "new product created: " + productName, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

    private void setDateTimeField() {
        mEditTextExpireDate.setOnClickListener(this);
        mEditTextBuyDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mBuyDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditTextBuyDate.setText(mDateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mExpireDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditTextExpireDate.setText(mDateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
