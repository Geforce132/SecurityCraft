package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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

public class ItemBlockReinforcedWoodSlabs extends ItemBlock {
	
	private BlockSlab singleSlab = (BlockSlab) mod_SecurityCraft.reinforcedWoodSlabs;
	private Block doubleSlab = mod_SecurityCraft.reinforcedDoubleWoodSlabs;

	public ItemBlockReinforcedWoodSlabs(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int meta){
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0){
			return this.getUnlocalizedName() + "_oak";
		}else if(stack.getItemDamage() == 1){
			return this.getUnlocalizedName() + "_spruce";
		}else if(stack.getItemDamage() == 2){
			return this.getUnlocalizedName() + "_birch";
		}else if(stack.getItemDamage() == 3){
			return this.getUnlocalizedName() + "_jungle";
		}else if(stack.getItemDamage() == 4){
			return this.getUnlocalizedName() + "_acacia";
		}else if(stack.getItemDamage() == 5){
			return this.getUnlocalizedName() + "_darkoak";
		}else{
			return this.getUnlocalizedName();
		}
	}
	
	@Override
	public EnumActionResult onItemUse( EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack stack = playerIn.getHeldItem(hand);
		
		if(stack.getCount() == 0){
            return EnumActionResult.FAIL;
        }else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)){
            return EnumActionResult.FAIL;
        }else{
            Object object = this.singleSlab.getTypeForItem(stack);
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if(iblockstate.getBlock() == this.singleSlab){
                IProperty<?> iproperty = this.singleSlab.getVariantProperty();
                Comparable<?> comparable = iblockstate.getValue(iproperty);
                BlockSlab.EnumBlockHalf enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
                
                Owner owner = null;

                if(worldIn.getTileEntity(pos) instanceof IOwnable){
                	owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();
                	
                	if(!((IOwnable) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
                		if(!worldIn.isRemote){
                			PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("messages.reinforcedSlab"), ClientUtils.localize("messages.reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);
                		}
                		
                		return EnumActionResult.FAIL;
                	}             
                }
                
                if((side == EnumFacing.UP && enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable == object){
                    IBlockState iblockstate1 = makeState(iproperty, comparable);

                    if(worldIn.checkNoEntityCollision(iblockstate1.getCollisionBoundingBox(worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
                        worldIn.playSound(playerIn, pos, this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F);
                        stack.shrink(1);
                        
                        if(owner != null){
                        	((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
                        }
                    }

                    return EnumActionResult.SUCCESS;
                }
            }

            return this.tryPlace(stack, worldIn, playerIn, pos.offset(side), object) ? EnumActionResult.SUCCESS : super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
        }
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
        BlockPos blockpos1 = pos;
        IProperty<?> iproperty = this.singleSlab.getVariantProperty();
        Object object = this.singleSlab.getTypeForItem(stack);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if(iblockstate.getBlock() == this.singleSlab){
            boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

            if((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && object == iblockstate.getValue(iproperty)){
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState iblockstate1 = worldIn.getBlockState(pos);
        return iblockstate1.getBlock() == this.singleSlab && object == iblockstate1.getValue(iproperty) ? true : super.canPlaceBlockOnSide(worldIn, blockpos1, side, player, stack);
    }

    private boolean tryPlace(ItemStack stack, World worldIn, EntityPlayer player, BlockPos pos, Object variantInStack){
        IBlockState iblockstate = worldIn.getBlockState(pos);
        
        Owner owner = null;

        if(worldIn.getTileEntity(pos) instanceof IOwnable){
        	owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();
        }

        if(iblockstate.getBlock() == this.singleSlab){
            Comparable<?> comparable = iblockstate.getValue(this.singleSlab.getVariantProperty());

            if(comparable == variantInStack){
                IBlockState iblockstate1 = this.makeState(this.singleSlab.getVariantProperty(), comparable);

                if (worldIn.checkNoEntityCollision(iblockstate1.getCollisionBoundingBox(worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
                    worldIn.playSound(player, pos, this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F);
                    stack.shrink(1);
                    
                    if(owner != null){
                    	((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
                    }
                }

                return true;
            }
        }

        return false;
    }
    
    protected <T extends Comparable<T>> IBlockState makeState(IProperty<T> property, Comparable<?> comparable) {
        return this.doubleSlab.getDefaultState().withProperty(property, (T)comparable);
    }

}
