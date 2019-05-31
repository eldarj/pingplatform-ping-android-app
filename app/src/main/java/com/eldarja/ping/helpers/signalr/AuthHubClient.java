package com.eldarja.ping.helpers.signalr;

import com.eldarja.ping.helpers.GenericAbstractRunnable;
import com.eldarja.ping.helpers.signalr.base.BaseHubClient;
import com.microsoft.signalr.HubConnection;

public class AuthHubClient extends BaseHubClient {
    private final static String HUB_GATEWAY_NAME = "authhub";

    public AuthHubClient(GenericAbstractRunnable<HubConnection> onConnected,
                         GenericAbstractRunnable<HubConnection> hubMessageHandlers) {
        super(HUB_GATEWAY_NAME, onConnected, hubMessageHandlers);
    }
}
