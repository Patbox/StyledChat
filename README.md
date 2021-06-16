# Styled Chat
It's a simple mod that allows server owners to change how their chat looks!
It supports changing style per player with permissions (supports LuckPerms and PlayerRoles)
If you have any questions, you can ask them on my [Discord](https://discord.com/invite/AbqPPppgrd)

![Example image](https://i.imgur.com/y0KGyVT.png)
![Example image2](https://i.imgur.com/ObepOhW.png)


## Commands (and permissions):
- `/styledchat` - Main command (`styledchat.main`, available by default)
- `/styledchat reload` - Reloads configuration and styles (requires `styledchat.reload`)

## Configuration:
You can find config file in `./config/styled-chat.json`.
[Formatting uses PlaceholderAPI's Text Parser for which docs you can find here](https://github.com/Patbox/FabricPlaceholderAPI/blob/1.17/TEXT_FORMATTING.md).
It supports usage of placeholders from [Placeholder API](https://github.com/Patbox/FabricPlaceholderAPI/wiki).
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
  "permissionStyles": {            // Permission based overrides, applied from highest to lowest
    "permission.test": {           // Permission string required to use
      // The same values as in "defaultStyle", however it will handle missing ones just fine
      // By applying next valid 
    }
  },
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
  "CONFIG_VERSION_DONT_TOUCH_THIS": 1,
  "_comment": "Before changing anything, see https://github.com/Patbox/StyledChat#configuration",
  "defaultStyle": {
    "displayName": "${vanillaDisplayName}",
    "chat": "${player} <dark_gray>»</dark_gray> <gray>${message}",
    "join": "<gray>✚</gray> <color:#85ff8f><lang:multiplayer.player.joined:'${player}'>",
    "joinRenamed": "<gray>✚</gray> <color:#85ff8f><lang:multiplayer.player.joined.renamed:'${player}':'${old_name}'>",
    "left": "<gray>☁</gray> <color:#ff8585><lang:multiplayer.player.left:'${player}'>",
    "death": "<gray>☠</gray> <color:#d1d1d1>${default_message}",
    "advancementTask": "<gray>✔</gray> <gold><lang:chat.type.advancement.task:'${player}':'${advancement}'>",
    "advancementChallenge": "<gray>✔</gray> <gold><lang:chat.type.advancement.challenge:'${player}':'${advancement}'>",
    "advancementGoal": "<gray>✔</gray> <gold><lang:chat.type.advancement.goal:'${player}':'${advancement}'>"
  },
  "permissionStyles": {
    "group.admin": {
      "chat": "${player} <dark_gray>»</dark_gray> <gold>${message}"
    },
    "group.test": {
      "displayName": "<dark_gray>[<dark_red>Admin</dark_red> | <yellow>%player:playtime%</yellow>]</dark_gray> <red>${vanillaDisplayName}</red>"
    }
  }
}
```