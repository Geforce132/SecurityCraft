package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	@SideOnly(Side.CLIENT)
	private IIcon sidesIcon;


	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
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

	public static void searchForPlayers(World par1World, int par2, int par3, int par4, double searchRadius){
		if(!par1World.isRemote){
			double d0 = (searchRadius);

			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(par2, par3, par4, par2 + 1, par3 + 1, par4 + 1).expand(d0, d0, d0);
			axisalignedbb.maxY = par1World.getHeight();
			List<?> list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
			Iterator<?> iterator = list.iterator();
			EntityPlayer entityplayer;

			if(list.isEmpty())
				if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE) && par1World.getBlockMetadata(par2, par3, par4) == 1){
					togglePowerOutput(par1World, par2, par3, par4, false);
					return;
				}

			while (iterator.hasNext()){
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getOwner().getName());

				entityplayer = (EntityPlayer)iterator.next();

				if(par1World.getTileEntity(par2, par3, par4) == null || !(par1World.getTileEntity(par2, par3, par4) instanceof CustomizableSCTE))
					continue;

				if(((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(entityplayermp.getCommandSenderName().toLowerCase()))
					continue;

				if(PlayerUtils.isPlayerOnline(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getOwner().getName())){
					if(!((TileEntityPortableRadar) par1World.getTileEntity(par2, par3, par4)).shouldSendMessage(entityplayer))
						continue;

					PlayerUtils.sendMessageToPlayer(entityplayermp, StatCollector.translateToLocal("tile.portableRadar.name"), ((INameable)par1World.getTileEntity(par2, par3, par4)).hasCustomName() ? (StatCollector.translateToLocal("messages.portableRadar.withName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#n", EnumChatFormatting.ITALIC + ((INameable)par1World.getTileEntity(par2, par3, par4)).getCustomName() + EnumChatFormatting.RESET)) : (StatCollector.translateToLocal("messages.portableRadar.withoutName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(par2, par3, par4))), EnumChatFormatting.BLUE);
					((TileEntityPortableRadar) par1World.getTileEntity(par2, par3, par4)).setSentMessage();
				}

				if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE))
					togglePowerOutput(par1World, par2, par3, par4, true);
			}
		}
	}

	private static void togglePowerOutput(World par1World, int par2, int par3, int par4, boolean par5) {
		if(par5)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 3);
		else
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 3);

		BlockUtils.updateAndNotify(par1World, par2, par3, par4, par1World.getBlock(par2, par3, par4), 1, false);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(((CustomizableSCTE)par1IBlockAccess.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE) && par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1)
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
	public IIcon getIcon(int par1, int par2){
		return par1 == 1 ? topIcon : sidesIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister){
		sidesIcon = par1IconRegister.registerIcon("securitycraft:portableRadarSides");
		topIcon = par1IconRegister.registerIcon("securitycraft:portableRadarTop1");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, mod_SecurityCraft.configHandler.portableRadarSearchRadius, mod_SecurityCraft.configHandler.portableRadarDelay).nameable();
	}

}
