package net.geforcemods.securitycraft.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBlockPocketWall extends BlockOwnable
{
	private static final AxisAlignedBB FULL_BLOCK_AABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
	private IIcon[] seeThrough = new IIcon[6];
	private IIcon[] nonSeeThrough = new IIcon[6];

	public BlockBlockPocketWall()
	{
		super(Material.rock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity)
	{
		if(entity instanceof EntityPlayer)
		{
			TileEntity te1 = world.getTileEntity(x, y, z);

			if(te1 instanceof TileEntityBlockPocket)
			{
				TileEntityBlockPocket te = (TileEntityBlockPocket)te1;

				if(te.getManager() == null)
					return;

				if(te.getManager().hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(te.getManager().getWorld(), te.getManager().xCoord, te.getManager().yCoord, te.getManager().zCoord, EnumCustomModules.WHITELIST).contains(entity.getCommandSenderName().toLowerCase()))
					return;
				else if(!te.getOwner().isOwner((EntityPlayer)entity))
					list.add(FULL_BLOCK_AABB);
			}
		}
		else
			list.add(FULL_BLOCK_AABB);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
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

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityBlockPocket();
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		seeThrough[0] = reg.registerIcon("securitycraft:block_pocket_wall_see_through_bottom");
		seeThrough[1] = reg.registerIcon("securitycraft:block_pocket_wall_see_through_top");
		seeThrough[2] = reg.registerIcon("securitycraft:block_pocket_wall_see_through_side");
		seeThrough[3] = nonSeeThrough[2];
		seeThrough[4] = nonSeeThrough[2];
		seeThrough[5] = nonSeeThrough[2];
		nonSeeThrough[0] = reg.registerIcon("quartz_block_bottom");
		nonSeeThrough[1] = reg.registerIcon("quartz_block_top");
		nonSeeThrough[2] = reg.registerIcon("quartz_block_side");
		nonSeeThrough[3] = nonSeeThrough[2];
		nonSeeThrough[4] = nonSeeThrough[2];
		nonSeeThrough[5] = nonSeeThrough[2];
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return meta == 0 ? seeThrough[meta] : nonSeeThrough[meta];
	}
}
