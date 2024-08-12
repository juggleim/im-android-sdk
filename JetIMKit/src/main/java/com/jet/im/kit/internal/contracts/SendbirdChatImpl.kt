package com.jet.im.kit.internal.contracts

import android.content.Context
import com.jet.im.JetIM
import com.jet.im.JetIMConst
import com.jet.im.interfaces.IConnectionManager.IConnectionStatusListener
import com.jet.im.interfaces.IMessageManager
import com.jet.im.kit.SendbirdUIKit
import com.sendbird.android.AppInfo
import com.sendbird.android.ConnectionState
import com.sendbird.android.handler.AuthenticationHandler
import com.sendbird.android.handler.CompletionHandler
import com.sendbird.android.handler.ConnectionHandler
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.handler.UIKitConfigurationHandler
import com.sendbird.android.internal.sb.SendbirdSdkInfo
import com.sendbird.android.params.InitParams
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.android.user.User

internal class SendbirdChatImpl : SendbirdChatContract {

    override fun addChannelHandler(identifier: String, handler: IMessageManager.IMessageListener) {
        JetIM.getInstance().messageManager.addListener(identifier, handler)
    }

    override fun removeChannelHandler(identifier: String) {
        JetIM.getInstance().messageManager.removeListener(identifier)
    }


    override fun init(context: Context, params: InitParams, handler: InitResultHandler) {
//        SendbirdChat.init(params, handler)
    }

    private var mUser: User? = null;
    override fun connect(
        userId: String,
        accessToken: String?,
        handler: com.jet.im.kit.interfaces.ConnectHandler?
    ) {
        val listener = object : IConnectionStatusListener {
            override fun onStatusChange(
                status: JetIMConst.ConnectionStatus?,
                code: Int,
                extra: String
            ) {
                if (status == JetIMConst.ConnectionStatus.CONNECTED) {
                    handler?.onConnected(null);
                    JetIM.getInstance().connectionManager.removeConnectionStatusListener("kit")
                } else if (status == JetIMConst.ConnectionStatus.FAILURE) {
                    handler?.onConnected(RuntimeException());
                    JetIM.getInstance().connectionManager.removeConnectionStatusListener("kit")
                }
            }

            override fun onDbOpen() {
//                        TODO("Not yet implemented")
            }

            override fun onDbClose() {
//                        TODO("Not yet implemented")
            }
        }
        JetIM.getInstance().connectionManager.addConnectionStatusListener("kit", listener)
        JetIM.getInstance().connectionManager.connect(SendbirdUIKit.token)
    }

    override fun updateCurrentUserInfo(params: UserUpdateParams, handler: CompletionHandler?) {
    }

    override fun addExtension(key: String, version: String) {
    }

    override fun addSendbirdExtensions(
        extensions: List<SendbirdSdkInfo>,
        customData: Map<String, String>?
    ) {
    }




    override fun authenticateFeed(
        userId: String,
        accessToken: String?,
        apiHost: String?,
        handler: AuthenticationHandler?
    ) {
        val connectionStatus = JetIM.getInstance().connectionManager.connectionStatus;
        if (connectionStatus == JetIMConst.ConnectionStatus.CONNECTED) {
            handler?.onAuthenticated(mUser, null);
        } else {
            handler?.onAuthenticated(null, null);
        }
    }
}
