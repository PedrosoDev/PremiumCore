package com.leonardo.premiumcore.libs;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class PlayerCommons {

    public static void giveItem(Player p, final ItemStack itemStack, Predicate<ItemStack> predicate, Integer amount) {
        final int stackMax = itemStack.getMaxStackSize();
        for (ItemStack itemStack1 : p.getInventory().getContents()) {
            if (amount == 0) return;
            if (itemStack1 == null) continue;
            if ((itemStack.isSimilar(itemStack1) || (predicate != null && predicate.test(itemStack1))) && itemStack1.getAmount() < stackMax) {
                if (itemStack1.getAmount() + amount <= stackMax) {
                    itemStack1.setAmount(itemStack1.getAmount() + amount);
                    return;
                }
                amount = amount + itemStack1.getAmount() - stackMax;
                itemStack1.setAmount(stackMax);
            }
        }
        if (amount == 0) return;
        if (amount <= stackMax) {
            itemStack.setAmount(amount);
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(itemStack);
            } else p.getWorld().dropItem(p.getLocation(), itemStack);
            return;
        }
        final int items = amount / stackMax;
        final int rest = amount % stackMax;
        final ItemStack newItemStack = itemStack.clone();
        newItemStack.setAmount(rest);
        if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(newItemStack);
        } else p.getWorld().dropItem(p.getLocation(), newItemStack);
        for (int item = items; item > 0; item--) {
            final ItemStack newItem = itemStack.clone();
            newItem.setAmount(stackMax);
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(newItem);
            } else p.getWorld().dropItem(p.getLocation(), newItem);
        }
    }

    public boolean haveSpace(final Player p, final ItemStack itemStack, Predicate<ItemStack> predicate, Integer amount) {
        final int stackMax = itemStack.getMaxStackSize();
        for (ItemStack itemStack1 : p.getInventory().getContents()) {
            if (itemStack1 == null) {
                amount -= stackMax;
                continue;
            }
            if (itemStack1.isSimilar(itemStack) || (predicate != null && predicate.test(itemStack1))) {
                amount -= stackMax - itemStack1.getAmount();
            }
        }
        return amount <= 0;
    }

}
