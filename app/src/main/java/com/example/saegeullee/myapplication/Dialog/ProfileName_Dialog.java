package com.example.saegeullee.myapplication.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;

public class ProfileName_Dialog extends DialogFragment {

    private static final String TAG = "ProfileName_Dialog";

    public interface OnInputListener {
        void sendInput(String input);
    }

    public OnInputListener mOnInputListener;

    //widgets
    private EditText mInput;
    private TextView mActionOk, mActionCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_accontsetting_profilename, container, false);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        mInput = view.findViewById(R.id.inputName);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mInput.getText().toString();
                if(!TextUtils.isEmpty(input)) {

                    /**
                     * It's easiest way, but when you have complicated app you better use Interface.
                     */
//                    ((AccountSettingActivity)getActivity()).mProfileName.setText(input);
//                    getDialog().dismiss();

                    /**
                     * Best practice
                     */
                    mOnInputListener.sendInput(input);
                    getDialog().dismiss();

                }
            }
        });

        return view;
    }

    /**
     * You can get Null pointer Exception, if you don't do this onAttach()
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch(ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());

        }
    }
}
