package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.Arrays;

public class BlockReinforcedStainedGlass extends BlockBreakable implements ITileEntityProvider, IReinforcedBlock {

	private static final IIcon[] iicons = new IIcon[16];

	public BlockReinforcedStainedGlass(Material material) {
		super("glass", material, false);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass(){
		return 1;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public int flipMeta(int meta){
		return ~meta & 15;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for (int i = 0; i < iicons.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int meta){
		return meta;
	}

	@Override
	public int quantityDropped(Random random){
		return 1;
	}

	@Override
	protected boolean canSilkHarvest(){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iicons[meta % iicons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		for(int i = 0; i < iicons.length; ++i)
			iicons[i] = register.registerIcon(getTextureName() + "_" + ItemDye.dyeIcons[flipMeta(i)]);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.stained_glass
		});
	}

	@Override
	public int getAmount()
	{
		return 16;
	}
}
