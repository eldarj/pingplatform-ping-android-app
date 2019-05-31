package com.eldarja.ping.login.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText inputFirstname;
    private TextInputEditText inputLastname;
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private ProgressBar progressBarCallingCodes;
    private CircularProgressButton btnGetStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }
}
