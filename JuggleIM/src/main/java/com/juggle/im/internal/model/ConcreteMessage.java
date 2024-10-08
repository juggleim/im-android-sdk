package com.juggle.im.internal.model;

import com.juggle.im.model.GroupInfo;
import com.juggle.im.model.Message;
import com.juggle.im.model.PushData;
import com.juggle.im.model.UserInfo;

public class ConcreteMessage extends Message {
    public long getSeqNo() {
        return mSeqNo;
    }

    public void setSeqNo(long seqNo) {
        mSeqNo = seqNo;
    }

    public long getMsgIndex() {
        return mMsgIndex;
    }

    public void setMsgIndex(long msgIndex) {
        mMsgIndex = msgIndex;
    }

    public String getClientUid() {
        return mClientUid;
    }

    public void setClientUid(String clientUid) {
        mClientUid = clientUid;
    }

    public int getFlags() {
        return mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public boolean isExisted() {
        return mExisted;
    }

    public void setExisted(boolean existed) {
        mExisted = existed;
    }

    public GroupInfo getGroupInfo() {
        return mGroupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
    }

    public UserInfo getTargetUserInfo() {
        return mTargetUserInfo;
    }

    public void setTargetUserInfo(UserInfo targetUserInfo) {
        mTargetUserInfo = targetUserInfo;
    }

    public String getReferMsgId() {
        return mReferMsgId;
    }

    public void setReferMsgId(String referMsgId) {
        this.mReferMsgId = referMsgId;
    }

    public PushData getPushData() {
        return mPushData;
    }

    public void setPushData(PushData pushData) {
        mPushData = pushData;
    }


    private long mSeqNo;
    private long mMsgIndex;
    private String mClientUid;
    private int mFlags;
    private boolean mExisted;
    private GroupInfo mGroupInfo;
    private UserInfo mTargetUserInfo;
    private String mReferMsgId;
    private PushData mPushData;
}
