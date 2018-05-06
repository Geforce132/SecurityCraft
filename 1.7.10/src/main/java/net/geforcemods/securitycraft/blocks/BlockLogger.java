package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockLogger extends BlockOwnable {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon frontIcon;

	public BlockLogger(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else{
			player.openGui(SecurityCraft.instance, GuiHandler.USERNAME_LOGGER_GUI_ID, world, x, y, z);
			return true;
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if(!world.isRemote)
			if(world.isBlockIndirectlyGettingPowered(x, y, z))
				((TileEntityLogger) world.getTileEntity(x, y, z)).attackNextTick();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, stack);

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
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(side == 3 && meta == 0)
			return frontIcon;

		return side == 1 ? topIcon : (side == 0 ? topIcon : (side != meta ? blockIcon : frontIcon));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		blockIcon = register.registerIcon("securitycraft:usernameLoggerSide");
		frontIcon = register.registerIcon("securitycraft:usernameLoggerFront");
		topIcon = register.registerIcon("securitycraft:usernameLoggerTop");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLogger().attacks(EntityPlayer.class, SecurityCraft.config.usernameLoggerSearchRadius, 80);
	}

}
