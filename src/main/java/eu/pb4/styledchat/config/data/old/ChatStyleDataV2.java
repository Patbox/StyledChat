package eu.pb4.styledchat.config.data.old;

import eu.pb4.styledchat.config.data.ChatStyleData;

public class ChatStyleDataV2 {
    public String displayName;
    public String chat;
    public String join;
    public String joinRenamed;
    public String joinFirstTime;
    public String left;
    public String death;
    public String advancementTask;
    public String advancementChallenge;
    public String advancementGoal;
    public String teamChatSent;
    public String teamChatReceived;
    public String privateMessageSent;
    public String privateMessageReceived;
    public String sayCommand;
    public String meCommand;

    public ChatStyleData update() {
        var data = new ChatStyleData();
        this.copyInto(data);
        return data;
    }

    public void copyInto(ChatStyleData data) {
        data.displayName = this.displayName;

        data.messages.chat = this.chat;
        data.messages.advancementChallenge = this.advancementChallenge;
        data.messages.advancementGoal = this.advancementGoal;
        data.messages.advancementTask = this.advancementTask;
        data.messages.baseDeath = this.death;
        data.messages.joinedGame = this.join;
        data.messages.joinedAfterNameChange = this.joinRenamed;
        data.messages.joinedForFirstTime = this.joinFirstTime;
        data.messages.leftGame = this.left;
        data.messages.sentTeamChat = this.teamChatSent;
        data.messages.receivedTeamChat = this.teamChatReceived;
        data.messages.privateMessageSent = this.privateMessageSent;
        data.messages.privateMessageReceived = this.privateMessageReceived;
        data.messages.sayCommandMessage = this.sayCommand;
        data.messages.meCommandMessage = this.meCommand;
    }
}
