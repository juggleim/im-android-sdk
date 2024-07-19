package com.jet.im.interfaces;

import com.jet.im.JetIMConst;
import com.jet.im.push.PushChannel;

public interface IConnectionManager {
    void connect(String token);

    void disconnect(boolean receivePush);

    void registerPushToken(PushChannel channel, String token);

    JetIMConst.ConnectionStatus getConnectionStatus();

    void addConnectionStatusListener(String key, IConnectionStatusListener listener);

    JetIMConst.ConnectionStatus getConnectionStatus();
    void removeConnectionStatusListener(String key);

    interface IConnectionStatusListener {
        void onStatusChange(JetIMConst.ConnectionStatus status, int code, String extra);

        void onDbOpen();

        void onDbClose();
    }
}


