package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class SecurityCameraBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public SecurityCameraBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.getHeldItem(hand).getItem() != SCContent.cameraMonitor) {
			TileEntity be = world.getTileEntity(pos);

			if (be instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) be).isOwnedBy(player)) {
				if (!world.isRemote)
					player.openGui(SecurityCraft.instance, Screens.SINGLE_LENS.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());

				return true;
			}
		}

		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess access, BlockPos pos) {
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (state.getValue(FACING) == EnumFacing.DOWN)
			return EnumBlockRenderType.MODEL;
		else
			return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof SecurityCameraBlockEntity) {
			((SecurityCameraBlockEntity) te).dropAllModules();
			InventoryHelper.dropInventoryItems(world, pos, ((SecurityCameraBlockEntity) te).getLensContainer());
		}

		super.breakBlock(world, pos, state);
		world.notifyNeighborsOfStateChange(pos.north(), world.getBlockState(pos).getBlock(), true);
		world.notifyNeighborsOfStateChange(pos.south(), world.getBlockState(pos).getBlock(), true);
		world.notifyNeighborsOfStateChange(pos.east(), world.getBlockState(pos).getBlock(), true);
		world.notifyNeighborsOfStateChange(pos.west(), world.getBlockState(pos).getBlock(), true);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing dir = state.getValue(FACING);
		float px = 1.0F / 16.0F; //one sixteenth of a block

		if (dir == EnumFacing.SOUTH)
			return new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		else if (dir == EnumFacing.NORTH)
			return new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		else if (dir == EnumFacing.WEST)
			return new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		else if (dir == EnumFacing.DOWN)
			return new AxisAlignedBB(px * 5, 1.0F - px * 2, px * 5, px * 11, 1.0F, px * 11);
		else
			return new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = getDefaultState().withProperty(FACING, facing);

		if (!world.isSideSolid(pos.offset(facing.getOpposite()), facing)) {
			for (EnumFacing newFacing : Plane.HORIZONTAL) {
				if (world.isSideSolid(pos.offset(newFacing.getOpposite()), newFacing)) {
					state = state.withProperty(FACING, newFacing);
					break;
				}
			}
		}

		return state;
	}

	public void mountCamera(World world, int x, int y, int z, EntityPlayer player) {
		if (!world.isRemote) {
			EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
			WorldServer serverWorld = (WorldServer) world;
			SecurityCamera dummyEntity;
			BlockPos pos = new BlockPos(x, y, z);
			Chunk chunk = serverWorld.getChunk(pos);
			ChunkPos chunkPos = chunk.getPos();
			int viewDistance = serverPlayer.server.getPlayerList().getViewDistance();
			TileEntity te = world.getTileEntity(pos);
			Ticket ticket = ForgeChunkManager.requestTicket(SecurityCraft.instance, world, Type.ENTITY);

			if (serverPlayer.getSpectatingEntity() instanceof SecurityCamera)
				serverPlayer.getSpectatingEntity().setDead();

			dummyEntity = new SecurityCamera(world, x, y, z);
			ticket.bindEntity(dummyEntity);
			dummyEntity.setChunkTicket(ticket);

			//two loops to prevent ConcurrentModificationException
			for (int cx = chunkPos.x - viewDistance; cx <= chunkPos.x + viewDistance; cx++) {
				for (int cz = chunkPos.z - viewDistance; cz <= chunkPos.z + viewDistance; cz++) {
					ForgeChunkManager.forceChunk(ticket, new ChunkPos(cx, cz));
				}
			}

			//let the player track the chunks the camera can see
			for (int cx = chunkPos.x - viewDistance; cx <= chunkPos.x + viewDistance; cx++) {
				for (int cz = chunkPos.z - viewDistance; cz <= chunkPos.z + viewDistance; cz++) {
					serverWorld.getPlayerChunkMap().getOrCreateEntry(cx, cz).addPlayer(serverPlayer);
				}
			}

			world.spawnEntity(dummyEntity);
			//can't use EntityPlayerMP#setSpectatingEntity here because it also teleports the player
			serverPlayer.spectatingEntity = dummyEntity;
			SecurityCraft.network.sendTo(new SetCameraView(dummyEntity), serverPlayer);

			//update which entities the player is tracking to allow for the correct ones to show up
			for (EntityTrackerEntry entry : serverWorld.getEntityTracker().entries) {
				entry.updatePlayerEntity(serverPlayer);
			}

			if (te instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) te).startViewing();
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		if (side == EnumFacing.UP)
			return false;
		else
			return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (state.getValue(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (state.getValue(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).isModuleEnabled(ModuleType.REDSTONE) && state.getValue(FACING) == side)
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockAt(world, pos) && !canPlaceBlockOnSide(world, pos, state.getValue(FACING))) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta <= 5)
			return getDefaultState().withProperty(FACING, (EnumFacing.values()[meta] == EnumFacing.UP) ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(POWERED))
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SecurityCameraBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		EnumFacing facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}
}
