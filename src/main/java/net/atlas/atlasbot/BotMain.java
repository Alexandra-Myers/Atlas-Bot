package net.atlas.atlasbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.atlas.atlasbot.command.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;

public class BotMain {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static JDA JDA;
    public static File CONFIG_FILE;
    public static BotConfiguration CONFIGURATION;
    public static ArrayList stringOfChannels = new ArrayList<String>();
    public static void main(String[] args) throws LoginException, InterruptedException {
        String userHome = System.getProperty("user.home");
        CONFIG_FILE = new File(userHome, "atlas_bot/bot.json");
        try {
            if (!CONFIG_FILE.exists()) {
                Files.createDirectories(Path.of(userHome + "/atlas_bot"));
                CONFIG_FILE.createNewFile();
                PrintWriter writer = new PrintWriter(CONFIG_FILE);
                GSON.toJson(new BotConfiguration(), writer);
                writer.close();
            }
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));
            CONFIGURATION = GSON.fromJson(reader, BotConfiguration.class);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read config file: " + e);
            return;
        } catch (IOException e) {
            System.out.println("Failed to create config file: " + e);
            return;
        }
        JDABuilder builder = JDABuilder.createDefault(CONFIGURATION.token);

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.playing("Role Groups"));
        configureMemoryUsage(builder);
        EnumSet<GatewayIntent> intents = EnumSet.of(
                // Enables MessageReceivedEvent for guild (also known as servers)
                GatewayIntent.GUILD_MESSAGES,
                // Enables the event for private channels (also known as direct messages)
                GatewayIntent.DIRECT_MESSAGES,
                // Enables access to message.getContentRaw()
                GatewayIntent.MESSAGE_CONTENT,
                // Enables MessageReactionAddEvent for guild
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                // Enables MessageReactionAddEvent for private channels
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );
        builder.enableIntents(intents);

        JDA = builder.build();
        JDA.awaitReady();
        JDA.addEventListener(new ReadyListener());
        new AddRoleGroupCommand();
        new AddRoleToGroupCommand();
        new ExcludeFromBansCommand();
        new TrapChannelCommand();
        BaseSlashCommand.register();
        for (int i = 0; i <= 201; i++) {
            if(i == 200) {
                i = 0;
            }
        }
    }
    public static void configureMemoryUsage(JDABuilder builder) {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY);

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Disable presence updates and typing events
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(50);
    }
}