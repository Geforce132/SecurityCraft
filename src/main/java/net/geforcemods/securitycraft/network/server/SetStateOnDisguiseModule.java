package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.registries.GameData;

public class SetStateOnDisguiseModule implements IMessage {
	private IBlockState state;

	public SetStateOnDisguiseModule() {}

	public SetStateOnDisguiseModule(IBlockState state) {
		this.state = state;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(GameData.getBlockStateIDMap().get(state));
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		state = GameData.getBlockStateIDMap().getByValue(buf.readInt());
	}

	public static class Handler implements IMessageHandler<SetStateOnDisguiseModule, IMessage> {
		@Override
		public IMessage onMessage(SetStateOnDisguiseModule message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.disguiseModule);

				if (!player.isSpectator() && !stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = stack.getTagCompound();

					if (message.state.getMaterial() == Material.AIR) {
						tag.removeTag("SavedState");
						tag.removeTag("ItemInventory");
					}
					else
						tag.setTag("SavedState", NBTUtil.writeBlockState(new NBTTagCompound(), message.state));
				}
			});
			return null;
		}
	}
}
