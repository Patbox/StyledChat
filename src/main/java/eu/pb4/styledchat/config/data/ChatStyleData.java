package eu.pb4.styledchat.config.data;


import java.util.Objects;

public class ChatStyleData {
    public static ChatStyleData DEFAULT = getDefault();

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


    private static ChatStyleData getDefault() {
        ChatStyleData data = new ChatStyleData();
        data.displayName = "${vanillaDisplayName}";
        data.chat = "<${player}> ${message}";
        data.join = "<yellow><lang:multiplayer.player.joined:'${player}'></yellow>";
        data.joinRenamed = "<yellow><lang:multiplayer.player.joined.renamed:'${player}':'${old_name}'></yellow>";
        data.joinFirstTime = "<yellow><lang:multiplayer.player.joined:'${player}'></yellow>";
        data.left = "<yellow><lang:multiplayer.player.left:'${player}'></yellow>";
        data.death = "${default_message}";
        data.advancementTask = "<><lang:chat.type.advancement.task:'${player}':'${advancement}'>";
        data.advancementGoal = "<lang:chat.type.advancement.goal:'${player}':'${advancement}'>";
        data.advancementChallenge = "<lang:chat.type.advancement.challenge:'${player}':'${advancement}'>";
        data.teamChatSent = "<lang:'chat.type.team.sent':'<hover\\:\\'<lang\\:chat.type.team.hover>\\'><suggest_command\\:\\'/teammsg \\'>${team}':'${displayName}':'${message}'>";
        data.teamChatReceived = "<lang:'chat.type.team.text':'<hover\\:\\'<lang\\:chat.type.team.hover>\\'><suggest_command\\:\\'/teammsg \\'>${team}':'${displayName}':'${message}'>";
        data.privateMessageSent = "<gray><italic><lang:commands.message.display.outgoing:'${receiver}':'${message}'>";
        data.privateMessageReceived = "<gray><italic><lang:commands.message.display.incoming:'${sender}':'${message}'>";
        data.sayCommand = "[${player}] ${message}";
        data.meCommand = "<lang:'chat.type.emote':'${player}':'${message}'>";

        return data;
    }

    public void fillMissing() {
        this.displayName = Objects.requireNonNullElse(this.displayName, DEFAULT.displayName);
        this.chat = Objects.requireNonNullElse(this.chat, DEFAULT.chat);
        this.join = Objects.requireNonNullElse(this.join, DEFAULT.join);
        this.joinRenamed = Objects.requireNonNullElse(this.joinRenamed, DEFAULT.joinRenamed);
        this.left = Objects.requireNonNullElse(this.left, DEFAULT.left);
        this.death = Objects.requireNonNullElse(this.death, DEFAULT.death);
        this.advancementTask = Objects.requireNonNullElse(this.advancementTask, DEFAULT.advancementTask);
        this.advancementChallenge = Objects.requireNonNullElse(this.advancementChallenge, DEFAULT.advancementChallenge);
        this.advancementGoal = Objects.requireNonNullElse(this.advancementGoal, DEFAULT.advancementGoal);
        this.privateMessageReceived = Objects.requireNonNullElse(this.privateMessageReceived, DEFAULT.privateMessageReceived);
        this.privateMessageSent = Objects.requireNonNullElse(this.privateMessageSent, DEFAULT.privateMessageSent);
        this.teamChatSent = Objects.requireNonNullElse(this.teamChatSent, DEFAULT.teamChatSent);
        this.teamChatReceived = Objects.requireNonNullElse(this.teamChatReceived, DEFAULT.teamChatReceived);
        this.sayCommand = Objects.requireNonNullElse(this.sayCommand, DEFAULT.sayCommand);
        this.meCommand = Objects.requireNonNullElse(this.meCommand, DEFAULT.meCommand);
    }
}
