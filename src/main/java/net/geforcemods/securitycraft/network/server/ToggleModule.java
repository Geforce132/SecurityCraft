package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleModule {
	private BlockPos pos;
	private ModuleType moduleType;

	public ToggleModule() {}

	public ToggleModule(BlockPos pos, ModuleType moduleType) {
		this.pos = pos;
		this.moduleType = moduleType;
	}

	public ToggleModule(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		moduleType = buf.readEnum(ModuleType.class);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeEnum(moduleType);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		BlockEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof IModuleInventory moduleInv && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			if (moduleInv.isModuleEnabled(moduleType)) {
				moduleInv.removeModule(moduleType, true);

				if (be instanceof LinkableBlockEntity linkable)
					linkable.propagate(new ILinkedAction.ModuleRemoved(moduleType, true), linkable);
			}
			else {
				moduleInv.insertModule(moduleInv.getModule(moduleType), true);

				if (be instanceof LinkableBlockEntity linkable) {
					ItemStack stack = moduleInv.getModule(moduleType);

					linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), true), linkable);
				}
			}

			if (be instanceof CustomizableBlockEntity)
				player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
