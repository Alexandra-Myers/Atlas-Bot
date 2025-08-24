package net.atlas.atlasbot;

import net.atlas.atlasbot.command.BaseSlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ReadyListener extends ListenerAdapter {
    public static final Emoji HEART = Emoji.fromUnicode("U+2764");
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (BaseSlashCommand.slashCommands.isEmpty()) return;
        String command = event.getName();

        for (BaseSlashCommand slashCommand : BaseSlashCommand.slashCommands) {
            if (slashCommand.name.equalsIgnoreCase(command))  {
                slashCommand.commandTriggered(event);
                return;
            }
        }
    }
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
        if (BotMain.CONFIGURATION.trappedChannels.containsKey(event.getGuild().getIdLong()) && BotMain.CONFIGURATION.trappedChannels.get(event.getGuild().getIdLong()).contains(channel.getIdLong())) {
            if (!(author.isBot() || isExcludedFromBan(event.getGuild().getIdLong(), event.getMember()))) {
                event.getGuild().ban(author, 1).queue();
                event.getGuild().unban(author).queue();
            }
        }
        if (!author.isBot()) {
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
                            .sendMessageEmbeds(new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl()).addField("Thread: " + channel.getAsMention(), message.getContentDisplay().split("#Atlt\s?", 2)[1] + " - " + author.getAsMention() + ", " + message.getTimeCreated().getYear(), true).setColor(Color.MAGENTA).build())
                            .addActionRow(Button.of(ButtonStyle.LINK, message.getJumpUrl(), "Original Message"))
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
                if (string.matches("getGithub")) {
                    try {
                        channel
                                .sendMessage("https://github.com/Alexandra-Myers?tab=repositories")
                                .submit()
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                } else if (string.matches("help")) {
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

    private boolean isExcludedFromBan(long guildId, Member author) {
        if (BotMain.CONFIGURATION.excludedFromTraps.get(guildId).stream().anyMatch(exclusionTarget -> switch (exclusionTarget.type) {
            case USER -> author.getId().equals(exclusionTarget.id);
            case ROLE -> author.getRoles().stream().anyMatch(role -> role.getId().equals(exclusionTarget.id));
            case ROLE_GROUP -> author.getRoles().stream().anyMatch(role -> BotMain.CONFIGURATION.roleGroups.values().stream().anyMatch(ids -> ids.contains(role.getIdLong())));
        })) return true;
        return false;
    }
}
