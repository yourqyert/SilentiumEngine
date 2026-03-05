package net.silentium.engine.api.npc.npc.types;

import net.silentium.engine.api.npc.npc.NPC;
import org.bukkit.block.Block;

public interface EndermanNPC extends NPC {
    boolean isScreaming();
    void setScreaming(boolean screaming);

    boolean isCarryingBlock();
    void setCarryingBlock(boolean carrying);

    Block carriedBlock();
    void setCarriedBlock(Block block);
}