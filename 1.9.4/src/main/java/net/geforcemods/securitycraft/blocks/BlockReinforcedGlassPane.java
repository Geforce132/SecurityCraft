package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedGlassPane extends BlockPane implements ITileEntityProvider {

	public BlockReinforcedGlassPane(Material par1Material, boolean par2) {
		super(par1Material, par2);
		ReflectionHelper.setPrivateValue(Block.class, this, true, 26);
		setSoundType(SoundType.GLASS);
	}

    public void breakBlock(World p_149749_1_, BlockPos pos, IBlockState state){
        super.breakBlock(p_149749_1_, pos, state);
        p_149749_1_.removeTileEntity(pos);
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

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
	
	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}
}
