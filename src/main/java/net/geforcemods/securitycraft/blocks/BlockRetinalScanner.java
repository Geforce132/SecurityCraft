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
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRetinalScanner extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon rtIconTop;

	@SideOnly(Side.CLIENT)
	private IIcon rtIconFront;

	@SideOnly(Side.CLIENT)
	private IIcon rtIconFrontActive;

	public BlockRetinalScanner(Material material) {
		super(material);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if (!world.isRemote && world.getBlockMetadata(x, y, z) >= 7 && world.getBlockMetadata(x, y, z) <= 10)
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		if(access.getBlockMetadata(x, y, z) == 7 || access.getBlockMetadata(x, y, z) == 8 || access.getBlockMetadata(x, y, z) == 9 || access.getBlockMetadata(x, y, z) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if(side == 3 && meta == 0)
			return rtIconFront;

		if(meta == 7 || meta == 8 || meta == 9 || meta == 10)
			return side == 1 ? rtIconTop : (side == 0 ? rtIconTop : (side != (meta - 5) ? blockIcon : rtIconFrontActive));
		else
			return side == 1 ? rtIconTop : (side == 0 ? rtIconTop : (side != meta ? blockIcon : rtIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("furnace_side");
		rtIconTop = register.registerIcon("furnace_top");
		rtIconFront = register.registerIcon("securitycraft:retinalScannerFront");
		rtIconFrontActive = register.registerIcon("securitycraft:retinalScannerFront");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRetinalScanner().activatedByView();
	}

}
