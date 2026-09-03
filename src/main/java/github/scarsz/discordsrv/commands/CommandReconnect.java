/*
 * DiscordSRV - https://github.com/DiscordSRV/DiscordSRV
 *
 * Copyright (C) 2016 - 2024 Austin "Scarsz" Shapiro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package github.scarsz.discordsrv.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.LangUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.dv8tion.jda.api.JDA;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandReconnect {

    @Command(commandNames = { "reconnect" },
             helpMessage = "Reconnects DiscordSRV to Discord",
             permission = "discordsrv.reconnect"
    )
    public static void execute(CommandSender sender, String[] args) {
        JDA jda = DiscordSRV.getPlugin().getJda();

        if (jda != null) {
            String status = jda.getStatus().name();
            DiscordSRV.info("Current Discord connection status: " + status);

            // If already connected, ask for confirmation or just inform
            if (jda.getStatus() == JDA.Status.CONNECTED) {
                MessageUtil.sendMessage(sender, ChatColor.GREEN + "DiscordSRV is already connected to Discord (Status: " + status + ").");
                MessageUtil.sendMessage(sender, ChatColor.YELLOW + "If you still want to reconnect, this will restart the connection.");
            }
        } else {
            MessageUtil.sendMessage(sender, ChatColor.YELLOW + "DiscordSRV has no active JDA instance. Starting connection...");
        }

        MessageUtil.sendMessage(sender, ChatColor.AQUA + "Attempting to reconnect to Discord...");

        // Run reconnect asynchronously and notify when done
        DiscordSRV.getPlugin().reconnectToDiscord(() -> {
            // This runs on the reconnect thread, so we need to schedule for main thread
            org.bukkit.Bukkit.getScheduler().runTask(DiscordSRV.getPlugin(), () -> {
                JDA newJda = DiscordSRV.getPlugin().getJda();
                if (newJda != null && newJda.getStatus() == JDA.Status.CONNECTED) {
                    MessageUtil.sendMessage(sender, ChatColor.GREEN + "Successfully reconnected to Discord!");
                } else {
                    MessageUtil.sendMessage(sender, ChatColor.RED + "Failed to reconnect to Discord. Check console for details.");
                }
            });
        });

        MessageUtil.sendMessage(sender, ChatColor.GRAY + "Reconnection process started. Check console for progress.");
    }

}
