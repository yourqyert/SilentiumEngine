package net.silentium.engine.core.menus.provider;

import me.xflyiwnl.colorfulgui.object.Gui;
import net.silentium.engine.core.menus.MenuConfig;
import net.silentium.engine.core.menus.RequirementService;
import net.silentium.engine.core.menus.handler.ActionHandler;
import org.bukkit.entity.Player;

public class GenericSimpleProvider extends AbstractMenuProvider<Gui> {
    public GenericSimpleProvider(Player player, MenuConfig menuConfig, int page, ActionHandler actionHandler, RequirementService requirementService) {
        super(player, menuConfig, page, actionHandler, requirementService);
    }
}