package com.sweattypalms.skyblock.core.items.types.dungeon.items;

import com.cryptomorin.xseries.XMaterial;
import com.sweattypalms.skyblock.core.items.builder.Rarity;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItemType;
import com.sweattypalms.skyblock.core.items.builder.item.IDungeon;
import com.sweattypalms.skyblock.core.player.sub.stats.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hyperion extends SkyblockItem implements IDungeon {

    public static final String ID = "hyperion";
    private static final Map<Stats, Double> stats = new HashMap<>(Map.of(
            Stats.DAMAGE, 1d
    ));

    public Hyperion() {
        super(
                ID,
                "Hyperion",
                XMaterial.IRON_SWORD.parseMaterial(),
                List.of("$7Deals +$c50% $7damage to",
                        "$7Withers, Grants $c+1 ❁ Damage",
                        "$7and $a+2 $b ✎ Intelligence",
                        "$7per $cCatacombs $7levels.",
                        "$f",
                        "$7Your Catacombs Level: $c0"),
                stats,
                Rarity.LEGENDARY,
                SkyblockItemType.SWORD
        );
    }
}
