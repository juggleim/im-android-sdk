package com.jet.im.kit.interfaces.providers

import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.OpenChannel
import com.jet.im.kit.activities.adapter.*
import com.jet.im.kit.model.ChannelListUIParams
import com.jet.im.kit.model.MessageListUIParams
import com.jet.im.kit.providers.*

/**
 * Interface definition to be invoked when message list adapter is created.
 * @see [AdapterProviders.messageList]
 * @since 3.9.0
 */
fun interface MessageListAdapterProvider {
    /**
     * Returns the MessageListAdapter.
     *
     * @return The [MessageListAdapter].
     * @since 3.9.0
     */
    fun provide(channel: GroupChannel, uiParams: MessageListUIParams): MessageListAdapter
}


/**
 * Interface definition to be invoked when channel list adapter is created.
 * @see [AdapterProviders.channelList]
 * @since 3.9.0
 */
fun interface ChannelListAdapterProvider {
    /**
     * Returns the ChannelListAdapter.
     *
     * @return The [ChannelListAdapter].
     * @since 3.9.0
     */
    fun provide(uiParams: ChannelListUIParams): ChannelListAdapter
}

/**
 * Interface definition to be invoked when create channel user list adapter is created.
 * @see [AdapterProviders.createChannelUserList]
 * @since 3.9.0
 */
fun interface CreateChannelUserListAdapterProvider {
    /**
     * Returns the CreateChannelUserListAdapter.
     *
     * @return The [CreateChannelUserListAdapter].
     * @since 3.9.0
     */
    fun provide(): CreateChannelUserListAdapter
}

/**
 * Interface definition to be invoked when invite user list adapter is created.
 * @see [AdapterProviders.inviteUserList]
 * @since 3.9.0
 */
fun interface InviteUserListAdapterProvider {
    /**
     * Returns the InviteUserListAdapter.
     *
     * @return The [InviteUserListAdapter].
     * @since 3.9.0
     */
    fun provide(): InviteUserListAdapter
}

/**
 * Interface definition to be invoked when message search adapter is created.
 * @see [AdapterProviders.messageSearch]
 * @since 3.9.0
 */
fun interface MessageSearchAdapterProvider {
    /**
     * Returns the MessageSearchAdapter.
     *
     * @return The [MessageSearchAdapter].
     * @since 3.9.0
     */
    fun provide(): MessageSearchAdapter
}


/**
 * Interface definition to be invoked when participant list adapter is created.
 * @see [AdapterProviders.participantList]
 * @since 3.9.0
 */
fun interface ParticipantListAdapterProvider {
    /**
     * Returns the ParticipantListAdapter.
     *
     * @return The [ParticipantListAdapter].
     * @since 3.9.0
     */
    fun provide(): ParticipantListAdapter
}

/**
 * Interface definition to be invoked when thread list adapter is created.
 * @see [AdapterProviders.threadList]
 * @since 3.9.0
 */
fun interface ThreadListAdapterProvider {
    /**
     * Returns the ThreadListAdapter.
     *
     * @return The [ThreadListAdapter].
     * @since 3.9.0
     */
    fun provide(params: MessageListUIParams): ThreadListAdapter
}
