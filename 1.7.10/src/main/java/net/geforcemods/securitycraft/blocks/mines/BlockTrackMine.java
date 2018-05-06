package net.geforcemods.securitycraft.blocks.mines;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTrackMine extends BlockRailBase implements IExplosive, ITileEntityProvider {

	@SideOnly(Side.CLIENT)
	private IIcon theIcon;

	public BlockTrackMine() {
		super(false);
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z){
		BlockUtils.destroyBlock(world, x, y, z, false);

		world.createExplosion(cart, x, y + 1, z, SecurityCraft.config.smallerMineExplosion ? 4.0F : 8.0F, true);

		cart.setDead();
	}

	@Override
	public void explode(World world, int x, int y, int z) {
		BlockUtils.destroyBlock(world, x, y, z, false);
		world.createExplosion((Entity) null, x, y + 1, z, SecurityCraft.config.smallerMineExplosion ? 4.0F : 8.0F, true);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}

	@Override
	protected void onRedstoneSignal(World world, int x, int y, int z, int meta, int par6, Block block){
		try{
			BlockRailBase.Rail rail = new BlockRailBase.Rail(world, x, y, z);
			Method method = rail.getClass().getDeclaredMethod("func_150650_a");

			method.setAccessible(true);
			int number = (Integer) method.invoke(rail);

			if (block.canProvidePower() && number == 3)
				refreshTrackShape(world, x, y, z, false);

		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(InvocationTargetException e) {
			e.printStackTrace();
		}catch(NoSuchMethodException e){
			e.printStackTrace();
		}catch(SecurityException e){
			e.printStackTrace();
		}
	}

	@Override
	public void activateMine(World world, int x, int y, int z) {}

	@Override
	public void defuseMine(World world, int x, int y, int z) {}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta >= 6 ? theIcon : blockIcon;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		super.registerIcons(register);
		theIcon = register.registerIcon("securitycraft:rail_mineTurned");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}
