package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockInventoryScanner extends ContainerBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BlockInventoryScanner(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.STONE));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag)
	{
		super.onBlockAdded(state, world, pos, oldState, flag);
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, BlockState state)
	{
		if (!world.isRemote)
		{
			//			BlockState north = world.getBlockState(pos.north());
			//			BlockState south = world.getBlockState(pos.south());
			//			BlockState west = world.getBlockState(pos.west());
			//			BlockState east = world.getBlockState(pos.east());
			Direction facing = state.get(FACING);

			if (facing == Direction.NORTH)// && north.isFullCube() && !south.isFullCube())
				facing = Direction.SOUTH;
			else if (facing == Direction.SOUTH)// && south.isFullCube() && !north.isFullCube())
				facing = Direction.NORTH;
			else if (facing == Direction.WEST)// && west.isFullCube() && !east.isFullCube())
				facing = Direction.EAST;
			else if (facing == Direction.EAST)// && east.isFullCube() && !west.isFullCube())
				facing = Direction.WEST;

			world.setBlockState(pos, state.with(FACING, facing), 2);
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(world.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(world, pos) && player instanceof ServerPlayerEntity)
				NetworkHooks.openGui((ServerPlayerEntity)player, new TEInteractionObject(GuiHandler.INVENTORY_SCANNER, world, pos), pos);
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.inventoryScanner.getTranslationKey()), ClientUtils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

			return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		if(entity instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)entity));

		if(world.isRemote)
			return;

		//		BlockState north = world.getBlockState(pos.north());
		//		BlockState south = world.getBlockState(pos.south());
		//		BlockState west = world.getBlockState(pos.west());
		//		BlockState east = world.getBlockState(pos.east());
		Direction facing = state.get(FACING);

		if (facing == Direction.NORTH)// && north.isFullCube() && !south.isFullCube())
			facing = Direction.SOUTH;
		else if (facing == Direction.SOUTH)// && south.isFullCube() && !north.isFullCube())
			facing = Direction.NORTH;
		else if (facing == Direction.WEST)// && west.isFullCube() && !east.isFullCube())
			facing = Direction.EAST;
		else if (facing == Direction.EAST)// && east.isFullCube() && !west.isFullCube())
			facing = Direction.WEST;

		world.setBlockState(pos, state.with(FACING, facing), 2);

		checkAndPlaceAppropriately(world, pos);

	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		Direction facing = world.getBlockState(pos).get(FACING);
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.inventoryScannerField)
				return;
		}

		for(int i = 1; i < loopBoundary; i++)
		{
			world.setBlockState(pos.offset(facing, i), SCContent.inventoryScannerField.getDefaultState().with(FACING, facing));
		}

		CustomizableSCTE.link((CustomizableSCTE)world.getTileEntity(pos), connectedScanner);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;
		for(Direction facing : Direction.Plane.HORIZONTAL)
		{
			for(int i = 1; i <= CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				BlockPos offsetIPos = pos.offset(facing, i);

				if(BlockUtils.getBlock(world, offsetIPos) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						BlockPos offsetJPos = pos.offset(facing, j);
						BlockState field = world.getBlockState(offsetJPos);

						//checking if the field is oriented correctly
						if(field.getBlock() == SCContent.inventoryScannerField)
						{
							if(facing == Direction.WEST || facing == Direction.EAST)
							{
								if(field.get(BlockInventoryScannerField.FACING) == Direction.WEST || field.get(BlockInventoryScannerField.FACING) == Direction.EAST)
									world.destroyBlock(offsetJPos, false);
							}
							else if(facing == Direction.NORTH || facing == Direction.SOUTH)
							{
								if(field.get(BlockInventoryScannerField.FACING) == Direction.NORTH || field.get(BlockInventoryScannerField.FACING) == Direction.SOUTH)
									world.destroyBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (TileEntityInventoryScanner)world.getTileEntity(offsetIPos);
					break;
				}
			}
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().size(); i++)
			{
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static TileEntityInventoryScanner getConnectedInventoryScanner(World world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).get(FACING);

		for(int i = 0; i <= CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.isAir(world, offsetPos) && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
				return null;

			if(block == SCContent.inventoryScanner && state.get(FACING) == facing.getOpposite())
				return (TileEntityInventoryScanner)world.getTileEntity(offsetPos);
		}

		return null;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(!(blockAccess.getTileEntity(pos) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType() == null){
			SecurityCraft.log("type is null on the " + EffectiveSide.get() + " side");
			return 0;
		}

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((TileEntityInventoryScanner) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityInventoryScanner();
	}

}
