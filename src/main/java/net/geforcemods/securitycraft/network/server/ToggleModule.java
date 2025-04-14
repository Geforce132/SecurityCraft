package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleModule implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "toggle_module");
	private BlockPos pos;
	private ModuleType moduleType;
	private int entityId;

	public ToggleModule() {}

	public ToggleModule(BlockPos pos, ModuleType moduleType) {
		this.pos = pos;
		this.moduleType = moduleType;
	}

	public ToggleModule(int entityId, ModuleType moduleType) {
		this.entityId = entityId;
		this.moduleType = moduleType;
	}

	public ToggleModule(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		moduleType = buf.readEnum(ModuleType.class);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeEnum(moduleType);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		IModuleInventory moduleInv = getModuleInventory(level);

		if (!player.isSpectator() && moduleInv != null && (!(moduleInv instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
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
