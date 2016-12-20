package de.ruf2.rube.fridgeorganizer;

import android.content.Context;
import android.os.Bundle;

import com.trello.rxlifecycle.components.RxFragment;

import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 20.12.2016.
 */

public class LifeCycleFragment extends RxFragment {
    private ScanProductFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState");
    }

    public void setFragmentTitle(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("onAttach");
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
        Timber.d("onDetach");
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
