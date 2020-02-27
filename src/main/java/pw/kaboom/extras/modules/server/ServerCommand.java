package pw.kaboom.extras.modules.server;

import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public final class ServerCommand implements Listener {
	public static String checkCommand(final CommandSender sender, final String command, final boolean isConsoleCommand) {
		final String[] arr = command.split(" ");
		String commandName = arr[0].toLowerCase();

		if (isConsoleCommand) {
			commandName = "/" + arr[0].toLowerCase();
		}

		try {
			switch (commandName) {
				case "/minecraft:execute":
				case "/execute":
					if (arr.length >= 2) {
						int asAtCount = 0;

						for (int i = 1; i < arr.length; i++) {
							if ("run".equalsIgnoreCase(arr[i])) {
								if (i + 1 < arr.length) {
									if ("execute".equalsIgnoreCase(arr[i + 1])
											|| "clone".equalsIgnoreCase(arr[i + 1])
											|| "fill".equalsIgnoreCase(arr[i + 1])
											|| "particle".equalsIgnoreCase(arr[i + 1])
											|| "say".equalsIgnoreCase(arr[i + 1])
											|| "spreadplayers".equalsIgnoreCase(arr[i + 1])
											|| "stop".equalsIgnoreCase(arr[i + 1])
											|| "tellraw".equalsIgnoreCase(arr[i + 1])) {
										Command.broadcastCommandMessage(sender, "Forbidden execute command detected");
										return "cancel";
									} else if (i + 3 < arr.length
											&& "gamerule".equalsIgnoreCase(arr[i + 1])) {
										if ("randomTickSpeed".equalsIgnoreCase(arr[i + 2])
												&& Double.parseDouble(arr[i + 3]) > 6) {
											return command.replaceFirst("(?i)" + "randomTickSpeed " + arr[i + 3], "randomTickSpeed 6");
										}
									} else if ("give".equalsIgnoreCase(arr[i + 1])) {
										if (Double.parseDouble(arr[arr.length - 1]) > 64) {
											arr[arr.length - 1] = "64";
											return String.join(" ", arr);
										}
									}
								}
								break;
							}

							if ("as".equalsIgnoreCase(arr[i])
									|| "at".equalsIgnoreCase(arr[i])) {
								asAtCount++;
							}
						}

						if (asAtCount >= 2) {
							Command.broadcastCommandMessage(sender, "Forbidden execute pattern detected");
							return "cancel";
						}
					}
					break;
				case "/minecraft:gamerule":
				case "/gamerule":
					if (arr.length >= 3) {
						if ("randomTickSpeed".equalsIgnoreCase(arr[1])
								&& Double.parseDouble(arr[2]) > 6) {
							return command.replaceFirst(arr[2], "6");
						}
					}
					break;
				case "/minecraft:give":
				case "/give":
					if (Double.parseDouble(arr[arr.length - 1]) > 64) {
						arr[arr.length - 1] = "64";
						return String.join(" ", arr);
					}
					break;
				case "/minecraft:particle":
				case "/particle":
					if (arr.length >= 10
							&& Double.parseDouble(arr[9]) > 10) {
						arr[9] = "10";
						return String.join(" ", arr);
					}
					break;
				case "/minecraft:spreadplayers":
				case "/spreadplayers":
					if (arr.length >= 5) {
						if (Double.parseDouble(arr[3]) > 0) {
							arr[3] = "0";
						}
						if (Double.parseDouble(arr[4]) < 8) {
							arr[4] = "8";
						}
						if (Double.parseDouble(arr[4]) > 50) {
							arr[4] = "50";
						}

						return String.join(" ", arr);
					}
					break;
				default:
					break;
			}
		} catch (NumberFormatException exception) {
			// Do nothing
		}
		return null;
	}

	@EventHandler
	void onServerCommand(final ServerCommandEvent event) {
		final CommandSender sender = event.getSender();

		if (sender instanceof BlockCommandSender) {
			final CommandBlock commandBlock = (CommandBlock) ((BlockCommandSender) sender).getBlock().getState();

			commandBlock.setCommand("");
			commandBlock.update();
		}

		final String command = event.getCommand();
		final boolean isConsoleCommand = true;
		final String checkedCommand = checkCommand(sender, command, isConsoleCommand);

		if (checkedCommand != null) {
			if ("cancel".equals(checkedCommand)) {
				event.setCancelled(true);
			} else {
				event.setCommand(checkedCommand);
			}
		}
	}
}
