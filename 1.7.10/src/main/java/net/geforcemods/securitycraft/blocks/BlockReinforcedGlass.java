package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockReinforcedGlass extends BlockGlass implements ITileEntityProvider {

	public BlockReinforcedGlass(Material par1Material) {
		super(par1Material, false);
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister){
        this.blockIcon = par1IIconRegister.registerIcon("securitycraft:glass_reinforced");
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
	
	@Override
	public int quantityDropped(Random r)
	{
		return 1;
	}
}
