package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockReinforcedSlabs extends ItemBlock {

	private BlockSlab singleSlab = (BlockSlab) mod_SecurityCraft.reinforcedStoneSlabs;
	private Block doubleSlab = mod_SecurityCraft.reinforcedDoubleStoneSlabs;
	
	public ItemBlockReinforcedSlabs(Block block) {
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
			return this.getUnlocalizedName() + "_stone";
		}else if(stack.getItemDamage() == 1){
			return this.getUnlocalizedName() + "_cobble";
		}else if(stack.getItemDamage() == 2){
			return this.getUnlocalizedName() + "_sandstone";
		}else{
			return this.getUnlocalizedName();
		}
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
        if(stack.stackSize == 0){
            return EnumActionResult.FAIL;
        }else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)){
            return EnumActionResult.FAIL;
        }else{
            Object object = this.singleSlab.getTypeForItem(stack);
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if(iblockstate.getBlock() instanceof BlockReinforcedSlabs){
                IProperty iproperty = this.singleSlab.getVariantProperty();
                Comparable<?> comparable = iblockstate.getValue(iproperty);
                BlockSlab.EnumBlockHalf enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
                
                Owner owner = null;

                if(worldIn.getTileEntity(pos) instanceof IOwnable){
                	owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();
                
                	if(!((IOwnable) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
                		if(!worldIn.isRemote){
                			PlayerUtils.sendMessageToPlayer(playerIn, I18n.translateToLocal("messages.reinforcedSlab"), I18n.translateToLocal("messages.reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);
                		}
                		
                		return EnumActionResult.FAIL;
                	}
                }
                
                if((side == EnumFacing.UP && enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable == object){
                    IBlockState iblockstate1 = this.getDoubleSlabBlock(comparable);
                    Block doubleSlabBlock = iblockstate1.getBlock();
                    
                    if(worldIn.checkNoEntityCollision(doubleSlabBlock.getCollisionBoundingBox(iblockstate1, worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
                        worldIn.playSound(playerIn, pos, this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F);
                        --stack.stackSize;
                        
                        if(owner != null){
                        	((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
                        }
                    }

                    return EnumActionResult.SUCCESS;
                }
            }

            return this.tryPlace(stack, worldIn, playerIn, pos.offset(side), object) ? EnumActionResult.SUCCESS : super.onItemUse(stack, playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
        }
    }

    private IBlockState getDoubleSlabBlock(Comparable<?> comparable) {
		if(comparable == BlockReinforcedSlabs.EnumType.STONE){
			return mod_SecurityCraft.reinforcedDoubleStoneSlabs.getDefaultState().withProperty(BlockReinforcedSlabs.VARIANT, comparable);
		}else if(comparable == BlockReinforcedSlabs.EnumType.COBBLESTONE){
			return mod_SecurityCraft.reinforcedDoubleStoneSlabs.getDefaultState().withProperty(BlockReinforcedSlabs.VARIANT, comparable);
		}else if(comparable == BlockReinforcedSlabs.EnumType.SANDSTONE){
			return mod_SecurityCraft.reinforcedDoubleStoneSlabs.getDefaultState().withProperty(BlockReinforcedSlabs.VARIANT, comparable);
		}else if(comparable == BlockReinforcedSlabs.EnumType.DIRT){
			return mod_SecurityCraft.reinforcedDoubleDirtSlab.getDefaultState().withProperty(BlockReinforcedSlabs.VARIANT, comparable);
		}else{
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
        BlockPos blockpos1 = pos;
        IProperty iproperty = this.singleSlab.getVariantProperty();
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

                if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(iblockstate1, worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)){
                    worldIn.playSound(player, pos, this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F);
                    --stack.stackSize;
                    
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
