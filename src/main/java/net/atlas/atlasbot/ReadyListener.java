package net.atlas.atlasbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ThreadManager;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.concurrent.ExecutionException;

public class ReadyListener extends ListenerAdapter {
    public static ArrayList<RoleGroup> roleGroups = new ArrayList();
    public static final Emoji HEART = Emoji.fromUnicode("U+2764");
    // This overrides the method called onMessageReceived in the ListenerAdapter class
    // Your IDE (such as intellij or eclipse) can automatically generate this override for you, by simply typing "onMessage" and auto-completing it!
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        // The user who sent the message
        User author = event.getAuthor();
        // This is a special class called a "union", which allows you to perform specialization to more concrete types such as TextChannel or NewsChannel
        MessageChannelUnion channel = event.getChannel();
        // The actual message sent by the user, this can also be a message the bot sent itself, since you *do* receive your own messages after all
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(message.getMember().hasPermission(Permission.ADMINISTRATOR) && author.isBot() && (content.startsWith("A!addRoleToGroup") || content.startsWith("A!addGroupRole"))) {
            int i = content.indexOf(",");
            String startArgs = content.substring(i + 1);
            int secondIndex = startArgs.indexOf(",");
            String roleToAdd = startArgs.substring(0, secondIndex - 1);
            String groupName = roleToAdd.substring(secondIndex + 1);
            for(RoleGroup group : roleGroups) {
                if(group.getName().equals(groupName)) {
                    for(Role role : event.getGuild().getRoles()) {
                        if(role.getName().equals(roleToAdd)) {
                            group.rolesInGroup.add(role);
                        }
                    }
                }
            }
        }
        if(message.getMember().hasPermission(Permission.ADMINISTRATOR) && author.isBot() && (content.startsWith("A!linkRoles") || content.startsWith("A!createGroup"))) {
            int i = content.indexOf(",");
            String startArgs = content.substring(i + 1);
            int secondIndex = startArgs.indexOf(",");
            String secondArgs = startArgs.substring(secondIndex + 1);
            int thirdIndex = secondArgs.indexOf(",");
            String firstRole = startArgs.substring(0, secondIndex - 1);
            String secondRole = startArgs.substring(secondIndex + 1, thirdIndex - 1);
            String groupName = secondRole.substring(thirdIndex + 1);
            boolean groupExists = false;
            Role groupRole = null;
            for(RoleGroup group : roleGroups) {
                if(group.getName().equals(groupName)) {
                    try {
                        channel
                                .sendMessage("Cannot link roles into an already existing group!")
                                .submit()
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
            for(Role role : event.getGuild().getRoles()) {
                if(role.getName().equals(firstRole) || role.getName().equals(secondRole)) {
                    if(!groupExists) {
                        try {
                            groupRole = event.getGuild().createRole().setName(groupName).setColor(role.getColor()).setMentionable(role.isMentionable()).setHoisted(role.isHoisted()).submit().get();
                            groupExists = true;
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    RoleGroup roleGroup = new RoleGroup(groupRole.getIdLong(), role.getGuild());
                    roleGroup.rolesInGroup.add(role);
                }
            }
        }
        if(!author.isBot()) {
            boolean bl = false;
            if(content.startsWith("#AThreadListenToggle") || content.startsWith("#Atlt")) {
                bl = !bl;
            }
            if(bl && channel.getType().isThread() && !content.startsWith("A!")) {
                GuildMessageChannelUnion parentChannel =
                        channel
                                .asThreadChannel()
                                .getParentMessageChannel();
                try {
                    parentChannel
                            .sendMessageEmbeds(new EmbedBuilder().addField("Thread: " + channel.getName(), author.getName() + ": " + message.getContentDisplay(), true).setColor(Color.MAGENTA).build())
                            .submit()
                            .get()
                            .addReaction(HEART)
                            .complete();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            if(content.startsWith("A!")) {
                int i = content.indexOf("!");
                String string = content.substring(i + 1);
                if(string.matches("getGithub")) {
                    try {
                        channel
                                .sendMessage("https://github.com/Alexandra-Myers?tab=repositories")
                                .submit()
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }else if(string.matches("help")) {
                    try {
                        channel
                                .sendMessageEmbeds(new EmbedBuilder()
                                        .addField("Get our Github", "getGithub", true)
                                        .addField("Toggle bot listening to messages in the thread", "#AThreadListenToggle or #Atlt", true)
                                        .addField("!!ADMINISTRATOR ONLY!! Link roles together as a group. Parameters: Names of two roles to add, separated by commas, and the name of the group to create (Note: This cannot be the name of a role that already exists. To add to a group, use the appropriate command).", "A!linkRoles", true)
                                        .addField("!!ADMINISTRATOR ONLY!! Add roles to a group. Parameters: name of the role to add, separated by a comma, and the name of the group to add to.", "A!linkRoles", true)
                                        .setColor(Color.CYAN)
                                        .build())
                                .submit()
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}