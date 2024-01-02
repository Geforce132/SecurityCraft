package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class RefreshDisguisableModel implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "refresh_disguisable_model");
	private BlockPos pos;
	private boolean insert;
	private ItemStack stack;
	private boolean toggled;

	public RefreshDisguisableModel() {}

	public RefreshDisguisableModel(BlockPos pos, boolean insert, ItemStack stack, boolean toggled) {
		this.pos = pos;
		this.insert = insert;
		this.stack = stack;
		this.toggled = toggled;
	}

	public RefreshDisguisableModel(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		insert = buf.readBoolean();
		stack = buf.readItem();
		toggled = buf.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(insert);
		buf.writeItem(stack);
		buf.writeBoolean(toggled);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		IModuleInventory be = (IModuleInventory) Minecraft.getInstance().level.getBlockEntity(pos);

		if (be != null) {
			if (insert)
				be.insertModule(stack, toggled);
			else
				be.removeModule(ModuleType.DISGUISE, toggled);

			ClientHandler.refreshModelData(be.getBlockEntity());
		}
	}
}
