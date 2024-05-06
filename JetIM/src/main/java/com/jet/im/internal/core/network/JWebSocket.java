package com.jet.im.internal.core.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.jet.im.JetIMConst;
import com.jet.im.internal.ConstInternal;
import com.jet.im.internal.HeartBeatManager;
import com.jet.im.internal.model.ConcreteMessage;
import com.jet.im.internal.util.JUtility;
import com.jet.im.model.Conversation;
import com.jet.im.model.MessageContent;
import com.jet.im.push.PushChannel;
import com.jet.im.utils.LoggerUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class JWebSocket extends WebSocketClient {

    public static URI createWebSocketUri(String server) {
        String webSocketUrl = WEB_SOCKET_PREFIX + server + WEB_SOCKET_SUFFIX;
        return URI.create(webSocketUrl);
    }

    public JWebSocket(String appKey, String token, URI serverUri, Context context) {
        super(serverUri);
        mAppKey = appKey;
        mToken = token;
        mContext = context;
        mPbData = new PBData();
        mCmdCallbackMap = new ConcurrentHashMap<>();
    }

    public void disconnect(Boolean receivePush) {
        sendDisconnectMsg(receivePush);
    }

    public void setConnectionListener(IWebSocketConnectListener listener) {
        mConnectListener = listener;
    }

    public void setMessageListener(IWebSocketMessageListener listener) {
        mMessageListener = listener;
    }

    public void sendIMMessage(MessageContent content,
                              Conversation conversation,
                              String clientUid,
                              List<ConcreteMessage> mergedMsgList,
                              boolean isBroadcast,
                              String userId,
                              SendMessageCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.sendMessageData(content.getContentType(),
                content.encode(),
                content.getFlags(),
                clientUid,
                mergedMsgList,
                isBroadcast,
                userId,
                mCmdIndex++,
                conversation.getConversationType(),
                conversation.getConversationId(),
                content.getMentionInfo());
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void recallMessage(String messageId,
                              Conversation conversation,
                              long timestamp,
                              WebSocketTimestampCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.recallMessageData(messageId, conversation, timestamp, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void syncConversations(long startTime,
                                  int count,
                                  String userId,
                                  SyncConversationsCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.syncConversationsData(startTime, count, userId, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void syncMessages(long receiveTime,
                             long sendTime,
                             String userId) {
        byte[] bytes = mPbData.syncMessagesData(receiveTime, sendTime, userId, mCmdIndex++);
        sendWhenOpen(bytes);
    }

    public void sendReadReceipt(Conversation conversation,
                                List<String> messageIds,
                                WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.sendReadReceiptData(conversation, messageIds, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void getGroupMessageReadDetail(Conversation conversation,
                                          String messageId,
                                          QryReadDetailCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.getGroupMessageReadDetail(conversation, messageId, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void deleteConversationInfo(Conversation conversation,
                                       String userId,
                                       WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.deleteConversationData(conversation, userId, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void clearUnreadCount(Conversation conversation,
                                 String userId,
                                 long msgIndex,
                                 WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.clearUnreadCountData(conversation, userId, msgIndex, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void clearTotalUnreadCount(String userId, long time, WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.clearTotalUnreadCountData(userId, time, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void queryHisMsg(Conversation conversation, long startTime, int count, JetIMConst.PullDirection direction, QryHisMsgCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.queryHisMsgData(conversation, startTime, count, direction, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void queryHisMsgByIds(Conversation conversation, List<String> messageIds, QryHisMsgCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.queryHisMsgDataByIds(conversation, messageIds, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void setMute(Conversation conversation, boolean isMute, String userId, WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.disturbData(conversation, userId, isMute, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void setTop(Conversation conversation, boolean isTop, String userId, WebSocketTimestampCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.topConversationData(conversation, userId, isTop, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void getMergedMessageList(String messageId,
                                     long timestamp,
                                     int count,
                                     JetIMConst.PullDirection direction,
                                     QryHisMsgCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.getMergedMessageList(messageId, timestamp, count, direction, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void getMentionMessageList(Conversation conversation,
                                      long time,
                                      int count,
                                      JetIMConst.PullDirection direction,
                                      QryHisMsgCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.getMentionMessages(conversation, time, count, direction, mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void registerPushToken(PushChannel channel, String token, String userId, WebSocketSimpleCallback callback) {
        Integer key = mCmdIndex;
        byte[] bytes = mPbData.registerPushToken(channel,
                token,
                JUtility.getDeviceId(mContext),
                mContext.getPackageName(),
                userId,
                mCmdIndex++);
        mCmdCallbackMap.put(key, callback);
        sendWhenOpen(bytes);
    }

    public void ping() {
        byte[] bytes = mPbData.pingData();
        sendWhenOpen(bytes);
    }

    public interface IWebSocketConnectListener {
        void onConnectComplete(int errorCode, String userId);

        void onDisconnect(int errorCode);

        void onWebSocketFail();

        void onWebSocketClose();

        void onTimeOut();
    }

    public interface IWebSocketMessageListener {
        void onMessageReceive(ConcreteMessage message);

        void onMessageReceive(List<ConcreteMessage> messages, boolean isFinished);

        void onSyncNotify(long syncTime);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LoggerUtils.i("JWebSocket, onOpen");
        sendConnectMsg();
    }

    @Override
    public void onMessage(String message) {
        updateLastMessageReceivedTime();
        LoggerUtils.i("JWebSocket, onMessage");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        updateLastMessageReceivedTime();
        PBRcvObj obj = mPbData.rcvObjWithBytes(bytes);
        LoggerUtils.i("JWebSocket, onMessage bytes, type is " + obj.getRcvType());
        switch (obj.getRcvType()) {
            case PBRcvObj.PBRcvType.connectAck:
                handleConnectAckMsg(obj.mConnectAck);
                break;
            case PBRcvObj.PBRcvType.publishMsgAck:
                handlePublishAckMsg(obj.mPublishMsgAck);
                break;
            case PBRcvObj.PBRcvType.qryHisMessagesAck:
                handleQryHisMsgAck(obj.mQryHisMsgAck);
                break;
            case PBRcvObj.PBRcvType.syncConversationsAck:
                handleSyncConversationAck(obj.mSyncConvAck);
                break;
            case PBRcvObj.PBRcvType.publishMsg:
                handleReceiveMessage(obj.mPublishMsgBody);
                break;
            case PBRcvObj.PBRcvType.publishMsgNtf:
                handlePublishMsgNtf(obj.mPublishMsgNtf);
                break;
            case PBRcvObj.PBRcvType.syncMessagesAck:
                handleSyncMsgAck(obj.mQryHisMsgAck);
                break;
            case PBRcvObj.PBRcvType.pong:
                handlePong();
                break;
            case PBRcvObj.PBRcvType.disconnectMsg:
                handleDisconnectMsg(obj.mDisconnectMsg);
                break;
            case PBRcvObj.PBRcvType.qryReadDetailAck:
                handleQryReadDetailAck(obj.mQryReadDetailAck);
                break;
            case PBRcvObj.PBRcvType.simpleQryAck:
                handleSimpleQryAck(obj.mSimpleQryAck);
                break;
            case PBRcvObj.PBRcvType.simpleQryAckCallbackTimestamp:
                handleSimpleQryAckWithTimeCallback(obj.mSimpleQryAck);
                break;
            case PBRcvObj.PBRcvType.conversationSetTopAck:
                handleTimestampCallback(obj.mTimestampQryAck);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LoggerUtils.i("JWebSocket, onClose, code is " + code + ", reason is " + reason + ", isRemote " + remote);
        if (remote && mConnectListener != null) {
            mConnectListener.onWebSocketClose();
        }
    }

    @Override
    public void onError(Exception ex) {
        LoggerUtils.i("JWebSocket, onError, msg is " + ex.getMessage());
        if (mConnectListener != null) {
            mConnectListener.onWebSocketFail();
        }
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setAppKey(String appKey) {
        mAppKey = appKey;
    }

    public void setSendHandler(Handler sendHandler) {
        mSendHandler = sendHandler;
    }

    public void setPushChannel(PushChannel pushChannel) {
        mPushChannel = pushChannel;
    }

    public void setPushToken(String pushToken) {
        mPushToken = pushToken;
    }

    private void sendConnectMsg() {
        byte[] bytes = mPbData.connectData(mAppKey,
                mToken,
                JUtility.getDeviceId(mContext),
                ConstInternal.PLATFORM,
                Build.BRAND,
                Build.MODEL,
                Build.VERSION.RELEASE,
                mContext.getPackageName(),
                mPushChannel,
                mPushToken,
                JUtility.getNetworkType(mContext),
                JUtility.getCarrier(mContext),
                "");
        sendWhenOpen(bytes);
    }

    private void sendDisconnectMsg(boolean receivePush) {
        byte[] bytes = mPbData.disconnectData(receivePush);
        sendWhenOpen(bytes);
    }

    private void sendPublishAck(int index) {
        byte[] bytes = mPbData.publishAckData(index);
        sendWhenOpen(bytes);
    }

    private void handleConnectAckMsg(@NonNull PBRcvObj.ConnectAck ack) {
        LoggerUtils.i("connect userId is " + ack.userId);
        if (mConnectListener != null) {
            mConnectListener.onConnectComplete(ack.code, ack.userId);
        }
        startHeartBeatTimeoutDetection();
    }

    private void handlePublishAckMsg(PBRcvObj.PublishMsgAck ack) {
        LoggerUtils.d("handlePublishAckMsg, msgId is " + ack.msgId + ", code is " + ack.code);
        IWebSocketCallback callback = mCmdCallbackMap.remove(ack.index);
        if (callback instanceof SendMessageCallback) {
            SendMessageCallback sCallback = (SendMessageCallback) callback;
            if (ack.code != 0) {
                sCallback.onError(ack.code, sCallback.getClientMsgNo());
            } else {
                sCallback.onSuccess(sCallback.getClientMsgNo(), ack.msgId, ack.timestamp, ack.seqNo);
            }
        }
    }

    private void handleQryHisMsgAck(PBRcvObj.QryHisMsgAck ack) {
        LoggerUtils.d("handleQryHisMsgAck");
        IWebSocketCallback callback = mCmdCallbackMap.remove(ack.index);
        if (callback instanceof QryHisMsgCallback) {
            QryHisMsgCallback qCallback = (QryHisMsgCallback) callback;
            if (ack.code != 0) {
                qCallback.onError(ack.code);
            } else {
                qCallback.onSuccess(ack.msgList, ack.isFinished);
            }
        }
    }

    private void handleSyncConversationAck(PBRcvObj.SyncConvAck ack) {
        IWebSocketCallback callback = mCmdCallbackMap.remove(ack.index);
        if (callback instanceof SyncConversationsCallback) {
            SyncConversationsCallback syncConversationsCallback = (SyncConversationsCallback) callback;
            if (ack.code != 0) {
                syncConversationsCallback.onError(ack.code);
            } else {
                syncConversationsCallback.onSuccess(ack.convList, ack.deletedConvList, ack.isFinished);
            }
        }
    }

    private void handleSyncMsgAck(PBRcvObj.QryHisMsgAck ack) {
        if (mMessageListener != null) {
            mMessageListener.onMessageReceive(ack.msgList, ack.isFinished);
        }
    }

    private void handleReceiveMessage(PBRcvObj.PublishMsgBody body) {
        if (mMessageListener != null) {
            mMessageListener.onMessageReceive(body.rcvMessage);
        }
        if (body.qos == 1) {
            sendPublishAck(body.index);
        }
    }

    private void handlePublishMsgNtf(PBRcvObj.PublishMsgNtf ntf) {
        if (mMessageListener != null) {
            mMessageListener.onSyncNotify(ntf.syncTime);
        }
    }

    private void handlePong() {
        LoggerUtils.d("pong, mMsgCallbackMap count is " + mCmdCallbackMap.size());
    }

    private void handleDisconnectMsg(PBRcvObj.DisconnectMsg msg) {
        if (mConnectListener != null) {
            mConnectListener.onDisconnect(msg.code);
        }
    }

    private void handleRecallMessage(PBRcvObj.PublishMsgAck ack) {
        LoggerUtils.d("handleRecallMessage, code is " + ack.code);
        IWebSocketCallback c = mCmdCallbackMap.remove(ack.index);
        if (c instanceof WebSocketTimestampCallback) {
            WebSocketTimestampCallback callback = (WebSocketTimestampCallback) c;
            if (ack.code != 0) {
                callback.onError(ack.code);
            } else {
                callback.onSuccess(ack.timestamp);
            }
        }
    }

    private void handleSimpleQryAck(PBRcvObj.SimpleQryAck ack) {
        LoggerUtils.d("handleSimpleQryAck, code is " + ack.code);
        IWebSocketCallback c = mCmdCallbackMap.remove(ack.index);
        if (c instanceof WebSocketSimpleCallback) {
            WebSocketSimpleCallback callback = (WebSocketSimpleCallback) c;
            if (ack.code != 0) {
                callback.onError(ack.code);
            } else {
                callback.onSuccess();
            }
        }
    }

    private void handleSimpleQryAckWithTimeCallback(PBRcvObj.SimpleQryAck ack) {
        LoggerUtils.d("handleSimpleQryAckWithTimeCallback, code is " + ack.code);
        IWebSocketCallback c = mCmdCallbackMap.remove(ack.index);
        if (c instanceof WebSocketTimestampCallback) {
            WebSocketTimestampCallback callback = (WebSocketTimestampCallback) c;
            if (ack.code != 0) {
                callback.onError(ack.code);
            } else {
                callback.onSuccess(ack.timestamp);
            }
        }
    }

    private void handleTimestampCallback(PBRcvObj.TimestampQryAck ack) {
        LoggerUtils.d("handleTimestampAck, code is " + ack.code);
        IWebSocketCallback c = mCmdCallbackMap.remove(ack.index);
        if (c instanceof WebSocketTimestampCallback) {
            WebSocketTimestampCallback callback = (WebSocketTimestampCallback) c;
            if (ack.code != 0) {
                callback.onError(ack.code);
            } else {
                callback.onSuccess(ack.operationTime);
            }
        }
    }

    private void handleQryReadDetailAck(PBRcvObj.QryReadDetailAck ack) {
        LoggerUtils.d("handleQryReadDetailAck, code is " + ack.code);
        IWebSocketCallback c = mCmdCallbackMap.remove(ack.index);
        if (c instanceof QryReadDetailCallback) {
            QryReadDetailCallback callback = (QryReadDetailCallback) c;
            if (ack.code != 0) {
                callback.onError(ack.code);
            } else {
                callback.onSuccess(ack.readMembers, ack.unreadMembers);
            }
        }
    }

    private void sendWhenOpen(byte[] bytes) {
        mSendHandler.post(() -> {
            if (isOpen()) {
                send(bytes);
            }
        });
    }

    private void startHeartBeatTimeoutDetection() {
        //停止之前的HeartBeatTimeoutDetection
        stopHeartBeatTimeoutDetection();
        //声明新的HeartBeatTimeoutDetection
        mHeartBeatTimeoutDetection = new Timer();
        mHeartBeatTimeoutDetection.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LoggerUtils.d("check heartbeat timeout");
                //获取当前系统时间
                long curTime = System.currentTimeMillis();
                //如果当前时系统时间和lastMessageReceivedTime的差值小于HEART_BEAT_TIMEOUT_DETECTION_MAX_IDLE_TIME，直接return
                if (curTime - mLastMessageReceivedTime <= HEART_BEAT_TIMEOUT_DETECTION_MAX_IDLE_TIME) {
                    return;
                }
                LoggerUtils.e("heartbeat has timeout, perform reconnection");
                //如果当前时系统时间和lastMessageReceivedTime的差值大于HEART_BEAT_TIMEOUT_DETECTION_MAX_IDLE_TIME，认为心跳超时，执行timeout回调
                if (mConnectListener != null) {
                    mConnectListener.onTimeOut();
                }
            }
        }, HEART_BEAT_TIMEOUT_DETECTION_INTERVAL, HEART_BEAT_TIMEOUT_DETECTION_INTERVAL);
    }

    public void stopHeartBeatTimeoutDetection() {
        if (mHeartBeatTimeoutDetection != null) {
            mHeartBeatTimeoutDetection.cancel();
            mHeartBeatTimeoutDetection = null;
        }
    }

    private void updateLastMessageReceivedTime() {
        mLastMessageReceivedTime = System.currentTimeMillis();
    }

    private String mAppKey;
    private String mToken;
    private PushChannel mPushChannel;
    private String mPushToken;
    private final PBData mPbData;
    private final Context mContext;
    private IWebSocketConnectListener mConnectListener;
    private IWebSocketMessageListener mMessageListener;
    private Integer mCmdIndex = 0;
    private Handler mSendHandler;
    private final ConcurrentHashMap<Integer, IWebSocketCallback> mCmdCallbackMap;
    private static final String WEB_SOCKET_PREFIX = "ws://";
    private static final String WEB_SOCKET_SUFFIX = "/im";
    private Timer mHeartBeatTimeoutDetection;
    private volatile long mLastMessageReceivedTime;
    private static final long HEART_BEAT_TIMEOUT_DETECTION_INTERVAL = 10 * 1000;
    private static final long HEART_BEAT_TIMEOUT_DETECTION_MAX_IDLE_TIME = HeartBeatManager.PING_INTERVAL * 3;
}
