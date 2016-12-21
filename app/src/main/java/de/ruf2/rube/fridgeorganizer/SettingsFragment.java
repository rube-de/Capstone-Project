package de.ruf2.rube.fridgeorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import de.ruf2.rube.fridgeorganizer.receivers.NotificationEventReceiver;
import timber.log.Timber;

import static de.ruf2.rube.fridgeorganizer.R.xml.preferences;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setFragmentTitle("Settings");
        addPreferencesFromResource(preferences);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean customDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
        getPreferenceScreen().findPreference(getString(R.string.key_expiry_date)).setEnabled(customDate);

    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        setFragmentTitle("Settings");
//        return view;
//    }

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (key.equals(getString(R.string.key_enable_notifications))) {
            Boolean enableNotifications = preferences.getBoolean(getString(R.string.key_enable_notifications), false);
            if (enableNotifications) {
                NotificationEventReceiver.setupAlarm(getActivity());
            } else {
                NotificationEventReceiver.cancelAlarm(getActivity());
            }
        } else if (key.equals(getString(R.string.key_custom_date))) {
            Boolean customDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
            getPreferenceScreen().findPreference(getString(R.string.key_expiry_date)).setEnabled(customDate);
        }
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
}
