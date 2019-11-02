package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockReinforcedWoodSlabs extends ItemBlockTinted {

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
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0)
			return this.getUnlocalizedName() + "_oak";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_spruce";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_birch";
		else if(stack.getItemDamage() == 3)
			return this.getUnlocalizedName() + "_jungle";
		else if(stack.getItemDamage() == 4)
			return this.getUnlocalizedName() + "_acacia";
		else if(stack.getItemDamage() == 5)
			return this.getUnlocalizedName() + "_darkoak";
		else
			return this.getUnlocalizedName();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if(stack.stackSize == 0)
			return false;
		else if (!player.canPlayerEdit(pos.offset(side), side, stack))
			return false;
		else{
			Object variant = singleSlab.getVariant(stack);
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() == singleSlab){
				IProperty variantProperty = singleSlab.getVariantProperty();
				Comparable<?> variantValue = state.getValue(variantProperty);
				BlockSlab.EnumBlockHalf half = (BlockSlab.EnumBlockHalf)state.getValue(BlockSlab.HALF);
				Owner owner = null;

				if(world.getTileEntity(pos) instanceof IOwnable){
					owner = ((IOwnable) world.getTileEntity(pos)).getOwner();

					if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
						if(!world.isRemote)
							PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:reinforcedSlab"), StatCollector.translateToLocal("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), EnumChatFormatting.RED);

						return false;
					}
				}

				if((side == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP) && variantValue == variant){
					IBlockState doubleState = doubleSlab.getDefaultState().withProperty(variantProperty, variantValue);

					if(world.checkNoEntityCollision(doubleSlab.getCollisionBoundingBox(world, pos, doubleState)) && world.setBlockState(pos, doubleState, 3)){
						world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, doubleSlab.stepSound.getPlaceSound(), (doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, doubleSlab.stepSound.getFrequency() * 0.8F);
						--stack.stackSize;

						if(owner != null)
							((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
					}

					return true;
				}
			}

			return tryPlace(stack, world, pos.offset(side), variant) ? true : super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
		BlockPos originalPos = pos;
		IProperty variantProperty = singleSlab.getVariantProperty();
		Object variant = singleSlab.getVariant(stack);
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() == singleSlab){
			boolean topSlab = state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

			if((side == EnumFacing.UP && !topSlab || side == EnumFacing.DOWN && topSlab) && variant == state.getValue(variantProperty))
				return true;
		}

		pos = pos.offset(side);
		IBlockState updatedState = world.getBlockState(pos);
		return updatedState.getBlock() == singleSlab && variant == updatedState.getValue(variantProperty) ? true : super.canPlaceBlockOnSide(world, originalPos, side, player, stack);
	}

	private boolean tryPlace(ItemStack stack, World world, BlockPos pos, Object variantInStack){
		IBlockState state = world.getBlockState(pos);
		Owner owner = null;

		if(world.getTileEntity(pos) instanceof IOwnable)
			owner = ((IOwnable) world.getTileEntity(pos)).getOwner();

		if(state.getBlock() == singleSlab){
			Comparable<?> value = state.getValue(singleSlab.getVariantProperty());

			if(value == variantInStack){
				IBlockState stateWithProperty = doubleSlab.getDefaultState().withProperty(singleSlab.getVariantProperty(), value);

				if (world.checkNoEntityCollision(doubleSlab.getCollisionBoundingBox(world, pos, stateWithProperty)) && world.setBlockState(pos, stateWithProperty, 3)){
					world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, doubleSlab.stepSound.getPlaceSound(), (doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, doubleSlab.stepSound.getFrequency() * 0.8F);
					--stack.stackSize;

					if(owner != null)
						((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
				}

				return true;
			}
		}

		return false;
	}

}
