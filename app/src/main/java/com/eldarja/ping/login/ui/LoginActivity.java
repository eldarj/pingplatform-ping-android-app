package com.eldarja.ping.login.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;

import com.eldarja.ping.helpers.MyAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.WeakRefApp;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.eldarja.ping.login.dtos.AuthRequestDto;
import com.eldarja.ping.login.dtos.CallingCodeDto;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private ProgressBar progressBarCallingCodes;
    private CircularProgressButton btnGetStarted;

    private AuthHubClient authHubClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHubClient = new AuthHubClient(this.onHubConnected, this.hubMessageHandler);
//        HubConnection hubConnection = buildHubConnection();

        inputPhoneNumber = findViewById(R.id.inputGetStartedPhoneNumber);
        inputPhoneNumber.addTextChangedListener(_inputPhoneChangeHandler());

        spinnerCallingCodes = findViewById(R.id.spinnerGetStartedCallingCodes);
        progressBarCallingCodes = findViewById(R.id.progressBarGetStartedCallingCodes);

        btnGetStarted = (CircularProgressButton) findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPhoneNumber.setEnabled(false);
                btnGetStarted.startAnimation(() -> null);
                AuthRequestDto request = new AuthRequestDto(inputPhoneNumber.getText().toString());
                try {
                    //hubConnection.send("RequestAuthentication", "rndGenCode", request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        UiUtils.fillSpinner(this,
                CallingCodeDto.toString(callingCodeDtoList),
                android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerCallingCodes);
    }

    private void _onAuthenticationSuccess(AuthRequestDto msgJson) {
        Log.e("Tagx", "Resposne: " + msgJson);
    }

    private void _onAuthenticationFailed() {
        startActivity(new Intent(WeakRefApp.getContext(), RegisterActivity.class));
        btnGetStarted.revertAnimation(() -> null);
    }

    private MyAbstractRunnable<HubConnection> onHubConnected = new MyAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection hubConnection) {
            hubConnection.send("RequestCallingCodes", "rndGenCode");
        }
    };

    private MyAbstractRunnable<HubConnection> hubMessageHandler = new MyAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection hubConnection) {

            hubConnection.on("ResponseCallingCodesrndGenCode", (CallingCodeDto[] msgJson) -> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Tagx", "Response: " + msgJson);
                        _onCallingCodesReceived(msgJson);
                    }
                });
            }, CallingCodeDto[].class);

            hubConnection.on("AuthenticationDonerndGenCode", (AuthRequestDto msgJson)-> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _onAuthenticationSuccess(msgJson);
                    }
                });
            }, AuthRequestDto.class);

            hubConnection.on("AuthenticationFailedrndGenCode", (String reasonMsg)-> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _onAuthenticationFailed();
                    }
                });
            }, String.class);
        }
    };

//    private HubConnection buildHubConnection() {
//        HubConnection hubConnection = HubConnectionBuilder.create("http://192.168.1.2:18458/authhub").build();
//        new HubConnectionTask(hubConnection, this.onHubConnected()).execute();
//
//
//        return hubConnection;
//    }
}
