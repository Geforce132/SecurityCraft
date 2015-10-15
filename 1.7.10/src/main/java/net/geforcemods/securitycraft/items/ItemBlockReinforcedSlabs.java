package net.geforcemods.securitycraft.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlockReinforcedSlabs extends ItemBlock {
	
	private final boolean isNotSlab; // <--- Not really, I just don't know what the purpose of this boolean is yet.
	private final BlockSlab singleSlab;
	private final ReinforcedSlabType slabType;
	
	public ItemBlockReinforcedSlabs(Block par1Block, BlockReinforcedWoodSlabs par2Block, Boolean par3, ReinforcedSlabType slabType){
        super(par1Block);
        this.singleSlab = (BlockSlab) par2Block;
        this.isNotSlab = par3;
        this.slabType = slabType;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
	
	public ItemBlockReinforcedSlabs(Block par1Block, BlockReinforcedSlabs par2Block, Boolean par3, ReinforcedSlabType slabType){
		super(par1Block);
        this.singleSlab = (BlockSlab) par2Block;
        this.isNotSlab = par3;
        this.slabType = slabType;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
	}
	
	/**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1){
    	if(slabType == ReinforcedSlabType.OTHER){
    		return mod_SecurityCraft.reinforcedStoneSlabs.getIcon(2, par1);
    	}else{
            return mod_SecurityCraft.reinforcedWoodSlabs.getIcon(2, par1);
    	}
    }
	
	public String getUnlocalizedName(ItemStack stack){
		if(slabType == ReinforcedSlabType.WOOD){
			if(stack.getItemDamage() == 0){
				return this.getUnlocalizedName() + "_oak";
			}else if(stack.getItemDamage() == 1){
				return this.getUnlocalizedName() + "_spruce";
			}else if(stack.getItemDamage() == 2){
				return this.getUnlocalizedName() + "_birch";
			}if(stack.getItemDamage() == 3){
				return this.getUnlocalizedName() + "_jungle";
			}else if(stack.getItemDamage() == 4){
				return this.getUnlocalizedName() + "_acacia";
			}else if(stack.getItemDamage() == 5){
				return this.getUnlocalizedName() + "_darkoak";
			}else{
				return this.getUnlocalizedName();
			}
		}else{
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
	}
	
    public int getMetadata(int par1){
        return par1;
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
        if(this.isNotSlab){
            return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
        }else if(par1ItemStack.stackSize == 0){
            return false;
        }else if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
            return false;
        }else{
            Block block = par3World.getBlock(par4, par5, par6);
            int i1 = par3World.getBlockMetadata(par4, par5, par6);
            int j1 = i1 & 7;
            boolean flag = (i1 & 8) != 0;
            
            String name = null;
            String uuid = null;

            if(par3World.getTileEntity(par4, par5, par6) instanceof IOwnable){
            	name = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerName();
            	uuid = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerUUID();

            	if(!BlockUtils.isOwnerOfBlock((IOwnable) par3World.getTileEntity(par4, par5, par6), par2EntityPlayer)){
            		if(!par3World.isRemote){
            			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Reinforced Slab", "You must be the owner of this block to turn it into a double slab.", EnumChatFormatting.RED);
            		}
            		
            		return false;
            	}
            }
            
            if((par7 == 1 && !flag || par7 == 0 && flag) && isBlock(block) && j1 == par1ItemStack.getItemDamage()){
                if(par3World.checkNoEntityCollision(this.getBlockVariant(i1).getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.getBlockVariant(block, i1), (block == mod_SecurityCraft.reinforcedStoneSlabs && i1 == 2 ? 2 : j1), 3)){
                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.getBlockVariant(block, i1).stepSound.func_150496_b(), (this.getBlockVariant(block, i1).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(block, i1).stepSound.getPitch() * 0.8F);
                    --par1ItemStack.stackSize;
                    
                    if(name != null && uuid != null){
                    	((IOwnable) par3World.getTileEntity(par4, par5, par6)).setOwner(uuid, name);
                    }
                }

                return true;
            }else{
                return this.func_150946_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack){
        int i1 = par2;
        int j1 = par3;
        int k1 = par4;
        Block block = par1World.getBlock(par2, par3, par4);
        int l1 = par1World.getBlockMetadata(par2, par3, par4);
        int i2 = l1 & 7;
        boolean flag = (l1 & 8) != 0;

        if((par5 == 1 && !flag || par5 == 0 && flag) && block == this.singleSlab && i2 == par7ItemStack.getItemDamage()){
            return true;
        }else{
            if(par5 == 0){
                --par3;
            }

            if(par5 == 1){
                ++par3;
            }

            if(par5 == 2){
                --par4;
            }

            if(par5 == 3){
                ++par4;
            }

            if(par5 == 4){
                --par2;
            }

            if(par5 == 5){
                ++par2;
            }

            Block block1 = par1World.getBlock(par2, par3, par4);
            int j2 = par1World.getBlockMetadata(par2, par3, par4);
            i2 = j2 & 7;
            return block1 == this.singleSlab && i2 == par7ItemStack.getItemDamage() ? true : super.func_150936_a(par1World, i1, j1, k1, par5, par6EntityPlayer, par7ItemStack);
        }
    }

    private boolean func_150946_a(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7){
        if(par7 == 0){
            --par5;
        }

        if(par7 == 1){
            ++par5;
        }

        if(par7 == 2){
            --par6;
        }

        if(par7 == 3){
            ++par6;
        }

        if(par7 == 4){
            --par4;
        }

        if(par7 == 5){
            ++par4;
        }

        Block block = par3World.getBlock(par4, par5, par6);
        int i1 = par3World.getBlockMetadata(par4, par5, par6);
        int j1 = i1 & 7;
        
        String name = null;
        String uuid = null;

        if(par3World.getTileEntity(par4, par5, par6) instanceof IOwnable){
        	name = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerName();
        	uuid = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerUUID();
        }

        if(block == this.singleSlab && j1 == par1ItemStack.getItemDamage()){
            if(par3World.checkNoEntityCollision(this.getBlockVariant(i1).getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.getBlockVariant(i1), j1, 3)){
            	par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.getBlockVariant(i1).stepSound.func_150496_b(), (this.getBlockVariant(i1).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(i1).stepSound.getPitch() * 0.8F);
                --par1ItemStack.stackSize;
                
                if(name != null && uuid != null){
                	((IOwnable) par3World.getTileEntity(par4, par5, par6)).setOwner(uuid, name);
                }
            }

            return true;
        }else{
            return false;
        }
    }
    
    public Block getBlockVariant(Block slab, int meta){
    	if(slab == mod_SecurityCraft.reinforcedWoodSlabs){
    		return mod_SecurityCraft.reinforcedDoubleWoodSlabs;
    	}
    	
    	if(slab == mod_SecurityCraft.reinforcedStoneSlabs){
    		return mod_SecurityCraft.reinforcedDoubleStoneSlabs;
    	}
    	
    	if(slab == mod_SecurityCraft.reinforcedDirtSlab){
    		return mod_SecurityCraft.reinforcedDoubleDirtSlab;
    	}
    		
    	return slab;   	
    }
    
    public Block getBlockVariant(int meta){
    	if(slabType == ReinforcedSlabType.OTHER){
        	return Block.getBlockFromItem(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 1, meta).getItem());
    	}else{
        	return Block.getBlockFromItem(new ItemStack(mod_SecurityCraft.reinforcedWoodSlabs, 1, meta).getItem());
    	}
    }
    
    public boolean isBlock(Block block){
    	if(slabType == ReinforcedSlabType.OTHER){
        	return block == mod_SecurityCraft.reinforcedStoneSlabs || block == mod_SecurityCraft.reinforcedDirtSlab;
    	}else{
        	return block == mod_SecurityCraft.reinforcedWoodSlabs;
    	}
    }
    
public static enum ReinforcedSlabType {
	WOOD,
	OTHER;
}

}
