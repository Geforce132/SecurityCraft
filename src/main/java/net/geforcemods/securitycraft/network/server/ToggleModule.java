package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleModule implements CustomPacketPayload {
	public static final Type<ToggleModule> TYPE = new Type<>(SecurityCraft.resLoc("toggle_module"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleModule> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public ToggleModule decode(RegistryFriendlyByteBuf buf) {
			if (buf.readBoolean())
				return new ToggleModule(buf.readBlockPos(), buf.readEnum(ModuleType.class));
			else
				return new ToggleModule(buf.readVarInt(), buf.readEnum(ModuleType.class));
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, ToggleModule packet) {
			boolean hasPos = packet.pos != null;

			buf.writeBoolean(hasPos);

			if (hasPos)
				buf.writeBlockPos(packet.pos);
			else
				buf.writeVarInt(packet.entityId);

			buf.writeEnum(packet.moduleType);
		}
	};
	private BlockPos pos;
	private ModuleType moduleType;
	private int entityId;

	public ToggleModule(BlockPos pos, ModuleType moduleType) {
		this.pos = pos;
		this.moduleType = moduleType;
	}

	public ToggleModule(int entityId, ModuleType moduleType) {
		this.entityId = entityId;
		this.moduleType = moduleType;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		IModuleInventory moduleInv = getModuleInventory(level);

		if (moduleInv != null && (!(moduleInv instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			if (moduleInv.isModuleEnabled(moduleType)) {
				moduleInv.removeModule(moduleType, true);

				if (moduleInv instanceof LinkableBlockEntity linkable)
					linkable.propagate(new ILinkedAction.ModuleRemoved(moduleType, true), linkable);
			}
			else {
				moduleInv.insertModule(moduleInv.getModule(moduleType), true);

				if (moduleInv instanceof LinkableBlockEntity linkable) {
					ItemStack stack = moduleInv.getModule(moduleType);

					linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), true), linkable);
				}
			}

			if (moduleInv instanceof BlockEntity be)
				player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}

	private IModuleInventory getModuleInventory(Level level) {
		if (pos != null) {
			if (level.getBlockEntity(pos) instanceof IModuleInventory be)
				return be;
		}
		else if (level.getEntity(entityId) instanceof IModuleInventory entity)
			return entity;

		return null;
	}
}
