package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.FluidStorageContainerMenu;
import com.refinedmods.refinedstorage.blockentity.FluidStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidStorageBlockScreen extends StorageScreen<FluidStorageContainerMenu> {
    public FluidStorageBlockScreen(FluidStorageContainerMenu containerMenu, Inventory inventory, Component title) {
        super(
            containerMenu,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenSynchronizationParameters(
                null,
                NetworkNodeBlockEntity.REDSTONE_MODE,
                FluidStorageBlockEntity.COMPARE,
                FluidStorageBlockEntity.WHITELIST_BLACKLIST,
                FluidStorageBlockEntity.PRIORITY,
                FluidStorageBlockEntity.ACCESS_TYPE
            ),
            FluidStorageBlockEntity.STORED::getValue,
            () -> (long) ((FluidStorageBlockEntity) containerMenu.getBlockEntity()).getFluidStorageType().getCapacity()
        );
    }
}
