package net.atlas.atlasbot;

import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static net.atlas.atlasbot.BotMain.CONFIG_FILE;
import static net.atlas.atlasbot.BotMain.GSON;

public class BotConfiguration {
    public String token;
    public Map<Long, List<Long>> trappedChannels;
    public Map<Long, List<ExclusionTarget>> excludedFromTraps;
    public Map<String, List<Long>> roleGroups;
    public BotConfiguration() {
        this.token = "<Your Token Here>";
        this.trappedChannels = new HashMap<>();
        this.roleGroups = new HashMap<>();
    }

    public boolean trapChannel(TextChannel channel) {
        List<Long> trapped = new ArrayList<>(trappedChannels.getOrDefault(channel.getGuild().getIdLong(), Collections.emptyList()));
        if (trapped.contains(channel.getIdLong())) return false;
        trapped.add(channel.getIdLong());
        trappedChannels.put(channel.getGuild().getIdLong(), trapped);
        save();
        return true;
    }

    public boolean exclude(long guildId, ExclusionTarget exclusionTarget) {
        List<ExclusionTarget> excluded = new ArrayList<>(excludedFromTraps.getOrDefault(guildId, Collections.emptyList()));
        if (excluded.stream().anyMatch(excludedT -> excludedT.equals(exclusionTarget))) return false;
        excluded.add(exclusionTarget);
        excludedFromTraps.put(guildId, excluded);
        return true;
    }

    public void save() {
        try {
            PrintWriter writer = new PrintWriter(CONFIG_FILE);
            GSON.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Failed to save config file: " + e);
        }
    }
}
