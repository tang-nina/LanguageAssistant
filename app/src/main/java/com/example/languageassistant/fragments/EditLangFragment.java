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
 * Use the {@link EditLangFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditLangFragment extends DialogFragment {

    private EditText etLang;
    private MaterialButton btnCancel;
    private MaterialButton btnSave;

    public interface OnItemSelectedListener {
        public void onUpdateSubmitted();
    }

    private OnItemSelectedListener listener;

    public EditLangFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditLangFragment newInstance(String curLang) {
        EditLangFragment frag = new EditLangFragment();
        Bundle args = new Bundle();
        args.putString("curLang", curLang);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_lang, container);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etLang = (EditText) view.findViewById(R.id.etLang);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        // Fetch arguments from bundle and set title
        getDialog().setTitle("Edit Target Language");
        String curLang = getArguments().getString("curLang");
        etLang.setText(curLang);
        // Show soft keyboard automatically and request focus to field
        etLang.requestFocus();
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
                ParseUser.getCurrentUser().put("targetLanguage", etLang.getText().toString());
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            dismiss();
                            listener.onUpdateSubmitted();
                        }
                    }
                });
            }
        });

    }
}