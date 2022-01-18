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
        this.p = p;
        PlayerInventory inv = p.getInventory();
        contents = inv.getContents();
        armorContents = inv.getArmorContents();
    }

    public static void save(Player player){
        inventories.put(player, new SavedInventory(player));
    }

    public static void restore(Player player){
        remove(player).restore();
    }

    public static SavedInventory get(Player p) {
        return inventories.get(p);
    }

    public static SavedInventory remove(Player p) {
        return inventories.remove(p);
    }

    public void restore() {
        p.getOpenInventory().setCursor(null);
        p.getInventory().clear();
        p.getInventory().setContents(contents);
        p.getInventory().setArmorContents(armorContents);
    }
}
