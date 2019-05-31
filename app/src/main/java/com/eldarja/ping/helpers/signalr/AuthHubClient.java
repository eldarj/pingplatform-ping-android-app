package com.eldarja.ping.helpers.signalr;

import com.eldarja.ping.helpers.MyAbstractRunnable;
import com.eldarja.ping.helpers.signalr.base.BaseHubClient;
import com.microsoft.signalr.HubConnection;

public class AuthHubClient extends BaseHubClient {
    private final static String HUB_GATEWAY_NAME = "authhub";

    public AuthHubClient(MyAbstractRunnable<HubConnection> onConnected,
                         MyAbstractRunnable<HubConnection> hubMessageHandlers) {
        super(HUB_GATEWAY_NAME, onConnected, hubMessageHandlers);
    }
}
