package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
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

	public static void encode(ToggleModule message, FriendlyByteBuf buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeEnum(message.moduleType);
	}

	public static ToggleModule decode(FriendlyByteBuf buf) {
		ToggleModule message = new ToggleModule();

		message.pos = BlockPos.of(buf.readLong());
		message.moduleType = buf.readEnum(ModuleType.class);
		return message;
	}

	public static void onMessage(ToggleModule message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Player player = ctx.get().getSender();
			BlockEntity be = player.level.getBlockEntity(pos);

			if (be instanceof IModuleInventory moduleInv && (!(be instanceof IOwnable ownable) || ownable.getOwner().isOwner(player))) {
				ModuleType moduleType = message.moduleType;
				LinkedAction linkedAction;

				if (moduleInv.isModuleEnabled(moduleType)) {
					moduleInv.removeModule(moduleType, true);
					linkedAction = LinkedAction.MODULE_REMOVED;
				}
				else {
					moduleInv.insertModule(moduleInv.getModule(moduleType), true);
					linkedAction = LinkedAction.MODULE_INSERTED;
				}

				if (be instanceof LinkableBlockEntity linkable)
					ModuleUtils.createLinkedAction(linkedAction, moduleInv.getModule(moduleType), linkable, true);

				if (be instanceof CustomizableBlockEntity)
					player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
