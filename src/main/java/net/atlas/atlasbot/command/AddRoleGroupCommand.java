package net.atlas.atlasbot.command;

import net.atlas.atlasbot.BotMain;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class AddRoleGroupCommand extends BaseSlashCommand {
    public AddRoleGroupCommand() {
        super("create-role-group", "Creates a group of roles.", DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR),
                new OptionData(OptionType.ROLE, "first-initial-role", "The first role to include in the group.")
                        .setRequired(true),
                new OptionData(OptionType.ROLE, "second-initial-role", "The second role to include in the group.")
                        .setRequired(true),
                new OptionData(OptionType.STRING, "group-name", "The name of the group.")
                        .setRequired(true)
        );
    }

    @Override
    public void commandTriggered(SlashCommandInteractionEvent event) {
        Role firstRole = event.getOption("first-initial-role").getAsRole();
        Role secondRole = event.getOption("second-initial-role").getAsRole();
        String groupName = event.getOption("group-name").getAsString();
        if (BotMain.CONFIGURATION.roleGroups.containsKey(groupName)) {
            sendMessage("Cannot create an already existing group!", event);
            return;
        }
        List<Long> rolesInGroup = new ArrayList<>();
        rolesInGroup.add(firstRole.getIdLong());
        rolesInGroup.add(secondRole.getIdLong());
        BotMain.CONFIGURATION.roleGroups.put(groupName, rolesInGroup);
        BotMain.CONFIGURATION.save();
        sendMessage("Success!", event);
    }
}
