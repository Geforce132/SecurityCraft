package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBlockReinforcedWoodSlabs extends ItemBlock {

	private BlockSlab singleSlab = (BlockSlab) SCContent.reinforcedWoodSlabs;
	private Block doubleSlab = SCContent.reinforcedDoubleWoodSlabs;

	public ItemBlockReinforcedWoodSlabs(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta){
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack){
		if(stack.getDamage() == 0)
			return this.getTranslationKey() + "_oak";
		else if(stack.getDamage() == 1)
			return this.getTranslationKey() + "_spruce";
		else if(stack.getDamage() == 2)
			return this.getTranslationKey() + "_birch";
		else if(stack.getDamage() == 3)
			return this.getTranslationKey() + "_jungle";
		else if(stack.getDamage() == 4)
			return this.getTranslationKey() + "_acacia";
		else if(stack.getDamage() == 5)
			return this.getTranslationKey() + "_darkoak";
		else
			return this.getTranslationKey();
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);

		if(stack.getCount() == 0)
			return EnumActionResult.FAIL;
		else if (!player.canPlayerEdit(pos.offset(side), side, stack))
			return EnumActionResult.FAIL;
		else{
			Object type = singleSlab.getTypeForItem(stack);
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() == singleSlab){
				IProperty<?> variantProperty = singleSlab.getVariantProperty();
				Comparable<?> value = state.get(variantProperty);
				BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
				Owner owner = null;

				if(world.getTileEntity(pos) instanceof IOwnable){
					owner = ((IOwnable) world.getTileEntity(pos)).getOwner();

					if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
						if(!world.isRemote)
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:reinforcedSlab"), ClientUtils.localize("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);

						return EnumActionResult.FAIL;
					}
				}

				if((side == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP) && value == type){
					IBlockState newState = makeState(variantProperty, value);

					if(world.checkNoEntityCollision(newState.getCollisionBoundingBox(world, pos)) && world.setBlockState(pos, newState, 3)){
						world.playSound(player, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
						stack.shrink(1);

						if(owner != null)
							((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
					}

					return EnumActionResult.SUCCESS;
				}
			}

			return tryPlace(stack, world, player, pos.offset(side), type) ? EnumActionResult.SUCCESS : super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
		BlockPos originalPos = pos;
		IProperty<?> variantProperty = singleSlab.getVariantProperty();
		Object value = singleSlab.getTypeForItem(stack);
		IBlockState iblockstate = world.getBlockState(pos);

		if(iblockstate.getBlock() == singleSlab){
			boolean isTop = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

			if((side == EnumFacing.UP && !isTop || side == EnumFacing.DOWN && isTop) && value == iblockstate.get(variantProperty))
				return true;
		}

		pos = pos.offset(side);
		IBlockState updatedState = world.getBlockState(pos);
		return updatedState.getBlock() == singleSlab && value == updatedState.get(variantProperty) ? true : super.canPlaceBlockOnSide(world, originalPos, side, player, stack);
	}

	private boolean tryPlace(ItemStack stack, World world, EntityPlayer player, BlockPos pos, Object variantInStack){
		IBlockState state = world.getBlockState(pos);
		Owner owner = null;

		if(world.getTileEntity(pos) instanceof IOwnable)
			owner = ((IOwnable) world.getTileEntity(pos)).getOwner();

		if(state.getBlock() == singleSlab){
			Comparable<?> value = state.getValue(singleSlab.getVariantProperty());

			if(value == variantInStack){
				IBlockState newState = this.makeState(singleSlab.getVariantProperty(), value);

				if (world.checkNoEntityCollision(newState.getCollisionBoundingBox(world, pos)) && world.setBlockState(pos, newState, 3)){
					world.playSound(player, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
					stack.shrink(1);

					if(owner != null)
						((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
				}

				return true;
			}
		}

		return false;
	}

	protected <T extends Comparable<T>> IBlockState makeState(IProperty<T> property, Comparable<?> comparable) {
		return doubleSlab.getDefaultState().with(property, (T)comparable);
	}

}
