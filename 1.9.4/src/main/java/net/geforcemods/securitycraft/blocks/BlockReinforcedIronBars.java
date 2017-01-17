package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedIronBars extends BlockPane implements ITileEntityProvider {
	
	public BlockReinforcedIronBars(Material par1Material, boolean par2) {
		super(par1Material, par2);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, SoundType.METAL, 33);
	}
	
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random) {	
    	BlockUtils.setBlock(par1World, pos, Blocks.IRON_BARS);
    }
     
    public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param){
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null ? tileentity.receiveClientEvent(id, param) : false;
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getItem(World p_149694_1_, BlockPos pos, IBlockState state){
        return new ItemStack(Item.getItemFromBlock(this));
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    @SideOnly(Side.CLIENT)
    public Item getItemDropped(IBlockState state, Random par2Random, int par3){
        return Item.getItemFromBlock(this);
    }

	public TileEntity createNewTileEntity(World par1, int par2) {
		return new TileEntityOwnable();
	}

}
