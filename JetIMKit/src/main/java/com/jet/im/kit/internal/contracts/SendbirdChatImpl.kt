package com.jet.im.kit.internal.contracts

import android.content.Context
import com.jet.im.JetIM
import com.jet.im.JetIMConst
import com.jet.im.interfaces.IConnectionManager.IConnectionStatusListener
import com.sendbird.android.AppInfo
import com.sendbird.android.ConnectionState
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.AuthenticationHandler
import com.sendbird.android.handler.BaseChannelHandler
import com.sendbird.android.handler.CompletionHandler
import com.sendbird.android.handler.ConnectHandler
import com.sendbird.android.handler.ConnectionHandler
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.handler.UIKitConfigurationHandler
import com.sendbird.android.internal.sb.SendbirdSdkInfo
import com.sendbird.android.params.InitParams
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.android.user.User

internal class SendbirdChatImpl : SendbirdChatContract {

    override fun addChannelHandler(identifier: String, handler: BaseChannelHandler) {
        SendbirdChat.addChannelHandler(identifier, handler)
    }

    override fun addConnectionHandler(identifier: String, handler: ConnectionHandler) {
        SendbirdChat.addConnectionHandler(identifier, handler)
    }

    override fun removeChannelHandler(identifier: String): BaseChannelHandler? =
        SendbirdChat.removeChannelHandler(identifier)

    override fun removeConnectionHandler(identifier: String): ConnectionHandler? =
        SendbirdChat.removeConnectionHandler(identifier)

    override fun init(context: Context, params: InitParams, handler: InitResultHandler) {
        JetIM.getInstance().init(context, "appkey")
        SendbirdChat.init(params, handler)
    }

    private val TOKEN1 = "CgZhcHBrZXkaIDAr072n8uOcw5YBeKCcQ+QCw4m6YWhgt99U787/dEJS"
    private val TOKEN2 = "CgZhcHBrZXkaINodQgLnbhTbt0SzC8b/JFwjgUAdIfUZTEFK8DvDLgM1"
    private val TOKEN3 = "CgZhcHBrZXkaINMDzs7BBTTZTwjKtM10zyxL4DBWFuZL6Z/OAU0Iajpv"
    private val TOKEN4 = "CgZhcHBrZXkaIDHZwzfny4j4GiJye8y8ehU5fpJ+wVOGI3dCsBMfyLQv"
    private val TOKEN5 = "CgZhcHBrZXkaIOx2upLCsmsefp8U/KNb52UGnAEu/xf+im3QaUd0HTC2"

    private var mUser: User? = null;
    override fun connect(userId: String, accessToken: String?, handler: ConnectHandler?) {
        val innerHandler = object : ConnectHandler {
            override fun onConnected(user: User?, e: SendbirdException?) {
                mUser = user
                val listener = object : IConnectionStatusListener {
                    override fun onStatusChange(status: JetIMConst.ConnectionStatus?, code: Int) {
                        if (status == JetIMConst.ConnectionStatus.CONNECTED) {
                            handler?.onConnected(user, null);
                            JetIM.getInstance().connectionManager.removeConnectionStatusListener("kit")
                        } else if (status == JetIMConst.ConnectionStatus.FAILURE) {
                            handler?.onConnected(null, null);
                            JetIM.getInstance().connectionManager.removeConnectionStatusListener("kit")
                        }
                    }

                    override fun onDbOpen() {
//                        TODO("Not yet implemented")
                    }
                }
                JetIM.getInstance().connectionManager.addConnectionStatusListener("kit", listener)
                JetIM.getInstance().connectionManager.connect(TOKEN4)
            }
        }
        SendbirdChat.connect("yuto", null, innerHandler)
    }

    override fun updateCurrentUserInfo(params: UserUpdateParams, handler: CompletionHandler?) {
        SendbirdChat.updateCurrentUserInfo(params, handler)
    }

    override fun addExtension(key: String, version: String) {
        SendbirdChat.addExtension(key, version)
    }

    override fun addSendbirdExtensions(
        extensions: List<SendbirdSdkInfo>,
        customData: Map<String, String>?
    ) {
        SendbirdChat.addSendbirdExtensions(extensions, customData)
    }

    override fun getAppInfo(): AppInfo? = SendbirdChat.appInfo

    override fun getConnectionState(): ConnectionState = SendbirdChat.connectionState

    override fun getUIKitConfiguration(handler: UIKitConfigurationHandler?) {
        SendbirdChat.getUIKitConfiguration(handler)
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
//        SendebirdChat.authenticateFeed(userId, accessToken, apiHost, handler)
    }
}
