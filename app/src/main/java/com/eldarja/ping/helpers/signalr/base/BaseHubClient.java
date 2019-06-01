package com.eldarja.ping.helpers.signalr.base;

import android.os.AsyncTask;
import android.util.Log;

import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseHubClient extends AsyncTask<Void, Void, GenericAbstractRunnable<HubConnection>> {
    private final static String BASE_HUB_GATEWAY_URL = "http://192.168.1.2:18458/";

    private String hubName;
    private HubConnection hubConnection;
    private GenericAbstractRunnable<HubConnection> onConnected;
    private GenericAbstractRunnable<HubConnection> onCouldntConnect;
    private GenericAbstractRunnable<HubConnection> hubMessageHandlers;

    public BaseHubClient(String hubName,
                         GenericAbstractRunnable<HubConnection> onConnected,
                         GenericAbstractRunnable<HubConnection> onCouldntConnect,
                         GenericAbstractRunnable<HubConnection> hubMessageHandlers) {
        this.hubName = hubName;
        this.onConnected = onConnected;
        this.onCouldntConnect = onCouldntConnect;
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

    // Send method exposed
    public void send(String method, Object... args) {
        this.hubConnection.send(method, args);
    }

    @Override
    protected GenericAbstractRunnable<HubConnection> doInBackground(Void... params) {
        AtomicReference<GenericAbstractRunnable<HubConnection>> runnableRef = new AtomicReference<>(null);
        try {
            this.hubConnection.start().doOnComplete(() -> {
                if (this.onConnected != null) {
                    runnableRef.set(this.onConnected);
                }
            }).blockingAwait();
        } catch (Exception e) {
            // connection was refused or interrupted while connecting
            runnableRef.set(this.onCouldntConnect);
        }

        return runnableRef.get();
    }

    @Override
    protected void onPostExecute(GenericAbstractRunnable<HubConnection> runnable) {
        runnable.run(this.hubConnection);
    }
}
