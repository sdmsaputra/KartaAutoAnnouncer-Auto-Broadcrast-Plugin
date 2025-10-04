# KartaAutoAnnouncer

KartaAutoAnnouncer is a lightweight, flexible, and easy-to-use Spigot plugin designed to broadcast automated messages to your Minecraft server. It supports various message types and gives you full control over your announcements.

## Features

- **Flexible Announcement Types**: Broadcast messages in chat, on the action bar, or as a screen title. Each type can be individually enabled or disabled.
- **Multi-line Chat Support**: Create detailed announcements with multiple lines in a single chat message.
- **Toggleable Prefix**: You can now enable or disable the prefix for chat messages.
- **Easy Configuration**: A clean and simple configuration file that is easy to understand and modify.
- **In-Game Commands**: Reload the configuration and set the announcement interval directly from the game.
- **PlaceholderAPI Support**: Use placeholders from other plugins in your announcements.
- **Hex Color Support**: Use hex color codes (e.g., `&#RRGGBB`) for vibrant messages.

## Installation

1.  Download the latest version of the plugin from the [releases page](https://github.com/your-repo/KartaAutoAnnouncer/releases).
2.  Place the downloaded `.jar` file into your server's `plugins` folder.
3.  Restart or reload your server.

## Configuration

The `config.yml` file is located in the `plugins/KartaAutoAnnouncer` directory. Here's an overview of the new configuration structure:

```yaml
# The interval in seconds between each announcement.
interval: 60

# Prefix settings for CHAT announcements.
prefix:
  # Set to true to enable the prefix, false to disable it.
  enabled: true
  # The text to be displayed as the prefix.
  text: "&d[Karta] &r"

# A list of messages to be broadcast.
# You can enable or disable each announcement type individually.
# Color codes (&a, &b, etc.) and hex colors (&#RRGGBB) are supported in all text fields.
announcements:
  chat:
    # Set to true to enable chat announcements, false to disable.
    enabled: true
    messages:
      - "This is a single-line chat message."
      - [
          "This is a multi-line message.",
          "This is the second line."
        ]
  action_bar:
    # Set to true to enable action bar announcements, false to disable.
    enabled: true
    messages:
      - "&aThis is an action bar message."
      - "&eCheck out our website!"
  title:
    # Set to true to enable title announcements, false to disable.
    enabled: true
    messages:
      - title: "&6Welcome!"
        subtitle: "&fEnjoy your stay on our server."
      - title: "&cVote for us!"
        subtitle: "&fReceive rewards for your support."
```

## Commands & Permissions

The main command is `/kaa` (alias for `/kartaautoannouncer`).

- **Permission**: `autoannouncer.admin` (default: OP)

### Sub-commands

- `/kaa reload` - Reloads the configuration file from disk.
- `/kaa setinterval <seconds>` - Sets the interval between announcements and saves it to the config.

**Note**: After using `/kaa setinterval`, you still need to run `/kaa reload` to apply the new interval. Adding and removing messages should be done by editing the `config.yml` file.

## Support

If you encounter any issues or have any questions, please open an issue on the [GitHub repository](https://github.com/your-repo/KartaAutoAnnouncer/issues).