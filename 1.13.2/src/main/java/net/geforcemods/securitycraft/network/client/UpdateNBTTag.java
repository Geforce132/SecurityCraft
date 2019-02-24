package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTag{

	private NBTTagCompound stackTag;
	private String itemName;

	public UpdateNBTTag(){

	}

	public UpdateNBTTag(ItemStack stack){
		if(!stack.isEmpty() && stack.hasTag()){
			stackTag = stack.getTag();
			itemName = stack.getTranslationKey();
		}
	}

	public void fromBytes(PacketBuffer buf) {
		stackTag = buf.readCompoundTag();
		itemName = buf.readString(Integer.MAX_VALUE);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeCompoundTag(stackTag);
		buf.writeString(itemName);
	}

	public static void encode(UpdateNBTTag message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static UpdateNBTTag decode(PacketBuffer packet)
	{
		UpdateNBTTag message = new UpdateNBTTag();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(UpdateNBTTag message, Supplier<NetworkEvent.Context> ctx) {
		if(!Minecraft.getInstance().player.inventory.getCurrentItem().isEmpty() && Minecraft.getInstance().player.inventory.getCurrentItem().getItem().getTranslationKey().equals(message.itemName)){
			Minecraft.getInstance().player.inventory.getCurrentItem().setTag(message.stackTag);;
		}

		ctx.get().setPacketHandled(true);
	}

}
