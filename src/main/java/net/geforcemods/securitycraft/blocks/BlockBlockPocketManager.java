package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBlockPocketManager extends BlockOwnable
{
	@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon side;
	@SideOnly(Side.CLIENT)
	private IIcon rest;
	@SideOnly(Side.CLIENT)
	private IIcon rest90;

	public BlockBlockPocketManager()
	{
		super(Material.iron);
	}

	@Override
	public int getRenderType()
	{
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
			player.openGui(SecurityCraft.instance, GuiHandler.BLOCK_POCKET_MANAGER, world, x, y, z);

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		world.setBlockMetadataWithNotify(x, y, z, MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3, 2);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityBlockPocketManager();
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		front = reg.registerIcon("securitycraft:block_pocket_manager_front");
		side = reg.registerIcon("quartz_block_lines_top");
		rest = reg.registerIcon("quartz_block_lines");
		rest90 = reg.registerIcon("securitycraft:quartz_block_lines_90");
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if((meta == 0 && side == 2) || (meta == 1 && side == 5) || (meta == 2 && side == 3) || (meta == 3 && side == 4))
			return front;
		else if((meta == 0 && (side == 4 || side == 5)) || (meta == 1 && (side == 2 || side == 3)) || (meta == 2 && (side == 4 || side == 5)) || (meta == 3 && (side == 2 || side == 3)))
			return this.side;
		else if((meta == 1 || meta == 3) && (side == 0 || side == 1))
			return rest;
		else if((meta == 0 && (side == 0 || side == 1 || side == 3)) || (meta == 1 && side == 4) || (meta == 2 && (side == 0 || side == 1 || side == 2)) || (meta == 3 && side == 5))
			return rest90;
		else return super.getIcon(side, meta);
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x0E7063;
	}
}
