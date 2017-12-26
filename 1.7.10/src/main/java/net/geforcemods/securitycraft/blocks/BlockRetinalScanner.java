package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRetinalScanner extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon rtIconTop;

	@SideOnly(Side.CLIENT)
	private IIcon rtIconFront;

	@SideOnly(Side.CLIENT)
	private IIcon rtIconFrontActive;

	public BlockRetinalScanner(Material par1) {
		super(par1);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		@SuppressWarnings("cast")
		int l = MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);

		if (l == 1)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);

		if (l == 2)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);

		if (l == 3)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if (!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) >= 7 && par1World.getBlockMetadata(par2, par3, par4) <= 10)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		if(par1 == 3 && par2 == 0)
			return rtIconFront;

		if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10)
			return par1 == 1 ? rtIconTop : (par1 == 0 ? rtIconTop : (par1 != (par2 - 5) ? blockIcon : rtIconFrontActive));
		else
			return par1 == 1 ? rtIconTop : (par1 == 0 ? rtIconTop : (par1 != par2 ? blockIcon : rtIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		blockIcon = par1IconRegister.registerIcon("furnace_side");
		rtIconTop = par1IconRegister.registerIcon("furnace_top");
		rtIconFront = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
		rtIconFrontActive = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityRetinalScanner().activatedByView();
	}

}
