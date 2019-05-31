package com.eldarja.ping.helpers.signalr.base;

import android.os.AsyncTask;

import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public abstract class BaseHubClient extends AsyncTask<Void, Void, Void> {
    private final static String BASE_HUB_GATEWAY_URL = "http://192.168.1.2:18458/";

    private String hubName;
    private HubConnection hubConnection;
    private GenericAbstractRunnable<HubConnection> onConnected;
    private GenericAbstractRunnable<HubConnection> hubMessageHandlers;

    public BaseHubClient(String hubName,
                         GenericAbstractRunnable<HubConnection> onConnected,
                         GenericAbstractRunnable<HubConnection> hubMessageHandlers) {
        this.hubName = hubName;
        this.onConnected = onConnected;
        this.hubMessageHandlers = hubMessageHandlers;

        // Build the HubConnection
        this.hubConnection = HubConnectionBuilder.create(BASE_HUB_GATEWAY_URL + this.hubName).build();

        // Execute the Message (SignalR .on()) Handler(s) runnable
        if (this.hubMessageHandlers != null) {
            this.hubMessageHandlers.run(hubConnection);
        }

        // Execute the Connect task & the onConnected runnable
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (this.onConnected != null) {
            this.hubConnection.start().doOnComplete(() -> this.onConnected.run(this.hubConnection)).blockingAwait();
        } else {
            this.hubConnection.start().blockingAwait();
        }
        return null;
    }

    // Send method exposed
    public void send(String method, Object... args) {
        this.hubConnection.send(method, args);
    }
}
