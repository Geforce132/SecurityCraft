package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

public class SonicSecuritySystemItem extends Item {

	public SonicSecuritySystemItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ)
	{
		if(!world.isRemote)
		{
			// If the player is sneaking, add/remove positions from the item when right-clicking a lockable block
			if(player.isSneaking())
			{
				TileEntity te = world.getTileEntity(pos);
				boolean isOwner = te instanceof IOwnable && ((IOwnable) te).getOwner().isOwner(player);
				boolean isLockable = te instanceof ILockable && ((ILockable) te).canBeLocked();

				if(isLockable && isOwner)
				{
					if(stack.getTag() == null)
						stack.setTag(new CompoundNBT());

					// Remove a block from the tag if it was already linked to.
					// If not, link to it
					if(isAdded(stack.getTag(), pos))
					{
						removeLinkedBlock(stack.getTag(), pos);
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:sonic_security_system.blockUnlinked", world.getBlockState(pos).getBlock().getTranslatedName(), pos), TextFormatting.GREEN);
						return ActionResultType.SUCCESS;
					}
					else
					{
						if(addLinkedBlock(stack.getTag(), pos, player))
						{
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:sonic_security_system.blockLinked", world.getBlockState(pos).getBlock().getTranslatedName(), pos), TextFormatting.GREEN);
						}
						else
						{
							return ActionResultType.FAIL;
						}
					}

					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));

					return ActionResultType.SUCCESS;
				}
			}
			// Place down the SSS if this item has at least one linked block saved
			else
			{
				if(stack.hasTag() && hasLinkedBlock(stack.getTag()))
				{
					pos = pos.offset(facing);

					if(!world.isAirBlock(pos))
					{
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:sonic_security_system.blocked"), TextFormatting.DARK_RED);
					}
					else
					{
						// Set up a new TileEntity and add it to the block once it's placed
						world.setBlockState(pos, SCContent.SONIC_SECURITY_SYSTEM.get().getDefaultState().with(SonicSecuritySystemBlock.FACING, player.getHorizontalFacing().getOpposite()));

						((SonicSecuritySystemTileEntity) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName().getString());
						((SonicSecuritySystemTileEntity) world.getTileEntity(pos)).transferPositionsFromItem(stack.getTag());
						world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);

						world.playSound(null, pos, SoundType.METAL.getPlaceSound(), SoundCategory.BLOCKS, SoundType.METAL.volume, SoundType.METAL.pitch);

						if(!player.isCreative())
							stack.shrink(1);
					}
				}
				else
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:sonic_security_system.notLinked"), TextFormatting.DARK_RED);
					return ActionResultType.FAIL;
				}
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {

		if(stack.getTag() == null)
			return;

		// If this item is storing block positions, show the number of them in the tooltip
		int numOfLinkedBlocks = stack.getTag().getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).size();

		if(numOfLinkedBlocks > 0)
			tooltip.add(ClientUtils.localize("tooltip.securitycraft:sonicSecuritySystem.linkedTo", numOfLinkedBlocks));
	}

	/**
	 * Adds the given position to the item's NBT tag
	 */
	private boolean addLinkedBlock(CompoundNBT tag, BlockPos pos, PlayerEntity player)
	{
		// If the position was already added, return
		if(isAdded(tag, pos))
			return false;

		ListNBT list = tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		if(list.size() >= SonicSecuritySystemTileEntity.MAX_LINKED_BLOCKS)
		{
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:sonic_security_system.linkMaxReached", SonicSecuritySystemTileEntity.MAX_LINKED_BLOCKS), TextFormatting.DARK_RED);
			return false;
		}

		CompoundNBT nbt = NBTUtil.writeBlockPos(pos);

		list.add(nbt);
		tag.put("LinkedBlocks", list);

		return true;
	}

	/**
	 * Removes the given position from the item's NBT tag
	 */
	private void removeLinkedBlock(CompoundNBT tag, BlockPos pos)
	{
		if(!tag.contains("LinkedBlocks"))
			return;

		ListNBT list = tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		// Starting from the end of the list to prevent skipping over entries
		for(int i = list.size() - 1; i >= 0; i--)
		{
			BlockPos posRead = NBTUtil.readBlockPos(list.getCompound(i));

			if(pos.equals(posRead))
				list.remove(i);
		}
	}

	/**
	 * If a position has already been added to this item's tag
	 */
	private boolean isAdded(CompoundNBT tag, BlockPos pos)
	{
		if(!tag.contains("LinkedBlocks"))
			return false;

		ListNBT list = tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < list.size(); i++)
		{
			BlockPos posRead = NBTUtil.readBlockPos(list.getCompound(i));

			if(pos.equals(posRead))
				return true;
		}

		return false;
	}

	/**
	 * If this item is linked to at least one block
	 */
	private boolean hasLinkedBlock(CompoundNBT tag)
	{
		if(!tag.contains("LinkedBlocks"))
			return false;

		return tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).size() > 0;
	}

}
