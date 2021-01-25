package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTagOnServer {

	private CompoundNBT stackTag;
	private String itemName;

	public UpdateNBTTagOnServer(){

	}

	public UpdateNBTTagOnServer(ItemStack stack){
		if(!stack.isEmpty() && stack.getTag() != null){
			stackTag = stack.getTag();
			itemName = stack.getTranslationKey();
		}
	}

	public static void encode(UpdateNBTTagOnServer message, PacketBuffer buf)
	{
		buf.writeCompoundTag(message.stackTag);
		buf.writeString(message.itemName);
	}

	public static UpdateNBTTagOnServer decode(PacketBuffer buf)
	{
		UpdateNBTTagOnServer message = new UpdateNBTTagOnServer();

		message.stackTag = buf.readCompoundTag();
		message.itemName = buf.readString(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(UpdateNBTTagOnServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem().getTranslationKey().equals(message.itemName))
				player.inventory.getCurrentItem().setTag(message.stackTag);
		});

		ctx.get().setPacketHandled(true);
	}
}
