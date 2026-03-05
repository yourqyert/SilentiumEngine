package net.silentium.engine.core.menus.provider;

import me.xflyiwnl.colorfulgui.object.PaginatedGui;
import net.silentium.engine.core.menus.MenuConfig;
import net.silentium.engine.core.menus.RequirementService;
import net.silentium.engine.core.menus.handler.ActionHandler;
import org.bukkit.entity.Player;

public class GenericPaginatedProvider extends AbstractMenuProvider<PaginatedGui> {
    public GenericPaginatedProvider(Player player, MenuConfig menuConfig, int page, ActionHandler actionHandler, RequirementService requirementService) {
        super(player, menuConfig, page, actionHandler, requirementService);
    }
}