package com.sweattypalms.skyblock.core.items.types.none.weapons;

import com.sweattypalms.skyblock.core.events.def.SkyblockInteractEvent;
import com.sweattypalms.skyblock.core.items.builder.Rarity;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItemType;
import com.sweattypalms.skyblock.core.items.builder.abilities.Ability;
import com.sweattypalms.skyblock.core.items.builder.abilities.IHasAbility;
import com.sweattypalms.skyblock.core.items.builder.abilities.TriggerType;
import com.sweattypalms.skyblock.core.items.builder.abilities.types.ICooldown;
import com.sweattypalms.skyblock.core.items.builder.abilities.types.ITriggerableAbility;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import com.sweattypalms.skyblock.core.player.sub.stats.Stats;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectOfTheJerry extends SkyblockItem implements IHasAbility {

    public static final String ID = "aspect_of_the_jerry";

    private static final Map<Stats, Double> stats = new HashMap<>(Map.of(
            Stats.DAMAGE, 1d
    ));

    public AspectOfTheJerry() {
        super(
                ID,
                "Aspect of the Jerry",
                Material.WOOD_SWORD,
                null,
                stats,
                Rarity.COMMON,
                SkyblockItemType.SWORD
        );
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of(new SoundAbility());
    }

    public static class SoundAbility implements ITriggerableAbility, ICooldown {

        @Override
        public long getCooldown(SkyblockPlayer skyblockPlayer) {
            return 100;
        }

        @Override
        public TriggerType getTriggerType() {
            return TriggerType.RIGHT_CLICK;
        }

        @Override
        public boolean trigger(Event event) {
            return event instanceof SkyblockInteractEvent skyblockInteractEvent &&
                    skyblockInteractEvent.getInteractType() == this.getTriggerType() &&
                    !isOnCooldown(skyblockInteractEvent.getSkyblockPlayer());
        }

        @Override
        public void apply(Event _e) {
            if (!(_e instanceof SkyblockInteractEvent event)) return;
            ICooldown.super.apply(_e);
            SkyblockPlayer skyblockPlayer = event.getSkyblockPlayer();
            skyblockPlayer.getPlayer().playSound(skyblockPlayer.getPlayer().getLocation(), Sound.VILLAGER_IDLE, 1.0F, 1.0F);
        }

        @Override
        public String getName() {
            return "Parley";
        }

        @Override
        public List<String> getDescription() {
            return List.of("$7Channel your inner Jerry.");
        }
    }
}
