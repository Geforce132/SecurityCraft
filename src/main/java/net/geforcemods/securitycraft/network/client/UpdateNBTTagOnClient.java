package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateNBTTagOnClient implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_nbt_tag_on_client");
	private ItemStack stack;

	public UpdateNBTTagOnClient() {}

	public UpdateNBTTagOnClient(ItemStack stack) {
		this.stack = stack;
	}

	public UpdateNBTTagOnClient(FriendlyByteBuf buf) {
		stack = buf.readItem();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeItem(stack);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		ItemStack stackToUpdate = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), stack.getItem());

		if (!stackToUpdate.isEmpty())
			stackToUpdate.setTag(stack.getTag());
	}
}
