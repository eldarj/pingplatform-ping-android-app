package com.eldarja.ping.domains.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;

import com.eldarja.ping.commons.dtos.ResponseDto;
import com.eldarja.ping.domains.chat.ui.ChatActivity;
import com.eldarja.ping.domains.login.dtos.AccountDto;
import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.WeakRefApp;
import com.eldarja.ping.helpers.session.SharedPrefSession;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.eldarja.ping.domains.login.dtos.AuthRequestDto;
import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.google.android.gms.common.api.Response;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private ProgressBar progressBarCallingCodes;
    private CircularProgressButton btnGetStarted;

    private AuthHubClient authHubClient;
    private CallingCodeDto[] callingCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initHubConnection();

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
            authHubClient.send("RequestAuthentication", request);
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

    private void _onCallingCountryCodesReceived(CallingCodeDto[] callingCodeDtoList) {
        inputPhoneNumber.setEnabled(true);
        progressBarCallingCodes.setVisibility(View.INVISIBLE);
        callingCodes = callingCodeDtoList;

        UiUtils.fillSpinner(this,
                CallingCodeDto.toString(callingCodeDtoList),
                spinnerCallingCodes);
    }

    private void _onAuthenticationSuccess(AccountDto dto) {
        SharedPrefSession.setUser(dto);
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    private void _onAuthenticationFailed(ResponseDto responseDto) {
        UiUtils.dismissKeyboard(this);
        Intent registerActivityIntent = new Intent(WeakRefApp.getContext(), RegisterActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(RegisterActivity.CALLING_CODES_BUNDLE_KEY, callingCodes);
        args.putString(RegisterActivity.REGISTRATION_PHONE_NUMBER_BUNDLE_KEY, inputPhoneNumber.getText().toString());
        registerActivityIntent.putExtras(args);

        startActivity(registerActivityIntent);
        finish();
    }

    private void initHubConnection() {
        authHubClient = new AuthHubClient(onHubConnected, onHubCouldntConnect, hubMessageHandlers);
    }

    private GenericAbstractRunnable<HubConnection> onHubConnected = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            exposedHubConnection.send("RequestCallingCodes", "rndGenCode");
        }
    };

    private GenericAbstractRunnable<HubConnection> onHubCouldntConnect = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            progressBarCallingCodes.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.couldnt_connect_to_hub), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBarCallingCodes.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(LoginActivity.this::initHubConnection, 500);
                    }
                })
                .show();
        }
    };

    private GenericAbstractRunnable<HubConnection> hubMessageHandlers = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection)
        {
            exposedHubConnection.on("ResponseCallingCodesrndGenCode", (CallingCodeDto[] response) -> {
                runOnUiThread(() -> _onCallingCountryCodesReceived(response));
            }, CallingCodeDto[].class);

            exposedHubConnection.on("AuthenticationDonerndGenCode", (AccountDto responseDto)-> {
                runOnUiThread(() -> _onAuthenticationSuccess(responseDto));
            }, AccountDto.class);

            exposedHubConnection.on("AuthenticationFailed", (ResponseDto responseDto)-> {
                runOnUiThread(() -> _onAuthenticationFailed(responseDto));
            }, ResponseDto.class);
        }
    };
}
