package com.eldarja.ping.domains.login.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;
import com.eldarja.ping.domains.login.dtos.AuthRequestDto;
import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    public final static String CALLING_CODES_BUNDLE_KEY = "CallingCodesBundleKey";
    public final static String REGISTRATION_PHONE_NUMBER_BUNDLE_KEY = "RegisterPhoneNumber";

    private TextInputEditText inputFirstname;
    private TextInputEditText inputLastname;
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private CircularProgressButton btnRegister;

    private AuthHubClient authHubClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authHubClient = new AuthHubClient(null, this.hubMessageHandlers);

        spinnerCallingCodes = findViewById(R.id.spinnerRegisterCallingCodes);
        inputFirstname = findViewById(R.id.inputRegisterFirstname);
        inputLastname = findViewById(R.id.inputRegisterLastname);
        inputPhoneNumber = findViewById(R.id.inputRegisterPhoneNumber);

        btnRegister = (CircularProgressButton) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> _clickRegisterHandler());

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String[] callingCodeStrings = args.getStringArray(CALLING_CODES_BUNDLE_KEY);
            String phoneNumber = args.getString(REGISTRATION_PHONE_NUMBER_BUNDLE_KEY);

            inputPhoneNumber.setText(phoneNumber);
            UiUtils.fillSpinner(this,
                    callingCodeStrings,
                    spinnerCallingCodes);
        }
    }

    private void _clickRegisterHandler() {
        btnRegister.startAnimation(() -> null);
        int selectedCallingCode = 0;

        try {
            String selectedItemString = (String)spinnerCallingCodes.getSelectedItem();

            selectedCallingCode = Integer.parseInt(selectedItemString.substring(1, selectedItemString.indexOf(' ')));
        } catch (Exception e) {
            Log.e("Tagx", "Thrown: " + e.getMessage());
        }

        AuthRequestDto request = new AuthRequestDto(inputPhoneNumber.getText().toString(),
                selectedCallingCode,
                inputFirstname.getText().toString(),
                inputLastname.getText().toString());
        try {
            authHubClient.send("RequestRegistration", "rndGenCode", request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _onRegistrationSuccess(AuthRequestDto dto) {
        Log.e("Tagx", "Registration failed: " + dto);
    }

    private void _onRegistrationFailed(String reasonMsg) {
        Log.e("Tagx", "Registration failed: " + reasonMsg);
    }

    private GenericAbstractRunnable<HubConnection> hubMessageHandlers = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection)
        {
            exposedHubConnection.on("RegistrationDonerndGenCode", (AuthRequestDto responseDto) -> {
                runOnUiThread(() -> _onRegistrationSuccess(responseDto));
            }, AuthRequestDto.class);

            exposedHubConnection.on("RegistrationFailedrndGenCode", (String reasonMsg)-> {
                runOnUiThread(() -> _onRegistrationFailed(reasonMsg));
            }, String.class);
        }
    };
}
