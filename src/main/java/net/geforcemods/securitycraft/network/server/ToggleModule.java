package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleModule {
	private BlockPos pos;
	private ModuleType moduleType;

	public ToggleModule() {}

	public ToggleModule(BlockPos pos, ModuleType moduleType) {
		this.pos = pos;
		this.moduleType = moduleType;
	}

	public ToggleModule(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		moduleType = buf.readEnum(ModuleType.class);
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeEnum(moduleType);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof IModuleInventory && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player))) {
			IModuleInventory moduleInv = (IModuleInventory) be;

			if (moduleInv.isModuleEnabled(moduleType)) {
				moduleInv.removeModule(moduleType, true);

				if (be instanceof LinkableBlockEntity) {
					LinkableBlockEntity linkable = (LinkableBlockEntity) be;

					linkable.propagate(new ILinkedAction.ModuleRemoved(moduleType, true), linkable);
				}
			}
			else {
				moduleInv.insertModule(moduleInv.getModule(moduleType), true);

				if (be instanceof LinkableBlockEntity) {
					LinkableBlockEntity linkable = (LinkableBlockEntity) be;
					ItemStack stack = moduleInv.getModule(moduleType);

					linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), true), linkable);
				}
			}
			if (be instanceof CustomizableBlockEntity)
				player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
