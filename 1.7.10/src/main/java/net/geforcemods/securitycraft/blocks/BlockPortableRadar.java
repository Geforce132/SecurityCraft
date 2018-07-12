package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
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

	public static void searchForPlayers(World world, int x, int y, int z, double searchRadius){
		if(!world.isRemote){
			AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(searchRadius, searchRadius, searchRadius);
			searchArea.maxY = world.getHeight();
			List<?> list = world.getEntitiesWithinAABB(EntityPlayer.class, searchArea);
			Iterator<?> iterator = list.iterator();
			EntityPlayer entityplayer;

			if(list.isEmpty())
				if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityPortableRadar && ((CustomizableSCTE) world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) && world.getBlockMetadata(x, y, z) == 1){
					togglePowerOutput(world, x, y, z, false);
					return;
				}

			while (iterator.hasNext()){
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(((TileEntityPortableRadar)world.getTileEntity(x, y, z)).getOwner().getName());

				entityplayer = (EntityPlayer)iterator.next();

				if(world.getTileEntity(x, y, z) == null || !(world.getTileEntity(x, y, z) instanceof CustomizableSCTE))
					continue;

				if(((CustomizableSCTE) world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(entityplayermp.getCommandSenderName().toLowerCase()))
					continue;

				if(PlayerUtils.isPlayerOnline(((TileEntityPortableRadar)world.getTileEntity(x, y, z)).getOwner().getName())){
					if(!((TileEntityPortableRadar) world.getTileEntity(x, y, z)).shouldSendMessage(entityplayer))
						continue;

					PlayerUtils.sendMessageToPlayer(entityplayermp, StatCollector.translateToLocal("tile.securitycraft:portableRadar.name"), ((INameable)world.getTileEntity(x, y, z)).hasCustomName() ? (StatCollector.translateToLocal("messages.securitycraft:portableRadar.withName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#n", EnumChatFormatting.ITALIC + ((INameable)world.getTileEntity(x, y, z)).getCustomName() + EnumChatFormatting.RESET)) : (StatCollector.translateToLocal("messages.securitycraft:portableRadar.withoutName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(x, y, z))), EnumChatFormatting.BLUE);
					((TileEntityPortableRadar) world.getTileEntity(x, y, z)).setSentMessage();
				}

				if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityPortableRadar && ((CustomizableSCTE) world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE))
					togglePowerOutput(world, x, y, z, true);
			}
		}
	}

	private static void togglePowerOutput(World world, int x, int y, int z, boolean side) {
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
