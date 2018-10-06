package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.ITileEntityProvider;
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

public class BlockReinforcedStainedGlassPanes extends BlockStainedGlassPane implements ITileEntityProvider, IReinforcedBlock {

	private static final IIcon[] paneTextures = new IIcon[16];
	private static final IIcon[] topPaneTextures = new IIcon[16];

	public BlockReinforcedStainedGlassPanes() {
		super();
		ObfuscationReflectionHelper.setPrivateValue(BlockPane.class, this, "glass_reinforced", 2);
		ObfuscationReflectionHelper.setPrivateValue(BlockPane.class, this, "glass_reinforced_pane_top", 0);
	}

	@Override
	public int damageDropped(int meta){
		return meta;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getItemIcon(int side, int meta){
		return paneTextures[meta % paneTextures.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon func_150104_b(int meta){
		return topPaneTextures[~meta & 15];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for (int i = 0; i < paneTextures.length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		for(int i = 0; i < paneTextures.length; ++i){
			paneTextures[i] = register.registerIcon(getTextureName() + "_" + ItemDye.dyeIcons[func_150103_c(i)]);
			topPaneTextures[i] = register.registerIcon(getTextureName() + "_pane_top_" + ItemDye.dyeIcons[func_150103_c(i)]);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.stained_glass_pane
		});
	}

	@Override
	public int getAmount()
	{
		return 16;
	}
}
