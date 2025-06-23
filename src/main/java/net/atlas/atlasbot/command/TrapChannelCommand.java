package net.atlas.atlasbot.command;

import net.atlas.atlasbot.BotMain;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TrapChannelCommand extends BaseSlashCommand {
    public TrapChannelCommand() {
        super("add-bot-trap", "Assigns a channel to trap spam bots with.", DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL),
                new OptionData(OptionType.CHANNEL, "target", "The channel which will be trapped.")
                        .setRequired(true).setChannelTypes(ChannelType.TEXT)
        );
    }

    @Override
    public void commandTriggered(SlashCommandInteractionEvent event) {
        TextChannel channel = event.getOption("target").getAsChannel().asTextChannel();
        if (!BotMain.CONFIGURATION.trapChannel(channel)) {
            sendMessage("Cannot trap the same channel twice!", event);
            return;
        }

        sendMessage("Success!", event);
        channel.sendMessage("This Channel is a **TRAP FOR BOTS**. Anyone who sends a message in here will be instantly banned. PLEASE DO NOT SPEAK IN HERE.").queue();
    }
}
