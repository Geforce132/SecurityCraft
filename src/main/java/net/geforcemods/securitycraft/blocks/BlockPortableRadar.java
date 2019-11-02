package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	@SideOnly(Side.CLIENT)
	private IIcon sidesIcon;


	public BlockPortableRadar(Material material) {
		super(material);
		setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	public static void togglePowerOutput(World world, int x, int y, int z, boolean side) {
		if(side)
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
		else
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);

		BlockUtils.updateAndNotify(world, x, y, z, world.getBlock(x, y, z), 1, false);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		if(((CustomizableSCTE)access.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) && access.getBlockMetadata(x, y, z) == 1)
			return 15;
		else
			return 0;
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 1 ? topIcon : sidesIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		sidesIcon = register.registerIcon("securitycraft:portableRadarSides");
		topIcon = register.registerIcon("securitycraft:portableRadarTop1");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, SecurityCraft.config.portableRadarSearchRadius, SecurityCraft.config.portableRadarDelay).nameable();
	}

}
