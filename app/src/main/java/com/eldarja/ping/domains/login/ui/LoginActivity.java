package com.eldarja.ping.domains.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;

import com.eldarja.ping.domains.chat.ui.ChatActivity;
import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.WeakRefApp;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.eldarja.ping.domains.login.dtos.AuthRequestDto;
import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private ProgressBar progressBarCallingCodes;
    private CircularProgressButton btnGetStarted;

    private AuthHubClient authHubClient;
    private String[] callingCodeStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHubClient = new AuthHubClient(this.onHubConnected, this.hubMessageHandlers);

        inputPhoneNumber = findViewById(R.id.inputGetStartedPhoneNumber);
        inputPhoneNumber.addTextChangedListener(_inputPhoneChangeHandler());

        spinnerCallingCodes = findViewById(R.id.spinnerGetStartedCallingCodes);
        progressBarCallingCodes = findViewById(R.id.progressBarGetStartedCallingCodes);

        btnGetStarted = (CircularProgressButton) findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> _clickGetStartedHandler());
    }

    private void _clickGetStartedHandler() {
        inputPhoneNumber.setEnabled(false);
        btnGetStarted.startAnimation(() -> null);
        AuthRequestDto request = new AuthRequestDto(inputPhoneNumber.getText().toString());
        try {
            authHubClient.send("RequestAuthentication", "rndGenCode", request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextWatcher _inputPhoneChangeHandler() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnGetStarted.setEnabled(s.length() > 5);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void _onCallingCodesReceived(CallingCodeDto[] callingCodeDtoList) {
        inputPhoneNumber.setEnabled(true);
        progressBarCallingCodes.setVisibility(View.INVISIBLE);
        callingCodeStrings = CallingCodeDto.toString(callingCodeDtoList);
        UiUtils.fillSpinner(this,
                callingCodeStrings,
                spinnerCallingCodes);
    }

    private void _onAuthenticationSuccess(AuthRequestDto msgJson) {
        Log.e("Tagx", "Resposne: " + msgJson);
//        MySession.setKorisnik(korisnik);
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    private void _onAuthenticationFailed() {
        UiUtils.dismissKeyboard(this);
        Intent registerActivityIntent = new Intent(WeakRefApp.getContext(), RegisterActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(RegisterActivity.CALLING_CODES_BUNDLE_KEY, callingCodeStrings);
        registerActivityIntent.putExtras(args);

        startActivity(registerActivityIntent);

        inputPhoneNumber.setEnabled(true);
        btnGetStarted.revertAnimation(() -> null);
    }

    private GenericAbstractRunnable<HubConnection> onHubConnected = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            exposedHubConnection.send("RequestCallingCodes", "rndGenCode");
        }
    };

    private GenericAbstractRunnable<HubConnection> hubMessageHandlers = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection)
        {
            exposedHubConnection.on("ResponseCallingCodesrndGenCode", (CallingCodeDto[] msgJson) -> {
                runOnUiThread(() -> _onCallingCodesReceived(msgJson));
            }, CallingCodeDto[].class);

            exposedHubConnection.on("AuthenticationDonerndGenCode", (AuthRequestDto msgJson)-> {
                runOnUiThread(() -> _onAuthenticationSuccess(msgJson));
            }, AuthRequestDto.class);

            exposedHubConnection.on("AuthenticationFailedrndGenCode", (String reasonMsg)-> {
                runOnUiThread(() -> _onAuthenticationFailed());
            }, String.class);
        }
    };
}
