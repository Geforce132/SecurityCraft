package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;

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
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
		setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	public static void searchForPlayers(World par1World, BlockPos pos, IBlockState state){
		if(!par1World.isRemote){
			double d0 = (mod_SecurityCraft.configHandler.portableRadarSearchRadius);

			AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(d0, d0, d0).addCoord(0.0D, par1World.getHeight(), 0.0D);
			List<?> list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
			Iterator<?> iterator = list.iterator();
			EntityPlayer entityplayer;

			if(list.isEmpty())
				if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) && ((Boolean) state.getValue(POWERED)).booleanValue()){
					togglePowerOutput(par1World, pos, false);
					return;
				}

			if(!((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
				togglePowerOutput(par1World, pos, false);

			while (iterator.hasNext()){
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName());

				entityplayer = (EntityPlayer)iterator.next();

				if(entityplayermp != null && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(entityplayermp.getCommandSenderName().toLowerCase()))
					continue;

				if(PlayerUtils.isPlayerOnline(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName())){
					if(!((TileEntityPortableRadar) par1World.getTileEntity(pos)).shouldSendMessage(entityplayer))
						continue;

					PlayerUtils.sendMessageToPlayer(entityplayermp, StatCollector.translateToLocal("tile.portableRadar.name"), ((INameable)par1World.getTileEntity(pos)).hasCustomName() ? (StatCollector.translateToLocal("messages.portableRadar.withName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#n", EnumChatFormatting.ITALIC + ((INameable)par1World.getTileEntity(pos)).getCustomName() + EnumChatFormatting.RESET)) : (StatCollector.translateToLocal("messages.portableRadar.withoutName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), EnumChatFormatting.BLUE);
					((TileEntityPortableRadar) par1World.getTileEntity(pos)).setSentMessage();
				}

				if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
					togglePowerOutput(par1World, pos, true);
			}
		}
	}

	private static void togglePowerOutput(World par1World, BlockPos pos, boolean par5) {
		if(par5 && !((Boolean) par1World.getBlockState(pos).getValue(POWERED)).booleanValue()){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, true, true);
			BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}else if(!par5 && ((Boolean) par1World.getBlockState(pos).getValue(POWERED)).booleanValue()){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
		if(((CustomizableSCTE)par1IBlockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) && ((Boolean) state.getValue(POWERED)).booleanValue())
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return meta == 1 ? getDefaultState().withProperty(POWERED, true) : getDefaultState().withProperty(POWERED, false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((Boolean) state.getValue(POWERED)).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, mod_SecurityCraft.configHandler.portableRadarSearchRadius, mod_SecurityCraft.configHandler.portableRadarDelay).nameable();
	}

}
