package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTagOnServer {

	private NBTTagCompound stackTag;
	private String itemName;

	public UpdateNBTTagOnServer(){

	}

	public UpdateNBTTagOnServer(ItemStack stack){
		if(!stack.isEmpty() && stack.hasTag()){
			stackTag = stack.getTag();
			itemName = stack.getTranslationKey();
		}
	}

	public void fromBytes(PacketBuffer buf) {
		stackTag = buf.readCompoundTag();
		itemName = buf.readString(Integer.MAX_VALUE / 4);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeCompoundTag(stackTag);
		buf.writeString(itemName);
	}

	public static void encode(UpdateNBTTagOnServer message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static UpdateNBTTagOnServer decode(PacketBuffer packet)
	{
		UpdateNBTTagOnServer message = new UpdateNBTTagOnServer();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(UpdateNBTTagOnServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			EntityPlayer player = ctx.get().getSender();
			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem().getTranslationKey().equals(message.itemName))
				player.inventory.getCurrentItem().setTag(message.stackTag);
		});

		ctx.get().setPacketHandled(true);
	}
}
