package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSentryRemoteAccessTool extends Item {
	private static final Style GRAY_STYLE = new Style().setColor(TextFormatting.GRAY);

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote)
			player.openGui(SecurityCraft.instance, GuiHandler.SRAT_MENU_ID, world, player.getServer().getPlayerList().getEntityViewDistance(), (int)player.posY, (int)player.posZ);

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);

		List<EntitySentry> sentries = world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(pos));

		if(!sentries.isEmpty()) {
			EntitySentry sentry = sentries.get(0);
			BlockPos pos2 = sentry.getPosition();

			if(!isSentryAdded(stack, pos2)){
				int availSlot = getNextAvailableSlot(stack);

				if(availSlot == 0){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.noSlots"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if(!sentry.getOwner().isOwner(player)){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.cantBind"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if(stack.getTagCompound() == null)
					stack.setTagCompound(new NBTTagCompound());

				stack.getTagCompound().setIntArray(("sentry" + availSlot), BlockUtils.posToIntArray(pos2));

				if (!world.isRemote)
					SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.bound", pos2), TextFormatting.GREEN);
			}else{
				removeTagFromItemAndUpdate(stack, pos2, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.unbound", pos2), TextFormatting.RED);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
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
						nameToShow = Utils.localize("tooltip.securitycraft:sentry").getFormattedText() + TextFormatting.GRAY + " " + i;

					list.add(nameToShow + ": " + Utils.getFormattedCoordinates(pos).setStyle(GRAY_STYLE).getFormattedText());
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

					if (!player.world.isRemote)
						SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isSentryAdded(ItemStack stack, BlockPos pos) {
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
		{
			if(stack.getTagCompound() == null)
				return 1;

			int[] pos = stack.getTagCompound().getIntArray("sentry" + i);

			if(pos.length == 0 || (pos[0] == 0 && pos[1] == 0 && pos[2] == 0))
				return i;
		}

		return 0;
	}
}
