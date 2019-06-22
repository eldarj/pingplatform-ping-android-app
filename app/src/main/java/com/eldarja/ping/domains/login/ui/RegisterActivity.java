package com.eldarja.ping.domains.login.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eldarja.ping.R;
import com.eldarja.ping.domains.chat.ui.ChatActivity;
import com.eldarja.ping.domains.login.dtos.AccountDto;
import com.eldarja.ping.domains.login.dtos.AuthRequestDto;
import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.contacts.ContactReader;
import com.eldarja.ping.helpers.session.SharedPrefSession;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.signalr.HubConnection;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    public final static String CALLING_CODES_BUNDLE_KEY = "CallingCodesBundleKey";
    public final static String REGISTRATION_PHONE_NUMBER_BUNDLE_KEY = "RegisterPhoneNumber";

    private final static int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private TextInputEditText inputFirstname;
    private TextInputEditText inputLastname;
    private TextInputEditText inputPhoneNumber;
    private Spinner spinnerCallingCodes;
    private CircularProgressButton btnRegister;

    private AuthHubClient authHubClient;

    private AuthRequestDto registrationRequestDto = new AuthRequestDto();
    private CallingCodeDto[] callingCodes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerCallingCodes = findViewById(R.id.spinnerRegisterCallingCodes);
        inputFirstname = findViewById(R.id.inputRegisterFirstname);
        inputLastname = findViewById(R.id.inputRegisterLastname);
        inputPhoneNumber = findViewById(R.id.inputRegisterPhoneNumber);

        btnRegister = (CircularProgressButton) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> _clickRegisterHandler());

        Bundle args = getIntent().getExtras();
        if (args != null) {
            callingCodes = (CallingCodeDto[]) args.getSerializable(CALLING_CODES_BUNDLE_KEY);
            String phoneNumber = args.getString(REGISTRATION_PHONE_NUMBER_BUNDLE_KEY);

            inputPhoneNumber.setText(phoneNumber);
            UiUtils.fillSpinner(this,
                    CallingCodeDto.toString(callingCodes),
                    spinnerCallingCodes);
        }
    }

    private void _clickRegisterHandler() {
        btnRegister.startAnimation(() -> null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
            dlgBuilder.setTitle(getString(R.string.contacts_permission_rationale_title))
                    .setMessage(getString(R.string.contacts_permission_rationale_body))
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            initHubConnectionAndSubmit();
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSIONS_REQUEST_READ_CONTACTS); // we'll catch the callback in onRequestPermissionsResult()
                        }
                    })
                    .show();
        } else {
            registrationRequestDto.setContacts(ContactReader.GetDeviceContacts(getContentResolver(), callingCodes));
            initHubConnectionAndSubmit();
        }
    }

    private void _onRegistrationSuccess(AccountDto dto) {
        SharedPrefSession.setUser(dto);
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    private void _onRegistrationFailed(String reasonMsg) {
        Log.e("Tagx", "Registration failed: " + reasonMsg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted - proceed to read the file and submit registration
                registrationRequestDto.setContacts(ContactReader.GetDeviceContacts(getContentResolver(), callingCodes));
            }
            initHubConnectionAndSubmit();
        }
    }

    private void initHubConnectionAndSubmit() {
        authHubClient = new AuthHubClient(onHubConnected, onHubCouldntConnect, hubMessageHandlers);
    }

    private GenericAbstractRunnable<HubConnection> onHubConnected = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            String selectedItemString = (String) spinnerCallingCodes.getSelectedItem();
            int selectedCallingCode = Integer.parseInt(selectedItemString.substring(1, selectedItemString.indexOf(' ')));

            registrationRequestDto.setPhoneNumber(inputPhoneNumber.getText().toString());
            registrationRequestDto.setCallingCountryCode(selectedCallingCode);
            registrationRequestDto.setFirstname(inputFirstname.getText().toString());
            registrationRequestDto.setLastname(inputLastname.getText().toString());

            Log.e("Tagx", "Request registration for country calling code: " + selectedCallingCode); //TODO: Remove
            exposedHubConnection.send("RequestRegistration", registrationRequestDto);
        }
    };

    private GenericAbstractRunnable<HubConnection> hubMessageHandlers = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection)
        {
            exposedHubConnection.on("RegistrationDonerndGenCode", (AccountDto responseDto) -> {
                runOnUiThread(() -> _onRegistrationSuccess(responseDto));
            }, AccountDto.class);

            exposedHubConnection.on("RegistrationFailedrndGenCode", (String reasonMsg)-> {
                runOnUiThread(() -> _onRegistrationFailed(reasonMsg));
            }, String.class);
        }
    };

    private GenericAbstractRunnable<HubConnection> onHubCouldntConnect = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            new Handler().postDelayed(() -> {
                btnRegister.revertAnimation(() -> null);
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.couldnt_connect_to_hub),
                        Snackbar.LENGTH_LONG)
                        .show();
            }, 1000);
        }
    };
}
