package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedCauldron;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBriefcase extends Item {

	private static final Style GRAY_STYLE = new Style().setColor(TextFormatting.GRAY);

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		ItemStack stack = player.getHeldItem(hand);

		if(state.getBlock() instanceof BlockCauldron)
		{
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityReinforcedCauldron && !((TileEntityReinforcedCauldron)te).isAllowedToInteract(player))
			{
				return EnumActionResult.FAIL;
			}

			int level = state.getValue(BlockCauldron.LEVEL);

			if(level > 0 && hasColor(stack))
			{
				removeColor(stack);
				((BlockCauldron)state.getBlock()).setWaterLevel(world, pos, state, level - 1);

				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.FAIL;
		}

		handle(stack, world, player, hand);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		handle(stack, world, player, hand);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private void handle(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack briefcase, World world, List<String> tooltip, ITooltipFlag flag)
	{
		String ownerName = getOwnerName(briefcase);

		if(!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft:briefcase.owner", ownerName).setStyle(GRAY_STYLE).getFormattedText());
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
		if(!briefcase.hasTagCompound())
			return true;

		String ownerName = getOwnerName(briefcase);
		String ownerUUID = getOwnerUUID(briefcase);

		return ownerName.isEmpty() || ownerUUID.equals(player.getUniqueID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName()));
	}

	public static String getOwnerName(ItemStack briefcase)
	{
		return briefcase.hasTagCompound() ? briefcase.getTagCompound().getString("owner") : "";
	}

	public static String getOwnerUUID(ItemStack briefcase)
	{
		return briefcase.hasTagCompound() ? briefcase.getTagCompound().getString("ownerUUID") : "";
	}
}
