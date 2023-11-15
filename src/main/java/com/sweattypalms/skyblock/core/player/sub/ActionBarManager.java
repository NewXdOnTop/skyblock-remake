package com.sweattypalms.skyblock.core.player.sub;

import com.sweattypalms.skyblock.core.player.PlayerManager;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import com.sweattypalms.skyblock.core.player.sub.stats.Stats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import static com.sweattypalms.skyblock.core.helpers.PlaceholderFormatter.formatDouble;

public class ActionBarManager extends PlayerManager {

    public ActionBarManager(SkyblockPlayer player) {
        super(player);
    }

    /**
     * Triggered every 20 ticks
     */
    public void tick() {
        String space = "     ";
        String healthComponent = getHealthComponent();
        String defenceComponent = getDefenceComponent();
        defenceComponent = defenceComponent.isEmpty() ? "" : space + defenceComponent;
        String intelligenceComponent = space + getIntelligenceComponent();

        final PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + healthComponent + defenceComponent + intelligenceComponent + "\"}"), (byte) 2);
        ((CraftPlayer) this.skyblockPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);

    }

    private String getHealthComponent() {
        double maxHealth = this.skyblockPlayer.getStatsManager().getMaxStats().get(Stats.HEALTH);
        String healthString = formatDouble(maxHealth);
        double currentHealth = this.skyblockPlayer.getPlayer().getHealth();
        if (currentHealth > maxHealth) currentHealth = maxHealth;
        String currentHealthString = formatDouble(currentHealth);
        return ChatColor.RED + Stats.HEALTH.getSymbol() + " " + currentHealthString + " / " + healthString;
    }

    private String getDefenceComponent() {
        double maxDefence = this.skyblockPlayer.getStatsManager().getMaxStats().get(Stats.DEFENSE);
        if (maxDefence == 0) return "";
        String defenceString = formatDouble(maxDefence);
        return ChatColor.GREEN + Stats.DEFENSE.getSymbol() + " " + defenceString;
    }

    private String getIntelligenceComponent() {
        double maxIntelligence = this.skyblockPlayer.getStatsManager().getMaxStats().get(Stats.INTELLIGENCE);
        String intelligenceString = formatDouble(maxIntelligence);
        double currentIntelligence = this.skyblockPlayer.getStatsManager().getLiveStats().get(Stats.INTELLIGENCE);
        if (currentIntelligence > maxIntelligence) currentIntelligence = maxIntelligence;
        String currentIntelligenceString = formatDouble(currentIntelligence);

        return ChatColor.AQUA + Stats.INTELLIGENCE.getSymbol() + " " + currentIntelligenceString + " / " + intelligenceString;
    }
}
