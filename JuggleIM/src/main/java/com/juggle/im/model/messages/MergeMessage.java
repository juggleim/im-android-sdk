package com.juggle.im.model.messages;

import android.text.TextUtils;

import com.juggle.im.internal.util.JLogger;
import com.juggle.im.model.Conversation;
import com.juggle.im.model.MergeMessagePreviewUnit;
import com.juggle.im.model.MessageContent;
import com.juggle.im.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MergeMessage extends MessageContent {
    public MergeMessage(String title, Conversation conversation, List<String> messageIdList, List<MergeMessagePreviewUnit> previewList) {
        this();
        this.mTitle = title;
        this.mConversation = conversation;
        if (messageIdList.size() > 100) {
            messageIdList = messageIdList.subList(0, 100);
        }
        this.mMessageIdList = messageIdList;
        if (previewList.size() > 10) {
            previewList = previewList.subList(0, 10);
        }
        this.mPreviewList = previewList;
    }

    public MergeMessage() {
        this.mContentType = "jg:merge";
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(TITLE, mTitle);
            jsonObject.putOpt(CONTAINER_MSG_ID, mContainerMsgId);
            if (mConversation != null) {
                jsonObject.putOpt(CONVERSATION_ID, mConversation.getConversationId());
                jsonObject.putOpt(CONVERSATION_TYPE, mConversation.getConversationType().getValue());
            }
            JSONArray messageIdListJson = new JSONArray();
            if (mMessageIdList != null) {
                for (String messageId : mMessageIdList) {
                    messageIdListJson.put(messageId);
                }
            }
            jsonObject.putOpt(MESSAGE_ID_LIST, messageIdListJson);
            JSONArray previewListJson = new JSONArray();
            if (mPreviewList != null) {
                for (MergeMessagePreviewUnit unit : mPreviewList) {
                    JSONObject unitJson = new JSONObject();
                    unitJson.putOpt(CONTENT, unit.getPreviewContent());
                    UserInfo sender = unit.getSender();
                    if (sender != null) {
                        unitJson.putOpt(USER_ID, sender.getUserId());
                        unitJson.putOpt(NAME, sender.getUserName());
                        unitJson.putOpt(PORTRAIT, sender.getPortrait());
                        previewListJson.put(unitJson);
                    }
                }
            }
            jsonObject.putOpt(PREVIEW_LIST, previewListJson);
            if (!TextUtils.isEmpty(mExtra)) {
                jsonObject.put(EXTRA, mExtra);
            }
        } catch (JSONException e) {
            JLogger.e("MSG-Encode", "MergeMessage JSONException " + e.getMessage());
        }
        return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void decode(byte[] data) {
        if (data == null) {
            JLogger.e("MSG-Decode", "MergeMessage decode data is null");
            return;
        }
        String jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            mTitle = jsonObject.optString(TITLE);
            mContainerMsgId = jsonObject.optString(CONTAINER_MSG_ID);
            if (jsonObject.has(CONVERSATION_TYPE) && jsonObject.has(CONVERSATION_ID)) {
                int type = jsonObject.optInt(CONVERSATION_TYPE);
                String conversationId = jsonObject.optString(CONVERSATION_ID);
                mConversation = new Conversation(Conversation.ConversationType.setValue(type), conversationId);
            }
            JSONArray messageIdListJson = jsonObject.optJSONArray(MESSAGE_ID_LIST);
            if (messageIdListJson != null) {
                List<String> messageIdList = new ArrayList<>();
                for (int i = 0; i < messageIdListJson.length(); i++) {
                    String messageId = messageIdListJson.optString(i);
                    messageIdList.add(messageId);
                }
                mMessageIdList = messageIdList;
            }
            JSONArray previewListJson = jsonObject.optJSONArray(PREVIEW_LIST);
            if (previewListJson != null) {
                List<MergeMessagePreviewUnit> previewList = new ArrayList<>();
                for (int i = 0; i < previewListJson.length(); i++) {
                    JSONObject unitJson = previewListJson.optJSONObject(i);
                    if (unitJson != null) {
                        MergeMessagePreviewUnit unit = new MergeMessagePreviewUnit();
                        unit.setPreviewContent(unitJson.optString(CONTENT));
                        UserInfo user = new UserInfo();
                        user.setUserId(unitJson.optString(USER_ID));
                        user.setUserName(unitJson.optString(NAME));
                        user.setPortrait(unitJson.optString(PORTRAIT));
                        unit.setSender(user);
                        previewList.add(unit);
                    }
                }
                mPreviewList = previewList;
            }
            if (jsonObject.has(EXTRA)) {
                mExtra = jsonObject.optString(EXTRA);
            }
        } catch (JSONException e) {
            JLogger.e("MSG-Decode", "ImageMessage decode JSONException " + e.getMessage());
        }
    }

    @Override
    public String conversationDigest() {
        return DIGEST;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContainerMsgId() {
        return mContainerMsgId;
    }

    public void setContainerMsgId(String containerMsgId) {
        this.mContainerMsgId = containerMsgId;
    }

    public Conversation getConversation() {
        return mConversation;
    }

    public void setConversation(Conversation conversation) {
        this.mConversation = conversation;
    }

    public List<String> getMessageIdList() {
        return mMessageIdList;
    }

    public List<MergeMessagePreviewUnit> getPreviewList() {
        return mPreviewList;
    }

    public String getExtra() {
        return mExtra;
    }

    public void setExtra(String extra) {
        mExtra = extra;
    }

    private String mTitle;
    private String mContainerMsgId;
    private Conversation mConversation;
    private List<String> mMessageIdList;
    private List<MergeMessagePreviewUnit> mPreviewList;
    private String mExtra;
    private static final String TITLE = "title";
    private static final String CONTAINER_MSG_ID = "containerMsgId";
    private static final String CONVERSATION_ID = "conversationId";
    private static final String CONVERSATION_TYPE = "conversationType";
    private static final String MESSAGE_ID_LIST = "messageIdList";
    private static final String PREVIEW_LIST = "previewList";
    private static final String CONTENT = "content";
    private static final String USER_ID = "userId";
    private static final String NAME = "name";
    private static final String PORTRAIT = "portrait";
    private static final String EXTRA = "extra";
    private static final String DIGEST = "[Merge]";
}
