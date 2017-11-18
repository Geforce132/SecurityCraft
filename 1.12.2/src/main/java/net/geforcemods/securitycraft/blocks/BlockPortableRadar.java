package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	}

	public static void searchForPlayers(World par1World, BlockPos pos, IBlockState state){
		if(!par1World.isRemote){
			double d0 = (mod_SecurityCraft.configHandler.portableRadarSearchRadius);

			AxisAlignedBB axisalignedbb = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(d0, d0, d0).contract(-0.0D, -par1World.getHeight(), -0.0D);
			List<?> list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
			Iterator<?> iterator = list.iterator();
			EntityPlayer entityplayer;

			if(list.isEmpty())
				if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) && state.getValue(POWERED).booleanValue()){
					togglePowerOutput(par1World, pos, false);
					return;
				}

			if(!((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
				togglePowerOutput(par1World, pos, false);

			while (iterator.hasNext()){
				EntityPlayerMP entityplayermp = par1World.getMinecraftServer().getPlayerList().getPlayerByUsername(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName());

				entityplayer = (EntityPlayer)iterator.next();

				if(entityplayermp != null && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(entityplayermp.getName().toLowerCase()))
					continue;

				if(PlayerUtils.isPlayerOnline(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName())){
					if(!((TileEntityPortableRadar) par1World.getTileEntity(pos)).shouldSendMessage(entityplayer))
						continue;

					PlayerUtils.sendMessageToPlayer(entityplayermp, ClientUtils.localize("tile.portableRadar.name"), ((INameable)par1World.getTileEntity(pos)).hasCustomName() ? (ClientUtils.localize("messages.portableRadar.withName").replace("#p", TextFormatting.ITALIC + entityplayer.getName() + TextFormatting.RESET).replace("#n", TextFormatting.ITALIC + ((INameable)par1World.getTileEntity(pos)).getCustomName() + TextFormatting.RESET)) : (ClientUtils.localize("messages.portableRadar.withoutName").replace("#p", TextFormatting.ITALIC + entityplayer.getName() + TextFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), TextFormatting.BLUE);
					((TileEntityPortableRadar) par1World.getTileEntity(pos)).setSentMessage();
				}

				if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
					togglePowerOutput(par1World, pos, true);
			}
		}
	}

	private static void togglePowerOutput(World par1World, BlockPos pos, boolean par5) {
		if(par5 && !par1World.getBlockState(pos).getValue(POWERED).booleanValue()){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, true, true);
			BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}else if(!par5 && par1World.getBlockState(pos).getValue(POWERED).booleanValue()){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue() && ((CustomizableSCTE) blockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
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
		return state.getValue(POWERED).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, mod_SecurityCraft.configHandler.portableRadarSearchRadius, mod_SecurityCraft.configHandler.portableRadarDelay).nameable();
	}

}
