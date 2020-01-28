package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSentryRemoteAccessTool extends Item {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote)
			player.openGui(SecurityCraft.instance, GuiHandler.SRAT_MENU_ID, world, (int)player.posX, (int)player.posY, (int)player.posZ);

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote){
			List<EntitySentry> sentries = world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(pos));

			if(!sentries.isEmpty()) {
				BlockPos pos2 = sentries.get(0).getPosition();

				if(!isSentryAdded(stack, world, pos2)){
					int availSlot = getNextAvailableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), ClientUtils.localize("messages.securitycraft:srat.noSlots"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(world.getTileEntity(pos2) instanceof IOwnable && !((IOwnable) world.getTileEntity(pos2)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), ClientUtils.localize("messages.securitycraft:srat.cantBind"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					stack.getTagCompound().setIntArray(("sentry" + availSlot), BlockUtils.fromPos(pos2));
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), ClientUtils.localize("messages.securitycraft:srat.bound").replace("#", Utils.getFormattedCoordinates(pos2)), TextFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos2, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), ClientUtils.localize("messages.securitycraft:srat.unbound").replace("#", Utils.getFormattedCoordinates(pos2)), TextFormatting.RED);
				}
			}
			else
				player.openGui(SecurityCraft.instance, GuiHandler.SRAT_MENU_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		if(stack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTagCompound().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add("---");
					continue;
				}
				else
				{
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					List<EntitySentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(pos));
					String nameToShow;

					if(!sentries.isEmpty() && sentries.get(0).hasCustomName())
						nameToShow = sentries.get(0).getCustomNameTag();
					else
						nameToShow = ClientUtils.localize("tooltip.securitycraft:sentry") + " " + i;

					list.add(nameToShow + ": " + Utils.getFormattedCoordinates(pos));
				}
			}
			else
				list.add("---");
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, EntityPlayer player) {
		if(stack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTagCompound().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTagCompound().setIntArray("sentry" + i, new int[]{0, 0, 0});
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isSentryAdded(ItemStack stack, World world, BlockPos pos) {
		if(stack.getTagCompound() == null)
			return false;

		for(int i = 1; i <= 12; i++)
			if(stack.getTagCompound().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;

		return false;
	}

	private int getNextAvailableSlot(ItemStack stack){
		for(int i = 1; i <= 12; i++)
			if(stack.getTagCompound() == null)
				return 1;
			else if(stack.getTagCompound().getIntArray("sentry" + i).length == 0 || (stack.getTagCompound().getIntArray("sentry" + i)[0] == 0 && stack.getTagCompound().getIntArray("sentry" + i)[1] == 0 && stack.getTagCompound().getIntArray("sentry" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}
