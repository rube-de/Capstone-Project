package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ruf2.rube.fridgeorganizer.data.DataUtilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScanProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScanProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanProductFragment extends RxFragment implements Observer<String>,View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_EAN = "ean";

    private String mEan;
    private Boolean mScan = false;

    private OnFragmentInteractionListener mListener;

    private Observable<String> cache;

    @Bind(R.id.edit_text_ean)
    EditText mEditTextEan;
    @Bind(R.id.button_scan)
    Button mButtonScan;
    @Bind(R.id.edit_text_buy_date)
    EditText mEditTextBuyDate;
    @Bind(R.id.edit_text_expiry_date)
    EditText mEditTextExpiryDate;
    @Bind(R.id.edit_text_product_name)
    EditText mEditTextProductName;
    @Bind(R.id.edit_text_product_amount)
    EditText mEditTextProductAmount;
    @Bind(R.id.spinner_fridge)
    Spinner mSpinnerFridge;
    @Bind(R.id.button_new_fridge_product_fragment)
    Button mButtonNewFridge;

    private final String EAN_CONTENT = "eanContent";
    private Activity mContext;

    private DatePickerDialog mBuyDatePickerDialog;
    private DatePickerDialog mExpiryDatePickerDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    static final int FRIDGE_LOADER = 0;
    private SimpleCursorAdapter mFridgeAdapter;

    public ScanProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ean Parameter 1.
     * @return A new instance of fragment ScanProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanProductFragment newInstance(boolean ean) {
        ScanProductFragment fragment = new ScanProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_EAN, ean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScan = getArguments().getBoolean(ARG_EAN);
        }
        mContext = getActivity();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        //start scan if fragment was started via scan
        if(mScan){
            startScan();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEditTextEan != null) {
            outState.putString(EAN_CONTENT, mEditTextEan.getText().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set actionbar title
        setFragmentTitle(getString(R.string.title_scan_product));
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_scan_product, container, false);
        ButterKnife.bind(this, fragmentView);



        setDateTimeField();

        //init fridge spinner

        String[] columns = {FridgeContract.FridgeEntry.COLUMN_NAME};
        int[] toViews = {R.id.checked_text_spinner};
        mFridgeAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.sipmle_spinner_dropdown_item,
                null,
                columns,
                toViews,
                0
                );
        mFridgeAdapter.setDropDownViewResource(R.layout.sipmle_spinner_dropdown_item);
//        ArrayAdapter<Fridge> fridgeAdapter = new ArrayAdapter<>(getActivity(),
//                R.layout.sipmle_spinner_dropdown_item, fridges);
//        fridgeAdapter.setDropDownViewResource(R.layout.sipmle_spinner_dropdown_item);
        mSpinnerFridge.setAdapter(mFridgeAdapter);

        //init dates
        mEditTextBuyDate.setText(Utilities.getTodayDateString());
        mEditTextExpiryDate.setText(Utilities.getTodayDateString());


        mEditTextEan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();

                //get product name from ean service.
                cache = getStringObservable();
                cache
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ScanProductFragment.this);

            }
        });

        //get ean from init
        if(!TextUtils.isEmpty(mEan)) {
            mEditTextEan.setText(mEan);
        }

        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(ScanProductFragment.this);
                scanIntegrator.initiateScan();
            }
        });

        if (savedInstanceState != null) {
            mEditTextEan.setText(savedInstanceState.getString(EAN_CONTENT));
        }


        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(FRIDGE_LOADER, null , this);
        super.onActivityCreated(savedInstanceState);
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
    public void onClick(View view) {
        if (view == mEditTextBuyDate) {
            mBuyDatePickerDialog.show();
        } else if (view == mEditTextExpiryDate) {
            mExpiryDatePickerDialog.show();
        }
    }

    @OnClick(R.id.button_add_product)
    public void onClickAddProduct(View view) {
        Timber.d("adding product");
        Utilities.hideKeyboard(mContext);
        try {
            //get product values
            String productName = mEditTextProductName.getText().toString();
            Date buyDate = Utilities.parseDate(mEditTextBuyDate.getText().toString());
            Date expireDate = Utilities.parseDate(mEditTextExpiryDate.getText().toString());
            Integer amount = NumberUtils.toInt(mEditTextProductAmount.getText().toString(), 0);
            Long fridgeId = ((Cursor) mSpinnerFridge.getSelectedItem()).getLong(
                    ((Cursor) mSpinnerFridge.getSelectedItem()).getColumnIndex(FridgeContract.FridgeEntry._ID));

            if (productName.isEmpty() ||  amount == 0) {
                if (productName.isEmpty()) {
                    mEditTextProductName.setError(getString(R.string.field_required));
                }
                if (amount == 0) {
                    mEditTextProductAmount.setError(getString(R.string.error_product_amount));
                }
            } else {

                //content values
                ContentValues productValues = new ContentValues();
                productValues.put(FridgeContract.ProductEntry.COLUMN_NAME, productName);
                productValues.put(FridgeContract.ProductEntry.COLUMN_AMOUNT, amount);
                productValues.put(FridgeContract.ProductEntry.COLUMN_BUY_DATE, buyDate.getTime());
                productValues.put(FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE, expireDate.getTime());
                productValues.put(FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY, fridgeId);

                //content provider
                mContext.getContentResolver().insert(FridgeContract.ProductEntry.CONTENT_URI, productValues);


                Snackbar.make(view, getString(R.string.snack_new_product_created) + productName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.button_new_fridge_product_fragment)
    public void onClickNewFridge(View view) {
        AddFridgeFragment newFragment = new AddFridgeFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(String s) {
        mEditTextProductName.setText(s);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = FridgeContract.FridgeEntry.COLUMN_NAME + " ASC";

        Uri fridgeUri = FridgeContract.FridgeEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                fridgeUri,
                DataUtilities.FRIDGE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFridgeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFridgeAdapter.swapCursor(null);
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            mEditTextEan.setText(scanningResult.getContents());
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    R.string.no_scan_data_received, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String productName = Utilities.fetchProduct(mEditTextEan.getText().toString());
                subscriber.onNext(productName);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    private void setDateTimeField() {
        mEditTextExpiryDate.setOnClickListener(this);
        mEditTextBuyDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mBuyDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditTextBuyDate.setText(Utilities.getDateString(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mExpiryDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditTextExpiryDate.setText(Utilities.getDateString(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void startScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(ScanProductFragment.this);
        scanIntegrator.initiateScan();
    }


}
