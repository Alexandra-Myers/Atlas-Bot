package net.atlas.atlasbot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static net.atlas.atlasbot.BotMain.JDA;

public class BaseSlashCommand {
    public String name;
    public String description;
    OptionData[] optionData;
    DefaultMemberPermissions permissions;
    public static ArrayList<BaseSlashCommand> slashCommands = new ArrayList<>();

    public BaseSlashCommand(String name, String description, DefaultMemberPermissions permissions, @NotNull OptionData... optionData) {
        this.name = name;
        this.description = description;
        this.optionData = optionData;
        this.permissions = permissions;

        slashCommands.add(this);
    }

    public static void register() {
        CommandData[] commands = slashCommands.stream().map(baseSlashCommand -> Commands.slash(baseSlashCommand.name, baseSlashCommand.description)
                .addOptions(baseSlashCommand.optionData).setDefaultPermissions(baseSlashCommand.permissions)).toArray(CommandData[]::new);
        JDA.updateCommands().addCommands(
                commands
        ).queue();
    }

    public void commandTriggered(SlashCommandInteractionEvent event) {
        this.sendMessage("This command hasn't been fully implemented yet!", event);
    }

    public void sendMessage(String message, SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        event.getHook().editOriginal(message).queue();
    }
}