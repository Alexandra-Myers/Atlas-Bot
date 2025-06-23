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

public class AddRoleToGroupCommand extends BaseSlashCommand {
    public AddRoleToGroupCommand() {
        super("add-role-to-group", "Creates a group of roles.", DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR),
                new OptionData(OptionType.ROLE, "role-to-add", "The role to add to the group.")
                        .setRequired(true),
                new OptionData(OptionType.STRING, "group-name", "The name of the group to add to.")
                        .setRequired(true)
        );
    }

    @Override
    public void commandTriggered(SlashCommandInteractionEvent event) {
        Role toAdd = event.getOption("role-to-add").getAsRole();
        String groupName = event.getOption("group-name").getAsString();
        if (BotMain.CONFIGURATION.roleGroups.containsKey(groupName)) {
            List<Long> added = new ArrayList<>(BotMain.CONFIGURATION.roleGroups.get(groupName));
            added.add(toAdd.getIdLong());
            BotMain.CONFIGURATION.roleGroups.put(groupName, added);
        } else {
            sendMessage("Invalid group name! Options: " + BotMain.CONFIGURATION.roleGroups.keySet().stream().toList(), event);
            return;
        }
        BotMain.CONFIGURATION.save();
        sendMessage("Success!", event);
    }
}
