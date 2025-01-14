package pw.kaboom.extras.commands;

import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGetJSON implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <message ..>", NamedTextColor.RED));
            return true;
        }

        final String message = String.join(" ", args);
        Component createdComponent = LegacyComponentSerializer
            .legacyAmpersand()
            .deserialize(message);

        String asJson = GsonComponentSerializer.gson().serialize(createdComponent);

        Component feedback = Component.empty()
                .append(Component.text("Your component as JSON (click to copy): "))
                .append(Component.text(asJson, NamedTextColor.GREEN))
                .clickEvent(ClickEvent.copyToClipboard(asJson));

        sender.sendMessage(feedback);
        return true;
    }
}
