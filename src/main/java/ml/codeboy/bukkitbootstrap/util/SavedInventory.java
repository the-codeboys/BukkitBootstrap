package ml.codeboy.bukkitbootstrap.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class SavedInventory {
    private static final HashMap<Player, SavedInventory> inventories = new HashMap<>();
    private final ItemStack[] contents, armorContents;
    private final Player p;

    public SavedInventory(Player p) {
        inventories.put(p, this);
        this.p = p;
        PlayerInventory inv = p.getInventory();
        contents = inv.getContents();
        armorContents = inv.getArmorContents();
        inv.clear();
    }

    public static void save(Player player){
        new SavedInventory(player);
    }

    public static void restore(Player player){
        get(player).restore();
    }

    public static SavedInventory get(Player p) {
        return inventories.get(p);
    }

    public void restore() {
        p.getOpenInventory().setCursor(null);
        p.closeInventory();
        p.getInventory().clear();
        p.getInventory().setContents(contents);
        p.getInventory().setArmorContents(armorContents);
    }
}