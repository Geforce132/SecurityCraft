package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBriefcase extends Item {

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		ItemStack stack = player.getHeldItem(hand);

		if(state.getBlock() == Blocks.CAULDRON)
		{
			int level = state.getValue(BlockCauldron.LEVEL);

			if(level > 0 && hasColor(stack))
			{
				removeColor(stack);
				((BlockCauldron)state.getBlock()).setWaterLevel(world, pos, state, level - 1);
			}

			return EnumActionResult.SUCCESS;
		}

		if(hand == EnumHand.MAIN_HAND)
		{
			if(world.isRemote) {
				if(!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
					ClientUtils.syncItemNBT(stack);
				}

				if(!stack.getTagCompound().hasKey("passcode"))
					player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				else
					player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTagCompound().hasKey("passcode"))
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			else
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack briefcase, World world, List<String> tooltip, ITooltipFlag flag)
	{
		if (briefcase.hasTagCompound() && briefcase.getTagCompound().hasKey("owner"))
			tooltip.add(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:briefcase.owner", briefcase.getTagCompound().getString("owner")));
	}

	public boolean hasColor(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && tag.hasKey("display", 10) ? tag.getCompoundTag("display").hasKey("color", 3) : false;
	}

	public int getColor(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();

		if(tag != null)
		{
			NBTTagCompound displayTag = tag.getCompoundTag("display");

			if(displayTag != null && displayTag.hasKey("color", 3))
				return displayTag.getInteger("color");
		}

		return 0x333333;
	}

	public void removeColor(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();

		if(tag != null)
		{
			NBTTagCompound displayTag = tag.getCompoundTag("display");

			if(displayTag.hasKey("color"))
				displayTag.removeTag("color");
		}
	}

	public void setColor(ItemStack stack, int color)
	{
		NBTTagCompound tag = stack.getTagCompound();

		if(tag == null)
		{
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}

		NBTTagCompound displayTag = tag.getCompoundTag("display");

		if(!tag.hasKey("display", 10))
			tag.setTag("display", displayTag);

		displayTag.setInteger("color", color);
	}

	public static boolean isOwnedBy(ItemStack briefcase, EntityPlayer player) {
		return !briefcase.hasTagCompound() || !briefcase.getTagCompound().hasKey("owner") || briefcase.getTagCompound().getString("ownerUUID").equals(player.getUniqueID().toString()) || (briefcase.getTagCompound().getString("ownerUUID").equals("ownerUUID") && briefcase.getTagCompound().getString("owner").equals(player.getName()));
	}
}
