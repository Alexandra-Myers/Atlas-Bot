package net.atlas.atlasbot.command;

import net.atlas.atlasbot.BotMain;
import net.atlas.atlasbot.ExclusionTarget;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ExcludeFromBansCommand extends BaseSlashCommand {
    public ExcludeFromBansCommand() {
        super("exclude_from_bot_trap", "Excludes users|roles|role_groups from spam bot traps.", DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR),
                new OptionData(OptionType.USER, "user", "A user to exclude.")
                        .setRequired(false),
                new OptionData(OptionType.ROLE, "role", "A role to exclude.")
                        .setRequired(false),
                new OptionData(OptionType.STRING, "role-group", "A group of roles to exclude.")
                        .setRequired(false)
        );
    }

    @Override
    public void commandTriggered(SlashCommandInteractionEvent event) {
        OptionMapping user = event.getOption("user");
        OptionMapping role = event.getOption("role");
        OptionMapping roleGroup = event.getOption("role-group");
        boolean anyChanged = false;
        if (user != null) anyChanged = BotMain.CONFIGURATION.exclude(event.getGuild().getIdLong(), new ExclusionTarget(ExclusionTarget.ExclusionType.USER, user.getAsUser().getId()));
        if (role != null) anyChanged |= BotMain.CONFIGURATION.exclude(event.getGuild().getIdLong(), new ExclusionTarget(ExclusionTarget.ExclusionType.ROLE, role.getAsRole().getId()));
        if (roleGroup != null) {
            if (!BotMain.CONFIGURATION.roleGroups.containsKey(roleGroup.getAsString())) {
                sendMessage("Invalid group name! Options: " + BotMain.CONFIGURATION.roleGroups.keySet().stream().toList(), event);
                return;
            }
            anyChanged |= BotMain.CONFIGURATION.exclude(event.getGuild().getIdLong(), new ExclusionTarget(ExclusionTarget.ExclusionType.ROLE_GROUP, roleGroup.getAsString()));
        }

        if (anyChanged) {
            sendMessage("Success!", event);
            BotMain.CONFIGURATION.save();
        } else {
            sendMessage("But nothing changed.", event);
        }
    }
}