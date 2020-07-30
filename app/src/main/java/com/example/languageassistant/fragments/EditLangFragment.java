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

import com.example.languageassistant.Keys;
import com.example.languageassistant.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A pop up fragment to edit the target language of the user.
 */
public class EditLangFragment extends DialogFragment {

    //interface to communicate to the main activity
    public interface OnItemSelectedListener {
        public void onUpdateSubmitted();
    }

    private EditText etLang;
    private MaterialButton btnCancel;
    private MaterialButton btnSave;

    private OnItemSelectedListener listener;

    public EditLangFragment() {
        // Empty constructor is required for DialogFragment
    }

    //factory method to get new instance of this fragment and communicate arguments
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
        etLang = (EditText) view.findViewById(R.id.etLang);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        // Set title and current target language
        getDialog().setTitle(getContext().getString(R.string.edit_lang));
        String curLang = getArguments().getString("curLang");
        etLang.setText(curLang);
        // Show soft keyboard automatically and request focus to field
        etLang.requestFocus();
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
                //update language info of current user
                ParseUser.getCurrentUser().put(Keys.KEY_TARGET_LANG, etLang.getText().toString());
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            dismiss();
                            listener.onUpdateSubmitted();
                        }else{
                            Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}