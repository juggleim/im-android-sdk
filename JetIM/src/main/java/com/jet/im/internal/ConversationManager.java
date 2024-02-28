package com.jet.im.internal;

import android.text.TextUtils;

import com.jet.im.JetIMConst;
import com.jet.im.internal.core.JetIMCore;
import com.jet.im.interfaces.IConversationManager;
import com.jet.im.internal.core.db.DBManager;
import com.jet.im.internal.core.network.SyncConversationsCallback;
import com.jet.im.internal.core.network.WebSocketTimestampCallback;
import com.jet.im.internal.model.ConcreteConversationInfo;
import com.jet.im.model.Conversation;
import com.jet.im.model.ConversationInfo;
import com.jet.im.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationManager implements IConversationManager {

    public ConversationManager(JetIMCore core) {
        this.mCore = core;
        this.mCachedSyncTime = -1;
    }

    @Override
    public List<ConversationInfo> getConversationInfoList() {
        return mCore.getDbManager().getConversationInfoList();
    }

    @Override
    public List<ConversationInfo> getConversationInfoList(int[] conversationTypes, int count, long timestamp, JetIMConst.PullDirection direction) {
        return mCore.getDbManager().getConversationInfoList(conversationTypes, count, timestamp, direction);
    }

    @Override
    public List<ConversationInfo> getConversationInfoList(int count, long timestamp, JetIMConst.PullDirection direction) {
        return getConversationInfoList(null, count, timestamp, direction);
    }

    @Override
    public ConversationInfo getConversationInfo(Conversation conversation) {
        return mCore.getDbManager().getConversationInfo(conversation);
    }

    @Override
    public void deleteConversationInfo(Conversation conversation) {
        mCore.getDbManager().deleteConversationInfo(conversation);
        //手动删除不给回调
        if (mCore.getWebSocket() == null) {
            return;
        }
        mCore.getWebSocket().deleteConversationInfo(conversation, mCore.getUserId(), new WebSocketTimestampCallback() {
            @Override
            public void onSuccess(long timestamp) {
                LoggerUtils.i("delete conversation success");
            }

            @Override
            public void onError(int errorCode) {
                LoggerUtils.i("delete conversation fail, code is " + errorCode);
            }
        });
    }

    @Override
    public void setDraft(Conversation conversation, String draft) {
        mCore.getDbManager().setDraft(conversation, draft);
    }

    @Override
    public void clearDraft(Conversation conversation) {
        setDraft(conversation, "");
    }

    @Override
    public void addListener(String key, IConversationListener listener) {
        if (listener == null || TextUtils.isEmpty(key)) {
            return;
        }
        if (mListenerMap == null) {
            mListenerMap = new ConcurrentHashMap<>();
        }
        mListenerMap.put(key, listener);
    }

    @Override
    public void removeListener(String key) {
        if (!TextUtils.isEmpty(key) && mListenerMap != null) {
            mListenerMap.remove(key);
        }
    }

    @Override
    public void addSyncListener(String key, IConversationSyncListener listener) {
        if (listener == null || TextUtils.isEmpty(key)) {
            return;
        }
        if (mSyncListenerMap == null) {
            mSyncListenerMap = new ConcurrentHashMap<>();
        }
        mSyncListenerMap.put(key, listener);
    }

    @Override
    public void removeSyncListener(String key) {
        if (!TextUtils.isEmpty(key) && mSyncListenerMap != null) {
            mSyncListenerMap.remove(key);
        }
    }

    void syncConversations(ICompleteCallback callback) {
        if (mCore.getWebSocket() == null) {
            return;
        }
        mSyncProcessing = true;
        mCore.getWebSocket().syncConversations(mCore.getConversationSyncTime(), CONVERSATION_SYNC_COUNT, mCore.getUserId(), new SyncConversationsCallback() {
            @Override
            public void onSuccess(List<ConcreteConversationInfo> conversationInfoList, List<ConcreteConversationInfo> deleteConversationInfoList, boolean isFinished) {
                long syncTime = 0;
                if (conversationInfoList.size() > 0) {
                    ConcreteConversationInfo last = conversationInfoList.get(conversationInfoList.size() - 1);
                    if (last.getSyncTime() > syncTime) {
                        syncTime = last.getSyncTime();
                    }
                    mCore.getDbManager().insertConversations(conversationInfoList, new DBManager.IDbInsertConversationsCallback() {
                        @Override
                        public void onComplete(List<ConcreteConversationInfo> insertList, List<ConcreteConversationInfo> updateList) {
                            if (insertList.size() > 0) {
                                if (mListenerMap != null) {
                                    List<ConversationInfo> l = new ArrayList<>(insertList);
                                    for (Map.Entry<String, IConversationListener> entry : mListenerMap.entrySet()) {
                                        entry.getValue().onConversationInfoAdd(l);
                                    }
                                }
                            }
                            if (updateList.size() > 0) {
                                if (mListenerMap != null) {
                                    List<ConversationInfo> l = new ArrayList<>(updateList);
                                    for (Map.Entry<String, IConversationListener> entry : mListenerMap.entrySet()) {
                                        entry.getValue().onConversationInfoUpdate(l);
                                    }
                                }
                            }
                        }
                    });
                }
                if (deleteConversationInfoList.size() > 0) {
                    ConcreteConversationInfo last = deleteConversationInfoList.get(deleteConversationInfoList.size() - 1);
                    if (last.getSyncTime() > syncTime) {
                        syncTime = last.getSyncTime();
                    }
                    for (ConcreteConversationInfo info : deleteConversationInfoList) {
                        mCore.getDbManager().deleteConversationInfo(info.getConversation());
                    }
                    List<ConversationInfo> l = new ArrayList<>(deleteConversationInfoList);
                    if (mListenerMap != null) {
                        for (Map.Entry<String, IConversationListener> entry : mListenerMap.entrySet()) {
                            entry.getValue().onConversationInfoDelete(l);
                        }
                    }
                }
                if (syncTime > 0) {
                    mCore.setConversationSyncTime(syncTime);
                }
                if (!isFinished) {
                    syncConversations(callback);
                } else {
                    mSyncProcessing = false;
                    if (mCachedSyncTime > 0) {
                        mCore.setConversationSyncTime(mCachedSyncTime);
                        mCachedSyncTime = -1;
                    }
                    if (mSyncListenerMap != null) {
                        for (Map.Entry<String, IConversationSyncListener> entry : mSyncListenerMap.entrySet()) {
                            entry.getValue().onConversationSyncComplete();
                        }
                    }
                    if (callback != null) {
                        callback.onComplete();
                    }
                }
            }

            @Override
            public void onError(int errorCode) {
                LoggerUtils.e("sync conversation fail, code is " + errorCode);
                if (callback != null) {
                    callback.onComplete();
                }
            }
        });
    }

    interface ICompleteCallback {
        void onComplete();
    }

    private final JetIMCore mCore;
    private ConcurrentHashMap<String, IConversationListener> mListenerMap;
    private ConcurrentHashMap<String, IConversationSyncListener> mSyncListenerMap;
    private boolean mSyncProcessing;
    private long mCachedSyncTime;
    private static final int CONVERSATION_SYNC_COUNT = 100;
}
