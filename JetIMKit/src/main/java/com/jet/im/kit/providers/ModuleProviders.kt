package com.jet.im.kit.providers

import com.jet.im.kit.interfaces.providers.*
import com.jet.im.kit.internal.ui.notifications.ChatNotificationChannelModule
import com.jet.im.kit.internal.ui.notifications.FeedNotificationChannelModule
import com.jet.im.kit.modules.*

/**
 * UIKit for Android, you need a module and components to create a view.
 * Components are the smallest unit of customizable views that can make up a whole screen and the module coordinates these components to be shown as the fragment's view.
 * Each module also has its own customizable style per screen.
 * A set of Providers that provide a Module that binds to a Fragment among the screens used in UIKit.
 *
 * @since 3.9.0
 */
object ModuleProviders {

    /**
     * Returns the ChannelListModule provider.
     *
     * @return The [ChannelListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var channelList: ChannelListModuleProvider

    /**
     * Returns the ChannelModule provider.
     *
     * @return The [ChannelModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var channel: ChannelModuleProvider

    /**
     * Returns the CreateChannelModule provider.
     *
     * @return The [CreateChannelModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var createChannel: CreateChannelModuleProvider

    /**
     * Returns the CreateOpenChannelModule provider.
     *
     * @return The [CreateOpenChannelModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var createOpenChannel: CreateOpenChannelModuleProvider

    /**
     * Returns the ChannelSettingsModule provider.
     *
     * @return The [ChannelSettingsModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var channelSettings: ChannelSettingsModuleProvider

    /**
     * Returns the InviteUserModule provider.
     *
     * @return The [InviteUserModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var inviteUser: InviteUserModuleProvider

    /**
     * Returns the RegisterOperatorModule provider.
     *
     * @return The [RegisterOperatorModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var registerOperator: RegisterOperatorModuleProvider

    /**
     * Returns the ModerationModule provider.
     *
     * @return The [ModerationModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var moderation: ModerationModuleProvider

    /**
     * Returns the MemberListModule provider.
     *
     * @return The [MemberListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var memberList: MemberListModuleProvider

    /**
     * Returns the BannedUserListModule provider.
     *
     * @return The [BannedUserListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var bannedUserList: BannedUserListModuleProvider

    /**
     * Returns the MutedMemberListModule provider.
     *
     * @return The [MutedMemberListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var mutedMemberList: MutedMemberListModuleProvider

    /**
     * Returns the OperatorListModule provider.
     *
     * @return The [OperatorListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var operatorList: OperatorListModuleProvider

    /**
     * Returns the MessageSearchModule provider.
     *
     * @return The [MessageSearchModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var messageSearch: MessageSearchModuleProvider

    /**
     * Returns the MessageThreadModule provider.
     *
     * @return The [MessageThreadModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var messageThread: MessageThreadModuleProvider

    /**
     * Returns the ParticipantListModule provider.
     *
     * @return The [ParticipantListModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var participantList: ParticipantListModuleProvider

    /**
     * Returns the ChannelPushSettingModule provider.
     *
     * @return The [ChannelPushSettingModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    lateinit var channelPushSetting: ChannelPushSettingModuleProvider

    /**
     * Returns the OpenChannelParticipantListModule provider.
     *
     * @return The [FeedNotificationChannelModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    internal lateinit var feedNotificationChannel: FeedNotificationChannelModuleProvider

    /**
     * Returns the ChatNotificationChannelModule provider.
     *
     * @return The [ChatNotificationChannelModuleProvider].
     * @since 3.9.0
     */
    @JvmStatic
    internal lateinit var chatNotificationChannel: ChatNotificationChannelModuleProvider

    /**
     * Reset all providers to default provider.
     *
     * @since 3.10.1
     */
    @JvmStatic
    fun resetToDefault() {
        this.channelList = ChannelListModuleProvider { context, _ -> ChannelListModule(context) }

        this.channel = ChannelModuleProvider { context, _ -> ChannelModule(context) }

        this.createChannel = CreateChannelModuleProvider { context, _ -> CreateChannelModule(context) }

        this.createOpenChannel = CreateOpenChannelModuleProvider { context, _ -> CreateOpenChannelModule(context) }

        this.channelSettings = ChannelSettingsModuleProvider { context, _ -> ChannelSettingsModule(context) }

        this.inviteUser = InviteUserModuleProvider { context, _ -> InviteUserModule(context) }

        this.registerOperator = RegisterOperatorModuleProvider { context, _ -> RegisterOperatorModule(context) }

        this.moderation = ModerationModuleProvider { context, _ -> ModerationModule(context) }

        this.memberList = MemberListModuleProvider { context, _ -> MemberListModule(context) }

        this.bannedUserList = BannedUserListModuleProvider { context, _ -> BannedUserListModule(context) }

        this.mutedMemberList = MutedMemberListModuleProvider { context, _ -> MutedMemberListModule(context) }

        this.operatorList = OperatorListModuleProvider { context, _ -> OperatorListModule(context) }

        this.messageSearch = MessageSearchModuleProvider { context, _ -> MessageSearchModule(context) }

        this.messageThread = MessageThreadModuleProvider { context, _, message -> MessageThreadModule(context, message) }

        this.participantList = ParticipantListModuleProvider { context, _ -> ParticipantListModule(context) }

        this.channelPushSetting = ChannelPushSettingModuleProvider { context, _ -> ChannelPushSettingModule(context) }

        this.feedNotificationChannel = FeedNotificationChannelModuleProvider { context, _, config ->
            FeedNotificationChannelModule(context, config)
        }

        this.chatNotificationChannel = ChatNotificationChannelModuleProvider { context, _, config ->
            ChatNotificationChannelModule(context, config)
        }
    }

    init {
        resetToDefault()
    }
}
