package com.eldarja.ping.helpers.signalr;

import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.signalr.base.BaseHubClient;
import com.microsoft.signalr.HubConnection;

public class ChatHubClient extends BaseHubClient {
    private final static String HUB_GATEWAY_NAME = "chathub";

    public ChatHubClient(GenericAbstractRunnable<HubConnection> onConnected,
                         GenericAbstractRunnable<HubConnection> onCouldntConnect,
                         GenericAbstractRunnable<HubConnection> hubMessageHandlers) {
        super(HUB_GATEWAY_NAME, onConnected, onCouldntConnect, hubMessageHandlers);
    }
}
