package com.eldarja.ping.helpers.signalr.base;

import android.os.AsyncTask;

import com.eldarja.ping.helpers.MyAbstractRunnable;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public abstract class BaseHubClient extends AsyncTask<Void, Void, Void> {
    class HubConnectionTask extends AsyncTask<Void, Void, Void> {
        HubConnection hubConnection;
        MyAbstractRunnable<HubConnection> onConnected;

        private HubConnectionTask(HubConnection hubConnection, MyAbstractRunnable<HubConnection> onConnected) {
            this.hubConnection = hubConnection;
            this.onConnected = onConnected;
        }

        @Override
        protected Void doInBackground(Void... hubConnections) {
            if (this.onConnected != null) {
                this.hubConnection.start().doOnComplete(() -> this.onConnected.run(this.hubConnection)).blockingAwait();
            } else {
                this.hubConnection.start().blockingAwait();
            }
            return null;
        }
    }

    private final static String BASE_HUB_GATEWAY_URL = "http://192.168.1.2:18458/";

    private String hubName;
    private MyAbstractRunnable<HubConnection> onConnected;
    private MyAbstractRunnable<HubConnection> hubMessageHandlers;

    public BaseHubClient(String hubName,
                         MyAbstractRunnable<HubConnection> onConnected,
                         MyAbstractRunnable<HubConnection> hubMessageHandlers) {
        this.hubName = hubName;
        this.onConnected = onConnected;
        this.hubMessageHandlers = hubMessageHandlers;

        build();
    }

    private void build() {
        // Create the HubConnection
        HubConnection hubConnection = HubConnectionBuilder.create(BASE_HUB_GATEWAY_URL + this.hubName).build();

        // Execute the Message Handler runnable
        if (this.hubMessageHandlers != null) {
            this.hubMessageHandlers.run(hubConnection);
        }

        // Execute the Connect task & the onConnected runnable
        new HubConnectionTask(hubConnection, this.onConnected).execute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        
        return null;
    }
}
