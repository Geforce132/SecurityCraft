package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs2;
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

public class ItemBlockReinforcedSlabs2 extends ItemBlock {

	private BlockSlab singleSlab = (BlockSlab) SCContent.reinforcedStoneSlabs2;
	private Block doubleSlab = SCContent.reinforcedDoubleStoneSlabs2;

	public ItemBlockReinforcedSlabs2(Block block) {
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
			return this.getUnlocalizedName() + "_red_sandstone";if(stack.getItemDamage() == 1)
				return this.getUnlocalizedName() + "_purpur";
			else
				return this.getUnlocalizedName();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(stack.stackSize == 0)
			return EnumActionResult.FAIL;
		else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
			return EnumActionResult.FAIL;
		else{
			Object object = singleSlab.getTypeForItem(stack);
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if(iblockstate.getBlock() instanceof BlockReinforcedSlabs2){
				IProperty<?> iproperty = singleSlab.getVariantProperty();
				Comparable<?> comparable = iblockstate.getValue(iproperty);
				BlockSlab.EnumBlockHalf enumblockhalf = iblockstate.getValue(BlockSlab.HALF);

				Owner owner = null;

				if(worldIn.getTileEntity(pos) instanceof IOwnable){
					owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();

					if(!((IOwnable) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
						if(!worldIn.isRemote)
							PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("messages.reinforcedSlab"), ClientUtils.localize("messages.reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);

						return EnumActionResult.FAIL;
					}
				}

				if((side == EnumFacing.UP && enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable == object){
					IBlockState iblockstate1 = getDoubleSlabBlock(comparable);
					iblockstate1.getBlock();

					if(worldIn.checkNoEntityCollision(iblockstate1.getCollisionBoundingBox(worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
						worldIn.playSound(playerIn, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
						--stack.stackSize;

						if(owner != null)
							((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
					}

					return EnumActionResult.SUCCESS;
				}
			}

			return tryPlace(stack, worldIn, playerIn, pos.offset(side), object) ? EnumActionResult.SUCCESS : super.onItemUse(stack, playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
		}
	}

	private IBlockState getDoubleSlabBlock(Comparable<?> comparable) {
		if(comparable == BlockReinforcedSlabs2.EnumType.RED_SANDSTONE)
			return makeState_Stone(BlockReinforcedSlabs2.VARIANT, comparable);
		else if(comparable == BlockReinforcedSlabs2.EnumType.PURPUR)
			return makeState_Stone(BlockReinforcedSlabs2.VARIANT, comparable);
		else
			return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
		BlockPos blockpos1 = pos;
		IProperty<?> iproperty = singleSlab.getVariantProperty();
		Object object = singleSlab.getTypeForItem(stack);
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if(iblockstate.getBlock() == singleSlab){
			boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

			if((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && object == iblockstate.getValue(iproperty))
				return true;
		}

		pos = pos.offset(side);
		IBlockState iblockstate1 = worldIn.getBlockState(pos);
		return iblockstate1.getBlock() == singleSlab && object == iblockstate1.getValue(iproperty) ? true : super.canPlaceBlockOnSide(worldIn, blockpos1, side, player, stack);
	}

	private boolean tryPlace(ItemStack stack, World worldIn, EntityPlayer player, BlockPos pos, Object variantInStack){
		IBlockState iblockstate = worldIn.getBlockState(pos);

		Owner owner = null;

		if(worldIn.getTileEntity(pos) instanceof IOwnable)
			owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();

		if(iblockstate.getBlock() == singleSlab){
			Comparable<?> comparable = iblockstate.getValue(singleSlab.getVariantProperty());

			if(comparable == variantInStack){
				IBlockState iblockstate1 = this.makeState(singleSlab.getVariantProperty(), comparable);

				if (worldIn.checkNoEntityCollision(iblockstate1.getCollisionBoundingBox( worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
					worldIn.playSound(player, pos, doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, doubleSlab.getSoundType().getPitch() * 0.8F);
					--stack.stackSize;

					if(owner != null)
						((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
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
		return SCContent.reinforcedDoubleStoneSlabs2.getDefaultState().withProperty(property, (T)comparable);
	}
}
