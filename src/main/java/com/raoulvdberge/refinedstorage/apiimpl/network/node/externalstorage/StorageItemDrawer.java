package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StorageItemDrawer extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IDrawer> drawerSupplier;

    public StorageItemDrawer(NetworkNodeExternalStorage externalStorage, Supplier<IDrawer> drawerSupplier) {
        this.externalStorage = externalStorage;
        this.drawerSupplier = drawerSupplier;
    }

    @Override
    public int getCapacity() {
        IDrawer drawer = drawerSupplier.get();

        return drawer != null ? drawer.getMaxCapacity() : 0;
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        return getStacks(drawerSupplier.get());
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        return insert(externalStorage, drawerSupplier.get(), stack, size, simulate);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        return extract(drawerSupplier.get(), stack, size, flags, simulate);
    }

    @Override
    public int getStored() {
        IDrawer drawer = drawerSupplier.get();

        return drawer != null ? drawer.getStoredItemCount() : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    public static NonNullList<ItemStack> getStacks(@Nullable IDrawer drawer) {
        if (drawer != null && !drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return NonNullList.withSize(1, ItemHandlerHelper.copyStackWithSize(drawer.getStoredItemPrototype(), drawer.getStoredItemCount()));
        }

        return RSUtils.emptyNonNullList();
    }

    public static ItemStack insert(NetworkNodeExternalStorage externalStorage, @Nullable IDrawer drawer, @Nonnull ItemStack stack, int size, boolean simulate) {
        if (drawer != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            int stored = drawer.getStoredItemCount();
            int remainingSpace = drawer.getMaxCapacity(stack) - stored;

            int inserted = remainingSpace > size ? size : (remainingSpace <= 0) ? 0 : remainingSpace;

            if (!simulate && remainingSpace > 0) {
                if (drawer.isEmpty()) {
                    drawer.setStoredItem(stack, inserted);
                } else {
                    drawer.setStoredItemCount(stored + inserted);
                }
            }

            if (inserted == size) {
                return null;
            }

            int returnSize = size - inserted;

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) {
                returnSize = -returnSize;
            }

            return ItemHandlerHelper.copyStackWithSize(stack, returnSize);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    public static ItemStack extract(@Nullable IDrawer drawer, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        if (drawer != null && API.instance().getComparer().isEqual(stack, drawer.getStoredItemPrototype(), flags)) {
            if (size > drawer.getStoredItemCount()) {
                size = drawer.getStoredItemCount();
            }

            ItemStack stored = drawer.getStoredItemPrototype();

            if (!simulate) {
                drawer.setStoredItemCount(drawer.getStoredItemCount() - size);
            }

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }
}
