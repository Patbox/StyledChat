# Styled Chat
It's a simple mod that allows server owners to change how their chat looks!

It adds support for [modern chat formatting](https://placeholders.pb4.eu/user/text-format/) supported by Minecraft, 
but ignored by many chat mods/plugins.

It's also compatible with any mods using [Placeholder API](https://placeholders.pb4.eu/user/general/).

It also supports changing style per player with permissions (supports LuckPerms and PlayerRoles)

*This mod works only on Fabric Mod Loader and compatible!*

If you have any questions, you can ask them on my [Discord](https://pb4.eu/discord)

[Also check out my other mods and project, as you might find them useful!](https://pb4.eu)

![Example image](https://i.imgur.com/HPSMaS8.png)
![Example image2](https://i.imgur.com/mSWzIV4.png)


## Commands (and permissions):
- `/styledchat` - Main command (`styledchat.main`, available by default)
- `/styledchat reload` - Reloads configuration and styles (requires `styledchat.reload`)

## Configuration:
You can find config file in `./config/styled-chat.json`.
[Formatting uses Simplified Text Format](https://placeholders.pb4.eu/user/text-format/).
It supports usage of placeholders from [Placeholder API](https://placeholders.pb4.eu/user/general/).
Additionally, every message type has few own local variables.

```json5
{
  "CONFIG_VERSION_DONT_TOUCH_THIS": 1,
  "defaultStyle": {                // Default style settings
    "displayName": "...",          // Display name (local variables: ${vanillaDisplayName}, ${name})
    "chat": "...",                 // Chat message style (local variables: ${player}, ${message})
    "join": "...",                 // Join message (local variables: ${player})
    "joinRenamed": "...",          // Join message after name change (local variables: ${player}, ${old_name})
    "left": "...",                 // Player leaving server (local variables: ${player})
    "death": "...",                // Player death message (local variables: ${player}, ${default_message})
    "advancementTask": "...",      // Finishing advancement task (local variables: ${player}, ${advancement})
    "advancementChallenge": "...", // Finishing advancement challenge (local variables: ${player}, ${advancement})
    "advancementGoal": "...",      // Finishing advancement goal (local variables: ${player}, ${advancement})
  },
  "permissionStyles": [            // Permission based overrides, applied from highest to lowest
    {
      "permission": "...",  // Permission string required to use
      "style": {
        // The same values as in "defaultStyle", however it will handle missing ones just fine
        // By applying next valid 
      }
    } // You can have as many permission overrides as possible
      // Just remember to have most important ones above least (so for example Admin, Moderator, Helper)
  ],
  "legacyChatFormatting": false,    // Enables support for legacy (&x) codes in chat (only when typed by player)
  "defaultEnabledFormatting": {
    "type": false
    // Here you can change which formatting is available by default for player
  }
}
```

## In chat formatting
If player has a required permissions (`styledchat.format.[tag_name]`, where `[tagname]` is Text Parser tag), then they can use Text Parser's formatting tags from within their chat.
It supports all default ones with addition of `<item>` tag.


## Example config
```json 
{
  "CONFIG_VERSION_DONT_TOUCH_THIS": 2,
  "_comment": "Before changing anything, see https://github.com/Patbox/StyledChat#configuration",
  "defaultStyle": {
    "displayName": "${vanillaDisplayName}",
    "chat": "${player} <dark_gray>»</dark_gray> <gray>${message}",
    "join": "<gray>✚</gray> <color:#85ff8f><lang:multiplayer.player.joined:'${player}'>",
    "joinRenamed": "<gray>✚</gray> <color:#85ff8f><lang:multiplayer.player.joined.renamed:'${player}':'${old_name}'>",
    "left": "<gray>☁</gray> <color:#ff8585><lang:multiplayer.player.left:'${player}'>",
    "death": "<gray>☠</gray> <color:#d1d1d1>${default_message}",
    "advancementTask": "<lang:chat.type.advancement.task:'${player}':'${advancement}'>",
    "advancementChallenge": "<lang:chat.type.advancement.challenge:'${player}':'${advancement}'>",
    "advancementGoal": "<lang:chat.type.advancement.goal:'${player}':'${advancement}'>",
    "teamChatSent": "<lang:'chat.type.team.sent':'<hover\\:\\'<lang\\:chat.type.team.hover>\\'><suggest_command\\:\\'/teammsg \\'>${team}':'${displayName}':'${message}'>",
    "teamChatReceived": "<lang:'chat.type.team.text':'<hover\\:\\'<lang\\:chat.type.team.hover>\\'><suggest_command\\:\\'/teammsg \\'>${team}':'${displayName}':'${message}'>",
    "privateMessageSent": "<gray>[<green>PM</green> → ${receiver}] <dark_gray>»<reset> ${message}",
    "privateMessageReceived": "<gray>[<green>PM</green> ← ${sender}] <dark_gray>»<reset> ${message}",
    "sayCommand": "<red>[${player}] ${message}",
    "meCommand": "<green>* ${player} ${message}"
  },
  "permissionStyles": [
    {
      "permission": "group.admin",
      "opLevel": 4,
      "style": {
        "displayName": "<dark_gray>[<red>Admin</red>]</dark_gray> <c:#ffe8a3>${vanillaDisplayName}</c>",
        "chat": "${player} <dark_gray>»</dark_gray> <orange>${message}",
        "death": ""
      }
    },
    {
      "permission": "group.default",
      "opLevel": 0,
      "style": {
        "displayName": "<dark_gray>[<aqua>Player</aqua>]</dark_gray> <dark_aqua>${vanillaDisplayName}</dark_aqua>"
      }
    }
  ],
  "petDeathMessage": "Oh no! ${default_message}",
  "emoticons": {
    "bucket": "🪣",
    "sword": "🗡",
    "potion": "🧪",
    "trident": "🔱",
    "shears": "✂",
    "rod": "🎣",
    "fire": "🔥",
    "shrug": "¯\\_(ツ)_/¯",
    "bow": "🏹",
    "bell": "<yellow>🔔",
    "table": "<rb>(╯°□°）╯︵ ┻━┻",
    "heart": "<red>❤"
  },
  "permissionEmoticons": [
    {
      "permission": "group.vip",
      "opLevel": 3,
      "emotes": {}
    }
  ],
  "legacyChatFormatting": true,
  "parseLinksInChat": true,
  "enableMarkdown": true,
  "formattingInPrivateMessages": true,
  "formattingInTeamMessages": true,
  "linkStyle": "<underline><blue>${link}",
  "spoilerStyle": "<dark_gray>${spoiler}",
  "spoilerSymbol": "▌",
  "defaultEnabledFormatting": {
    "dark_red": false,
    "color": false,
    "underline": true,
    "yellow": false,
    "italic": true,
    "dark_blue": false,
    "dark_purple": false,
    "gold": false,
    "red": false,
    "aqua": false,
    "hover": false,
    "gray": false,
    "light_purple": false,
    "white": false,
    "pos": true,
    "dark_gray": false,
    "spoiler": true,
    "strikethrough": true,
    "lang": false,
    "obfuscated": false,
    "dark_grey": false,
    "key": false,
    "change_page": false,
    "st": true,
    "b": true,
    "item": true,
    "green": false,
    "c": false,
    "dark_green": false,
    "gradient": false,
    "black": false,
    "em": false,
    "i": true,
    "bold": true,
    "gr": false,
    "grey": false,
    "orange": false,
    "rb": false,
    "rainbow": false,
    "obf": false,
    "colour": false,
    "blue": false,
    "dark_aqua": false,
    "underlined": false,
    "reset": false,
    "page": false,
    "font": false
  }
}
```