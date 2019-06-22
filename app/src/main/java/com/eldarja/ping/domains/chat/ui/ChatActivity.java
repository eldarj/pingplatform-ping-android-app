package com.eldarja.ping.domains.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eldarja.ping.R;
import com.eldarja.ping.domains.chat.adapters.ContactsRecyclerAdapter;
import com.eldarja.ping.domains.login.dtos.AccountDto;
import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.eldarja.ping.domains.login.dtos.ContactDto;
import com.eldarja.ping.domains.login.ui.LoginActivity;
import com.eldarja.ping.domains.login.ui.RegisterActivity;
import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.UiUtils;
import com.eldarja.ping.helpers.WeakRefApp;
import com.eldarja.ping.helpers.session.SharedPrefSession;
import com.eldarja.ping.helpers.signalr.AuthHubClient;
import com.eldarja.ping.helpers.signalr.ChatHubClient;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.signalr.HubConnection;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ProgressBar progressBarContacts;
    private TextView textContactsNoData;

    private ContactDto[] contacts;
    private ContactDto[] initialContacts;

    private RecyclerView recyclerViewContacts;
    private RecyclerView.Adapter contactsAdapter;
    private RecyclerView.LayoutManager contactsLayoutManager;

    private ChatHubClient chatHubClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initHubConnection();

        progressBarContacts = findViewById(R.id.progressBarContacts);
        textContactsNoData = findViewById(R.id.textContactsEmpty);

        initialContacts = contacts = SharedPrefSession.getContacts();

        recyclerViewContacts = (RecyclerView) findViewById(R.id.recyclerViewContacts);
        recyclerViewContacts.setHasFixedSize(true);

        contactsLayoutManager = new LinearLayoutManager(this);
        recyclerViewContacts.setLayoutManager(contactsLayoutManager);

        contactsAdapter = new ContactsRecyclerAdapter(contacts);
        recyclerViewContacts.setAdapter(contactsAdapter);
    }

    private void initHubConnection() {
        chatHubClient = new ChatHubClient(onHubConnected, onHubCouldntConnect, hubMessageHandlers);
    }

    private GenericAbstractRunnable<HubConnection> onHubConnected = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
        exposedHubConnection.send("RequestContacts");
        }
    };

    private GenericAbstractRunnable<HubConnection> onHubCouldntConnect = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
        progressBarContacts.setVisibility(View.INVISIBLE);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.couldnt_connect_to_hub), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarContacts.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(ChatActivity.this::initHubConnection, 500);
                }
            })
            .show();
        }
    };

    private GenericAbstractRunnable<HubConnection> hubMessageHandlers = new GenericAbstractRunnable<HubConnection>() {
        @Override
        public void run(HubConnection exposedHubConnection) {
            exposedHubConnection.on("RequestContactsSuccessrndGenCode", (ContactDto[] responseDto)-> {
                runOnUiThread(() -> _onAuthenticationSuccess(responseDto));
            }, ContactDto[].class);

            exposedHubConnection.on("RequestContactsFailrndGenCode", (String reasonMsg)-> {
                runOnUiThread(() -> _onAuthenticationFailed());
            }, String.class);
        }
    };

    private void _onAuthenticationSuccess(ContactDto[] contactsResponse) {
        SharedPrefSession.setContacts(contacts);
        initialContacts = contacts = contactsResponse;
        recyclerViewContacts.getAdapter().notifyDataSetChanged();
    }

    private void _onAuthenticationFailed() {
    }
}
