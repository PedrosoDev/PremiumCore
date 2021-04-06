package com.leonardo.premiumcore.libs.bookviewer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public interface BookViewer {

    void setPages(BookMeta metadata, List<String> pages);
    void openBook(ItemStack i, Player p);
}
