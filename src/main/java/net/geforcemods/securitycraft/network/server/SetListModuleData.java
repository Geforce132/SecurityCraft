package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetListModuleData implements IMessage {
	private NBTTagCompound tag;

	public SetListModuleData() {}

	public SetListModuleData(NBTTagCompound tag) {
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<SetListModuleData, IMessage> {
		@Override
		public IMessage onMessage(SetListModuleData message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.allowlistModule);

				if (stack.isEmpty())
					stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.denylistModule);

				if (!player.isSpectator() && !stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound clientTag = message.tag;
					NBTTagCompound serverTag = stack.getTagCompound();

					for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
						String key = "Player" + i;

						if (clientTag.hasKey(key))
							serverTag.setString(key, clientTag.getString(key));
						else //prevent two same players being on the list
							serverTag.removeTag(key);
					}

					if (clientTag.hasKey("ListedTeams")) {
						NBTTagList listedTeams = new NBTTagList();

						for (NBTBase teamTag : clientTag.getTagList("ListedTeams", Constants.NBT.TAG_STRING)) {
							//make sure the team the client sent is actually a team that exists
							if (player.world.getScoreboard().getTeamNames().contains(teamTag.toString().replace("\"", "")))
								listedTeams.appendTag(teamTag);
						}

						serverTag.setTag("ListedTeams", listedTeams);
					}

					serverTag.setBoolean("affectEveryone", clientTag.getBoolean("affectEveryone"));
				}
			});

			return null;
		}
	}
}
