package net.silentium.engine.api.npc.depend;

import org.bukkit.entity.Player;

public interface ClickAction {
  void onClick(Player paramPlayer, ClickType paramClickType);
}
