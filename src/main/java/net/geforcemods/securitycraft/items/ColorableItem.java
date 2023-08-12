package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColorableItem extends Item {
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		ItemStack stack = player.getHeldItem(hand);

		if (state.getBlock() instanceof BlockCauldron) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof ReinforcedCauldronBlockEntity && !((ReinforcedCauldronBlockEntity) te).isAllowedToInteract(player))
				return EnumActionResult.FAIL;

			int level = state.getValue(BlockCauldron.LEVEL);

			if (level > 0 && hasColor(stack)) {
				removeColor(stack);
				((BlockCauldron) state.getBlock()).setWaterLevel(world, pos, state, level - 1);

				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.FAIL;
		}

		extraHandling(stack, world, player);
		return EnumActionResult.SUCCESS;
	}

	public boolean hasColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && tag.hasKey("display", 10) ? tag.getCompoundTag("display").hasKey("color", 3) : false;
	}

	public int getColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null) {
			NBTTagCompound displayTag = tag.getCompoundTag("display");

			if (displayTag != null && displayTag.hasKey("color", 3))
				return displayTag.getInteger("color");
		}

		return getDefaultColor();
	}

	public int getDefaultColor() {
		return -1;
	}

	public void removeColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null) {
			NBTTagCompound displayTag = tag.getCompoundTag("display");

			if (displayTag.hasKey("color"))
				displayTag.removeTag("color");
		}
	}

	public void setColor(ItemStack stack, int color) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}

		NBTTagCompound displayTag = tag.getCompoundTag("display");

		if (!tag.hasKey("display", 10))
			tag.setTag("display", displayTag);

		displayTag.setInteger("color", color);
	}

	public void extraHandling(ItemStack stack, World level, EntityPlayer player) {}
}
