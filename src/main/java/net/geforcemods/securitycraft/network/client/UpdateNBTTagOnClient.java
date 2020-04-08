package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTagOnClient{

	private CompoundNBT stackTag;
	private String itemName;

	public UpdateNBTTagOnClient(){

	}

	public UpdateNBTTagOnClient(ItemStack stack){
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

	public static void encode(UpdateNBTTagOnClient message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static UpdateNBTTagOnClient decode(PacketBuffer packet)
	{
		UpdateNBTTagOnClient message = new UpdateNBTTagOnClient();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(UpdateNBTTagOnClient message, Supplier<NetworkEvent.Context> ctx) {
		if(!Minecraft.getInstance().player.inventory.getCurrentItem().isEmpty() && Minecraft.getInstance().player.inventory.getCurrentItem().getItem().getTranslationKey().equals(message.itemName)){
			Minecraft.getInstance().player.inventory.getCurrentItem().setTag(message.stackTag);
		}

		ctx.get().setPacketHandled(true);
	}

}
