package net.silentium.engine.core.menus;

import lombok.Getter;
import lombok.Setter;
import net.silentium.engine.core.menus.objects.MenuItem;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MenuConfig {

    public enum MenuType {
        SIMPLE,
        PAGINATED
    }

    private String name;
    private String title;
    private MenuType type = MenuType.SIMPLE;
    private List<String> commands;
    private List<String> rows;
    private Map<Integer, Map<String, MenuItem>> pages = new HashMap<>();

    private Sound openSound;
    private Sound closeSound;
    private int updateRate = -1;
}