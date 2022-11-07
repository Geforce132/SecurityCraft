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

	public static void encode(ToggleModule message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeEnum(message.moduleType);
	}

	public static ToggleModule decode(PacketBuffer buf) {
		ToggleModule message = new ToggleModule();

		message.pos = BlockPos.of(buf.readLong());
		message.moduleType = buf.readEnum(ModuleType.class);
		return message;
	}

	public static void onMessage(ToggleModule message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			TileEntity be = player.level.getBlockEntity(pos);

			if (be instanceof IModuleInventory && (!(be instanceof IOwnable) || ((IOwnable) be).getOwner().isOwner(player))) {
				IModuleInventory moduleInv = (IModuleInventory) be;
				ModuleType moduleType = message.moduleType;

				if (moduleInv.isModuleEnabled(moduleType)) {
					moduleInv.removeModule(moduleType, true);

					if (be instanceof LinkableBlockEntity) {
						LinkableBlockEntity linkable = (LinkableBlockEntity) be;

						linkable.createLinkedBlockAction(new ILinkedAction.ModuleRemoved(moduleType, true), linkable);
					}
				}
				else {
					moduleInv.insertModule(moduleInv.getModule(moduleType), true);

					if (be instanceof LinkableBlockEntity) {
						LinkableBlockEntity linkable = (LinkableBlockEntity) be;
						ItemStack stack = moduleInv.getModule(moduleType);

						linkable.createLinkedBlockAction(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), true), linkable);
					}
				}
				if (be instanceof CustomizableBlockEntity)
					player.level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
