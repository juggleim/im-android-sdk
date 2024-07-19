package com.jet.im.interfaces;

import com.jet.im.JetIMConst;
import com.jet.im.model.Conversation;
import com.jet.im.model.GroupMessageReadInfo;
import com.jet.im.model.MediaMessageContent;
import com.jet.im.model.Message;
import com.jet.im.model.MessageContent;
import com.jet.im.model.MessageOptions;
import com.jet.im.model.MessageQueryOptions;
import com.jet.im.model.UserInfo;

import java.util.List;
import java.util.Map;

public interface IMessageManager {
    interface ISimpleCallback {
        void onSuccess();

        void onError(int errorCode);
    }

    interface ISendMessageCallback {
        void onSuccess(Message message);

        void onError(Message message, int errorCode);
    }

    interface ISendMediaMessageCallback {
        void onProgress(int progress, Message message);

        void onSuccess(Message message);

        void onError(Message message, int errorCode);

        void onCancel(Message message);
    }

    interface IDownloadMediaMessageCallback {
        void onProgress(int progress, Message message);

        void onSuccess(Message message);

        void onError(int errorCode);

        void onCancel(Message message);
    }

    interface IDownloadMediaMessageCallback {
        void onProgress(int progress, Message message);

        void onSuccess(Message message);

        void onError(int errorCode);

        void onCancel(Message message);
    }

    interface IGetLocalAndRemoteMessagesCallback {
        void onGetLocalList(List<Message> messages, boolean hasRemote);

        void onGetRemoteList(List<Message> messages);

        void onGetRemoteListError(int errorCode);
    }

    interface IGetMessagesCallback {
        void onSuccess(List<Message> messages);

        void onError(int errorCode);
    }

    interface IRecallMessageCallback {
        void onSuccess(Message message);

        void onError(int errorCode);
    }

    interface ISendReadReceiptCallback {
        void onSuccess();

        void onError(int errorCode);
    }

    interface IGetGroupMessageReadDetailCallback {
        void onSuccess(List<UserInfo> readMembers, List<UserInfo> unreadMembers);

        void onError(int errorCode);
    }

    interface IBroadcastMessageCallback {
        void onProgress(Message message, int errorCode, int processCount, int totalCount);

        void onComplete();
    }

    Message sendMessage(MessageContent content,
                        Conversation conversation,
                        ISendMessageCallback callback);

    Message sendMessage(MessageContent content,
                        Conversation conversation,
                        MessageOptions options,
                        ISendMessageCallback callback);

    Message sendMediaMessage(MediaMessageContent content,
                             Conversation conversation,
                             ISendMediaMessageCallback callback);

    Message sendMediaMessage(MediaMessageContent content,
                             Conversation conversation,
                             MessageOptions options,
                             ISendMediaMessageCallback callback);

    Message resendMessage(Message message,
                          ISendMessageCallback callback);

    Message resendMediaMessage(Message message,
                               ISendMediaMessageCallback callback);

    Message saveMessage(MessageContent content, Conversation conversation);

    Message saveMessage(MessageContent content, Conversation conversation, MessageOptions options);

    List<Message> getMessages(Conversation conversation,
                              int count,
                              long timestamp,
                              JetIMConst.PullDirection direction);

    List<Message> getMessages(Conversation conversation,
                              int count,
                              long timestamp,
                              JetIMConst.PullDirection direction,
                              List<String> contentTypes);

    List<Message> getMessages(int count, long timestamp, JetIMConst.PullDirection direction, MessageQueryOptions messageQueryOptions);

    List<Message> getMessagesByMessageIds(List<String> messageIdList);

    void getMessagesByMessageIds(Conversation conversation,
                                 List<String> messageIds,
                                 IGetMessagesCallback callback);

    List<Message> getMessagesByClientMsgNos(long[] clientMsgNos);

    List<Message> searchMessage(
            String searchContent,
            int count,
            long timestamp,
            JetIMConst.PullDirection direction);

    List<Message> searchMessage(
            String searchContent,
            int count,
            long timestamp,
            JetIMConst.PullDirection direction,
            List<String> contentTypes);

    List<Message> searchMessageInConversation(
            Conversation conversation,
            String searchContent,
            int count,
            long timestamp,
            JetIMConst.PullDirection direction);


    /**
     * 下载多媒体文件。
     *
     * @param messageId 媒体消息（FileMessage，SightMessage，GIFMessage, HQVoiceMessage等）。
     * @param callback  下载文件的回调。参考 {@link IDownloadMediaMessageCallback}。
     * @since 5.0.0
     */
    void downloadMediaMessage(
            final String messageId, final IDownloadMediaMessageCallback callback);

    void cancelDownloadMediaMessage(String messageId);

    List<Message> searchMessageInConversation(
            Conversation conversation,
            String searchContent,
            int count,
            long timestamp,
            JetIMConst.PullDirection direction,
            List<String> contentTypes);

    void deleteMessagesByMessageIdList(Conversation conversation, List<String> messageIds, ISimpleCallback callback);

    void deleteMessagesByClientMsgNoList(Conversation conversation, List<Long> clientMsgNos, ISimpleCallback callback);

    void clearMessages(Conversation conversation, long startTime, ISimpleCallback callback);

    void recallMessage(String messageId, Map<String, String> extras, IRecallMessageCallback callback);

    void getRemoteMessages(Conversation conversation,
                           int count,
                           long startTime,
                           JetIMConst.PullDirection direction,
                           IGetMessagesCallback callback);

    void getLocalAndRemoteMessages(Conversation conversation,
                                   int count,
                                   long startTime,
                                   JetIMConst.PullDirection direction,
                                   IGetLocalAndRemoteMessagesCallback callback);

    void sendReadReceipt(Conversation conversation,
                         List<String> messageIds,
                         ISendReadReceiptCallback callback);

    void getGroupMessageReadDetail(Conversation conversation,
                                   String messageId,
                                   IGetGroupMessageReadDetailCallback callback);

    void getMergedMessageList(String containerMsgId,
                              IGetMessagesCallback callback);

    void getMentionMessageList(Conversation conversation,
                               int count,
                               long time,
                               JetIMConst.PullDirection direction,
                               IGetMessagesCallback callback);

    void setLocalAttribute(String messageId, String attribute);

    String getLocalAttribute(String messageId);

    void setLocalAttribute(long clientMsgNo, String attribute);

    String getLocalAttribute(long clientMsgNo);

    void broadcastMessage(MessageContent content,
                          List<Conversation> conversations,
                          IBroadcastMessageCallback callback);

    void setMessageState(long clientMsgNo, Message.MessageState state);

    void registerContentType(Class<? extends MessageContent> messageContentClass);

    void addListener(String key, IMessageListener listener);

    void removeListener(String key);

    void addSyncListener(String key, IMessageSyncListener listener);

    void removeSyncListener(String key);

    void addReadReceiptListener(String key, IMessageReadReceiptListener listener);

    void removeReadReceiptListener(String key);

    void setMessageUploadProvider(IMessageUploadProvider uploadProvider);

    interface IMessageListener {
        void onMessageReceive(Message message);

        void onMessageRecall(Message message);

        void onMessageDelete(Conversation conversation, List<Long> clientMsgNos);

        //当 senderId 有值时，表示只清空这个用户发送的消息
        void onMessageClear(Conversation conversation, long timestamp, String senderId);

        default void onSendMessageSuccess(Message message){
        }

        default void onSendMessageError(Message message,int errorCode){
        }

        default void onSendMessageUpload(Message message,int progress){
        }

        default void onSendMessageCancel(Message message){
        }
    }

    interface IMessageSyncListener {
        void onMessageSyncComplete();
    }

    interface IMessageReadReceiptListener {
        void onMessagesRead(Conversation conversation, List<String> messageIds);

        void onGroupMessagesRead(Conversation conversation, Map<String, GroupMessageReadInfo> messages);
    }
}
