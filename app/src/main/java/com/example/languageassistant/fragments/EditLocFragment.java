package com.example.languageassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.languageassistant.Keys;
import com.example.languageassistant.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditLocFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditLocFragment extends DialogFragment {

    //interface to communicate with main activity
    public interface OnItemSelectedListener {
        public void onLocationUpdate();
    }

    private static final String ARG_CITY = "city";
    private static final String ARG_REGION = "region";
    private String city;
    private String region;

    private OnItemSelectedListener listener;

    EditText etCity;
    EditText etRegion;
    MaterialButton btnCancel;
    MaterialButton btnSave;

    public EditLocFragment() {
        // Required empty public constructor
    }

    //factory method to get new instance of this fragment and communicate arguments
    public static EditLocFragment newInstance(String city, String region) {
        EditLocFragment fragment = new EditLocFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        args.putString(ARG_REGION, region);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditLangFragment.OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            city = getArguments().getString(ARG_CITY);
            region = getArguments().getString(ARG_REGION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_loc, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCity = (EditText) view.findViewById(R.id.etCity);
        etRegion = view.findViewById(R.id.etRegion);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        // Set title and location info
        getDialog().setTitle(getContext().getString(R.string.edit_loc));
        etCity.setText(city);
        etRegion.setText(region);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCity= etCity.getText().toString().trim();
                String newRegion= etRegion.getText().toString().trim();

                if((newCity.length() != 0 && newRegion.length() == 0) || (newCity.length() == 0 && newRegion.length() != 0) ){
                    //cannot have only city or only region inputted
                    Toast.makeText(getContext(), getContext().getString(R.string.edit_loc_error), Toast.LENGTH_SHORT).show();
                }else{
                    if(newCity.length()==0 && newRegion.length() ==0){
                        //leaving everything blank results in no location info
                        ParseUser.getCurrentUser().put(Keys.KEY_LOC, "");
                    }else{
                        //update new location info
                        ParseUser.getCurrentUser().put(Keys.KEY_LOC, etCity.getText().toString().trim() +", "+ etRegion.getText().toString().trim());
                    }

                    //save new location info
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                dismiss();
                                listener.onLocationUpdate();
                            }else{
                                Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}