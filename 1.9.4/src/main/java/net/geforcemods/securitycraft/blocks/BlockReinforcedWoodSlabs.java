package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedWoodSlabs extends BlockWoodSlab implements ITileEntityProvider {

	private final boolean isDouble;
	
	public BlockReinforcedWoodSlabs(boolean isDouble){		
		this.isDouble = isDouble;
		setSoundType(SoundType.WOOD);
		
		if(this.isDouble()){
			this.setCreativeTab(null);
		}
		
		if(!this.isDouble()){
			this.useNeighborBrightness = true;
		}
	}
	
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }
	
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }

    public boolean isDouble(){
		return isDouble;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

}
