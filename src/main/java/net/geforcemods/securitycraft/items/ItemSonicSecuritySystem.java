package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSonicSecuritySystem extends ItemBlock {
	public ItemSonicSecuritySystem()
	{
		super(SCContent.sonicSecuritySystem);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		// If the player is sneaking, add/remove positions from the item when right-clicking a lockable block
		if(player.isSneaking())
		{
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof ILockable) {
				if(te instanceof IOwnable && !((IOwnable) te).getOwner().isOwner(player)) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.sonicSecuritySystem), Utils.localize("messages.securitycraft:notOwned", ((IOwnable) te).getOwner().getName(), pos), TextFormatting.GREEN);
					return EnumActionResult.SUCCESS;
				}
				else {
					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					// Remove a block from the tag if it was already linked to.
					// If not, link to it
					if(isAdded(stack.getTagCompound(), pos))
					{
						removeLinkedBlock(stack.getTagCompound(), pos);
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.sonicSecuritySystem), Utils.localize("messages.securitycraft:sonic_security_system.blockUnlinked", world.getBlockState(pos).getBlock(), pos), TextFormatting.GREEN);
						return EnumActionResult.SUCCESS;
					}
					else if(addLinkedBlock(stack.getTagCompound(), pos, player))
					{
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.sonicSecuritySystem), Utils.localize("messages.securitycraft:sonic_security_system.blockLinked", world.getBlockState(pos).getBlock(), pos), TextFormatting.GREEN);

						if(!world.isRemote)
							SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP)player);

						return EnumActionResult.SUCCESS;
					}
				}
			}
		}

		//don't place down the SSS if it has at least one linked block
		//placing is handled by minecraft otherwise
		if(!stack.hasTagCompound() || !hasLinkedBlock(stack.getTagCompound()))
		{
			if(!world.isRemote)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.sonicSecuritySystem), Utils.localize("messages.securitycraft:sonic_security_system.notLinked"), TextFormatting.DARK_RED);

			return EnumActionResult.FAIL;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		EnumActionResult returnValue = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

		if(returnValue == EnumActionResult.SUCCESS)
			((TileEntitySonicSecuritySystem) world.getTileEntity(pos.offset(facing))).transferPositionsFromItem(player.getHeldItem(hand).getTagCompound());

		return returnValue;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if(!stack.hasTagCompound())
			return;

		// If this item is storing block positions, show the number of them in the tooltip
		int numOfLinkedBlocks = stack.getTagCompound().getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).tagCount();

		if(numOfLinkedBlocks > 0)
			tooltip.add(TextFormatting.GRAY + Utils.localize("tooltip.securitycraft:sonicSecuritySystem.linkedTo", numOfLinkedBlocks).getFormattedText());
	}

	/**
	 * Adds a position to a tag
	 * @param tag The tag to add the position to
	 * @param pos The position to add to the tag
	 * @param player The player who tries to link a block
	 * @return true if the position was added, false otherwise
	 */
	public static boolean addLinkedBlock(NBTTagCompound tag, BlockPos pos, EntityPlayer player)
	{
		// If the position was already added, return
		if(isAdded(tag, pos))
			return false;

		NBTTagList list = tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		if(list.tagCount() >= TileEntitySonicSecuritySystem.MAX_LINKED_BLOCKS)
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.sonicSecuritySystem), Utils.localize("messages.securitycraft:sonic_security_system.linkMaxReached", TileEntitySonicSecuritySystem.MAX_LINKED_BLOCKS), TextFormatting.DARK_RED);
			return false;
		}

		NBTTagCompound nbt = NBTUtil.createPosTag(pos);

		list.appendTag(nbt);
		tag.setTag("LinkedBlocks", list);
		return true;
	}

	/**
	 * Removes a position from a tag
	 * @param tag The tag to remove the position from
	 * @param pos The position to remove from the tag
	 */
	public static void removeLinkedBlock(NBTTagCompound tag, BlockPos pos)
	{
		if(!tag.hasKey("LinkedBlocks"))
			return;

		NBTTagList list = tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		// Starting from the end of the list to prevent skipping over entries
		for(int i = list.tagCount() - 1; i >= 0; i--)
		{
			BlockPos posRead = NBTUtil.getPosFromTag(list.getCompoundTagAt(i));

			if(pos.equals(posRead))
				list.removeTag(i);
		}
	}

	/**
	 * Checks whether a position is added to a tag
	 * @param tag The tag to check
	 * @param pos The position to check
	 * @return true if the position is added, false otherwise
	 */
	public static boolean isAdded(NBTTagCompound tag, BlockPos pos)
	{
		if(!tag.hasKey("LinkedBlocks"))
			return false;

		NBTTagList list = tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < list.tagCount(); i++)
		{
			BlockPos posRead = NBTUtil.getPosFromTag(list.getCompoundTagAt(i));

			if(pos.equals(posRead))
				return true;
		}

		return false;
	}

	/**
	 * @return true if the tag contains at least one position, false otherwise
	 */
	public static boolean hasLinkedBlock(NBTTagCompound tag)
	{
		if(!tag.hasKey("LinkedBlocks"))
			return false;

		return tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).tagCount() > 0;
	}

}
