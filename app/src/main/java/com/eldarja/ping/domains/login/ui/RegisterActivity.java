package com.eldarja.ping.domains.login.ui;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;
import com.eldarja.ping.helpers.UiUtils;
import com.google.android.material.textfield.TextInputEditText;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    public final static String CALLING_CODES_BUNDLE_KEY = "CallingCodesBundleKey";

    private TextInputEditText inputFirstname;
    private TextInputEditText inputLastname;
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private CircularProgressButton btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle args = getIntent().getExtras();
        String[] callingCodeStrings = args.getStringArray(CALLING_CODES_BUNDLE_KEY);

        spinnerCallingCodes = findViewById(R.id.spinnerRegisterCallingCodes);
        UiUtils.fillSpinner(this,
                callingCodeStrings,
                spinnerCallingCodes);

        inputFirstname = findViewById(R.id.inputRegisterFirstname);
        inputLastname = findViewById(R.id.inputRegisterLastname);
        inputPhoneNumber = findViewById(R.id.inputRegisterPhoneNumber);

        btnRegister = (CircularProgressButton) findViewById(R.id.btnRegister);
    }
}
