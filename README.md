# KartaAutoAnnouncer

KartaAutoAnnouncer is a lightweight and easy-to-use Spigot plugin designed to broadcast automated messages to your Minecraft server. It supports various message types, including chat, action bar, and titles, ensuring your announcements are seen.

## Features

- **Multiple Announcement Types**: Broadcast messages in chat, on the action bar, or as a screen title.
- **Easy Configuration**: A clean and simple configuration file.
- **In-Game Commands**: Reload the configuration, add new messages, and set the announcement interval directly from the game.
- **PlaceholderAPI Support**: Use placeholders from other plugins in your announcements.
- **Hex Color Support**: Use hex color codes (e.g., `&#RRGGBB`) for vibrant messages.

## Installation

1.  Download the latest version of the plugin from the [releases page](https://github.com/your-repo/KartaAutoAnnouncer/releases).
2.  Place the downloaded `.jar` file into your server's `plugins` folder.
3.  Restart or reload your server.

## Configuration

The `config.yml` file is located in the `plugins/KartaAutoAnnouncer` directory. Here's an overview of the default configuration:

```yaml
# The interval in seconds between each announcement.
interval: 60

# The prefix to be displayed before every CHAT announcement.
# This does not apply to ACTION_BAR or TITLE messages.
prefix: "&d[Karta] &r"

# A list of messages to be broadcast.
announcements:
  chat:
    - "&aWelcome to our server! We hope you have a great time."
    - "&6Don't forget to join our Discord!"
  action_bar:
    - "&bMake sure to read the &c&l/rules&b."
  title:
    - title: "&#4287f5Vote for us!"
      subtitle: "&eUse the /vote command for rewards!"
```

## Commands & Permissions

The main command is `/kaa` (alias for `/kartaautoannouncer`).

- **Permission**: `autoannouncer.admin` (default: OP)

### Sub-commands

- `/kaa reload` - Reloads the configuration file.
- `/kaa setinterval <seconds>` - Sets the interval between announcements.
- `/kaa add <type> <message>` - Adds a new announcement.

### Adding Announcements

You can add announcements directly in-game using the `/kaa add` command.

- **Type**: `chat`, `actionbar`, or `title`.

**Examples:**

- **Chat:**
  `/kaa add chat &aThis is a new chat message!`

- **Action Bar:**
  `/kaa add actionbar &bCheck out our new store!`

- **Title:**
  `/kaa add title &6Big News! | &eWe have a new event!`
  *(The pipe `|` character separates the title from the subtitle.)*

After adding a new message, you must run `/kaa reload` to apply the changes.

## Support

If you encounter any issues or have any questions, please open an issue on the [GitHub repository](https://github.com/your-repo/KartaAutoAnnouncer/issues).
