package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.entity.Player;

public interface Action {
    Action none = (p) -> {
    };

    void execute(Player player);
}