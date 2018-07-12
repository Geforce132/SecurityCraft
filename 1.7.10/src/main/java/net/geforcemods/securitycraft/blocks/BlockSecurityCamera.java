package net.geforcemods.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketCRemoveLGView;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSecurityCamera extends BlockContainer {

	public BlockSecurityCamera(Material material) {
		super(material);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		int meta = access.getBlockMetadata(x, y, z);
		float px = 1.0F/16.0F; //one sixteenth of a block

		if(meta == 3  || meta == 7)
			setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		else if(meta == 4 || meta == 8)
			setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		else if(meta == 2 || meta == 6)
			setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		else if(meta == 0 || meta == 9)
			setBlockBounds(px * 5, 1.0F - px * 2, px * 5, px * 11, 1.0F, px * 11);
		else
			setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		int k1 = meta & 8;
		byte b0 = -1;

		if(side == 2 && world.isSideSolid(x, y, z + 1, NORTH))
			b0 = 4;

		if(side == 3 && world.isSideSolid(x, y, z - 1, SOUTH))
			b0 = 3;

		if(side == 4 && world.isSideSolid(x + 1, y, z, WEST))
			b0 = 2;

		if(side == 5 && world.isSideSolid(x - 1, y, z, EAST))
			b0 = 1;

		if(side == 0 && world.isSideSolid(x, y + 1, z, DOWN))
			b0 = 0;

		return b0 == 0 ? 0 : b0 + k1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int metadata = world.getBlockMetadata(x, y, z);

		if(metadata == 1 || metadata == 5)
		{
			if(!world.isSideSolid(x - 1, y, z, EAST))
				BlockUtils.destroyBlock(world, x, y, z, true);
		}
		else if(metadata == 2 || metadata == 6)
		{
			if(!world.isSideSolid(x + 1, y, z, WEST))
				BlockUtils.destroyBlock(world, x, y, z, true);
		}
		else if(metadata == 3 || metadata == 7)
		{
			if(!world.isSideSolid(x, y, z - 1, SOUTH))
				BlockUtils.destroyBlock(world, x, y, z, true);
		}
		else if(metadata == 4 || metadata == 8)
		{
			if(!world.isSideSolid(x, y, z + 1, NORTH))
				BlockUtils.destroyBlock(world, x, y, z, true);
		}
		else if(metadata == 0 || metadata == 9)
		{
			if(!world.isSideSolid(x, y + 1, z, DOWN))
				BlockUtils.destroyBlock(world, x, y, z, true);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		SecurityCraft.network.sendToAll(new PacketCRemoveLGView(x, y, z, world.provider.dimensionId));
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side){
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		return (dir == NORTH && world.isSideSolid(x, y, z + 1, NORTH)) ||
				(dir == SOUTH && world.isSideSolid(x, y, z - 1, SOUTH)) ||
				(dir == WEST  && world.isSideSolid(x + 1, y, z, WEST )) ||
				(dir == EAST  && world.isSideSolid(x - 1, y, z, EAST )) ||
				(dir == DOWN && world.isSideSolid(x, y + 1, z, DOWN ));
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return !world.getBlock(x, y, z).isReplaceable(world, x, y, z) ^ //exclusive or
				(world.isSideSolid(x - 1, y, z, EAST) ||
						world.isSideSolid(x + 1, y, z, WEST) ||
						world.isSideSolid(x, y, z - 1, SOUTH) ||
						world.isSideSolid(x, y, z + 1, NORTH) ||
						world.isSideSolid(x, y + 1, z,  DOWN));
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		return ((CustomizableSCTE)access.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) && BlockUtils.isMetadataBetween(access, x, y, z, 5, 9) ? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side){
		return ((CustomizableSCTE)access.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) && BlockUtils.isMetadataBetween(access, x, y, z, 5, 9) ? 15 : 0;
	}

	public void mountCamera(World world, int x, int y, int z, int par5, EntityPlayer player){
		if(!world.isRemote && player.ridingEntity == null)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:securityCamera.name"), StatCollector.translateToLocal("messages.securitycraft:securityCamera.mounted"), EnumChatFormatting.GREEN);

		if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, x, y, z, par5, (EntitySecurityCamera) player.ridingEntity);
			world.spawnEntityInWorld(dummyEntity);
			player.mountEntity(dummyEntity);
			return;
		}

		EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, x, y, z, par5, player);
		world.spawnEntityInWorld(dummyEntity);
		player.mountEntity(dummyEntity);

		for(Object e : world.loadedEntityList)
			if(e instanceof EntityLiving)
				if(((EntityLiving)e).getAttackTarget() == player)
					((EntityLiving)e).setAttackTarget(null);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(SCContent.securityCamera);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(SCContent.securityCamera);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySecurityCamera().nameable();
	}

}
