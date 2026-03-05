package net.silentium.engine.core.menus.provider;

import me.xflyiwnl.colorfulgui.ColorfulGUI;
import me.xflyiwnl.colorfulgui.object.Gui;
import me.xflyiwnl.colorfulgui.object.GuiItem;
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.core.menus.MenuConfig;
import net.silentium.engine.core.menus.RequirementService;
import net.silentium.engine.core.menus.handler.ActionHandler;
import net.silentium.engine.core.menus.objects.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.Map;

public abstract class AbstractMenuProvider<T extends Gui> extends ColorfulProvider<T> {

    private final MenuConfig menuConfig;
    private final int page;
    private final ActionHandler actionHandler;
    private final RequirementService requirementService;
    private final ColorfulGUI colorfulGUI = SilentiumEngine.getInstance().getColorfulGUI();

    public AbstractMenuProvider(Player player, MenuConfig menuConfig, int page, ActionHandler actionHandler, RequirementService requirementService) {
        super(player);
        this.menuConfig = menuConfig;
        this.page = page;
        this.actionHandler = actionHandler;
        this.requirementService = requirementService;
    }

    @Override
    public void init() {
        Map<String, MenuItem> pageItems = menuConfig.getPages().get(page);
        if (pageItems == null) {
            return;
        }

        for (MenuItem menuItem : pageItems.values()) {
            if (menuItem.getMask() != null && requirementService.checkAll(getPlayer(), menuItem.getRequirements())) {
                GuiItem guiItem = colorfulGUI.staticItem()
                        .from(menuItem.getItemStack())
                        .name(menuItem.getName())
                        .lore(menuItem.getLore())
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                        .action(event -> actionHandler.process(getPlayer(), menuItem.getActions(), menuConfig, page))
                        .build();
                getGui().addMask(menuItem.getMask(), guiItem);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}