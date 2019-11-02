package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockReinforcedSlabs extends ItemBlock {

	private BlockSlab singleSlab = (BlockSlab) SCContent.reinforcedStoneSlabs;
	private Block doubleSlab = SCContent.reinforcedDoubleStoneSlabs;

	public ItemBlockReinforcedSlabs(Block block) {
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
			return this.getUnlocalizedName() + "_stone";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_cobble";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_sandstone";
		else if(stack.getItemDamage() == 3)
			return this.getUnlocalizedName() + "_stonebrick";
		else if(stack.getItemDamage() == 4)
			return this.getUnlocalizedName() + "_brick";
		else if(stack.getItemDamage() == 5)
			return this.getUnlocalizedName() + "_netherbrick";
		else if(stack.getItemDamage() == 6)
			return this.getUnlocalizedName() + "_quartz";
		else
			return this.getUnlocalizedName();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(stack.stackSize == 0)
			return EnumActionResult.FAIL;
		else if (!player.canPlayerEdit(pos.offset(side), side, stack))
			return EnumActionResult.FAIL;
		else{
			Object type = singleSlab.getTypeForItem(stack);
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockReinforcedSlabs){
				IProperty<?> valueProperty = singleSlab.getVariantProperty();
				Comparable<?> value = state.getValue(valueProperty);
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
					IBlockState doubleState = getDoubleSlabBlock(value);
					doubleState.getBlock();

					if(world.checkNoEntityCollision(doubleState.getCollisionBoundingBox(world, pos)) && world.setBlockState(pos, doubleState, 3)){
						world.playSound(player, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
						--stack.stackSize;

						if(owner != null)
							((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
					}

					return EnumActionResult.SUCCESS;
				}
			}

			return tryPlace(stack, world, player, pos.offset(side), type) ? EnumActionResult.SUCCESS : super.onItemUse(stack, player, world, pos, hand, side, hitX, hitY, hitZ);
		}
	}

	private IBlockState getDoubleSlabBlock(Comparable<?> comparable) {
		if(comparable == BlockReinforcedSlabs.EnumType.STONE)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.COBBLESTONE)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.SANDSTONE)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.STONEBRICK)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.BRICK)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.NETHERBRICK)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs.EnumType.QUARTZ)
			return makeState_Stone(BlockReinforcedSlabs.VARIANT, comparable);
		else
			return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
		BlockPos originalPos = pos;
		IProperty<?> valueProperty = singleSlab.getVariantProperty();
		Object value = singleSlab.getTypeForItem(stack);
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() == singleSlab){
			boolean isTop = state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

			if((side == EnumFacing.UP && !isTop || side == EnumFacing.DOWN && isTop) && value == state.getValue(valueProperty))
				return true;
		}

		pos = pos.offset(side);
		IBlockState updatedState = world.getBlockState(pos);
		return updatedState.getBlock() == singleSlab && value == updatedState.getValue(valueProperty) ? true : super.canPlaceBlockOnSide(world, originalPos, side, player, stack);
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

				if (world.checkNoEntityCollision(newState.getCollisionBoundingBox( world, pos)) && world.setBlockState(pos, newState, 3)){
					world.playSound(player, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
					--stack.stackSize;

					if(owner != null)
						((IOwnable) world.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
				}

				return true;
			}
		}

		return false;
	}

	protected  <T extends Comparable<T>> IBlockState makeState(IProperty<T> property, Comparable<?> comparable) {
		return doubleSlab.getDefaultState().withProperty(property, (T)comparable);
	}

	protected <T extends Comparable<T>> IBlockState makeState_Stone(IProperty<T> property, Comparable<?> comparable) {
		return SCContent.reinforcedDoubleStoneSlabs.getDefaultState().withProperty(property, (T)comparable);
	}
}
