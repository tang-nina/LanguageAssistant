package com.example.languageassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

    public interface OnItemSelectedListener {
        public void onLocationUpdate();
    }

    private OnItemSelectedListener listener;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CITY = "city";
    private static final String ARG_REGION = "region";

    private String city;
    private String region;


    EditText etCity;
    EditText etRegion;
    MaterialButton btnCancel;
    MaterialButton btnSave;

    public EditLocFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditLocFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_loc, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCity = (EditText) view.findViewById(R.id.etCity);
        etRegion = view.findViewById(R.id.etRegion);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        // Fetch arguments from bundle and set title
        getDialog().setTitle("Edit Target Language");
        etCity.setText(city);
        etRegion.setText(region);
        // Show soft keyboard automatically and request focus to field
        //etLang.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //listener.onUpdateSubmitted(false);
                dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.getCurrentUser().put("location", etCity.getText().toString().trim() +", "+ etRegion.getText().toString().trim());
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            dismiss();
                            listener.onLocationUpdate();
                        }
                    }
                });
            }
        });

    }
}