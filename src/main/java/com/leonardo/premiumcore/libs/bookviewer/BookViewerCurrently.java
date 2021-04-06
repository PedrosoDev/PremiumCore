package com.leonardo.premiumcore.libs.bookviewer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.leonardo.premiumcore.libs.ReflectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Method;
import java.util.List;

@Singleton
public class BookViewerCurrently implements BookViewer {

    private final Method getHandle;
    private final Method openBook;
    private final Method asNMSCopy;

    @Inject
    public BookViewerCurrently(@Named("player#getHandle") Method getHandle) throws NoSuchMethodException, ClassNotFoundException {
        this.getHandle = getHandle;
        this.openBook = ReflectionUtils.getMethod("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER, "openBook", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack"));
        this.asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), "asNMSCopy", ItemStack.class);
    }

    /**
     * Set the pages of the book in JSON format.
     *
     * @param metadata BookMeta of the Book ItemStack.
     * @param pages    Each page to be added to the book.
     */
    @Override
    public void setPages(BookMeta metadata, List<String> pages) {
        Object page;
        try {
            final List<Object> p = (List<Object>) ReflectionUtils.getField(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftMetaBook"), true, "pages").get(metadata);
            for (String text : pages) {
                page = ReflectionUtils.invokeMethod(ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent$ChatSerializer").newInstance(), "a", text);
                p.add(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open a "Virtual" Book ItemStack.
     *
     * @param i Book ItemStack.
     * @param p Player that will open the book.
     */
    @Override
    public void openBook(ItemStack i, Player p) {
        final ItemStack held = p.getItemInHand();
        try {
            p.setItemInHand(i);
            sendPacket(i, p);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        p.setItemInHand(held);
    }

    private void sendPacket(ItemStack i, Player p) throws ReflectiveOperationException {
        openBook.invoke(getHandle.invoke(p), getItemStack(i));
    }

    private Object getItemStack(ItemStack item) {
        try {
            return asNMSCopy.invoke(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
