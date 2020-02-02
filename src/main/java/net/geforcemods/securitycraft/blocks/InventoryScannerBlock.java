package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public InventoryScannerBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.STONE));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(world.isRemote)
			return ActionResultType.PASS;
		else{
			if(isFacingAnotherScanner(world, pos) && player instanceof ServerPlayerEntity)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof INamedContainerProvider)
					NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
			}
			else if(hand == Hand.MAIN_HAND)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.inventoryScanner.getTranslationKey()), ClientUtils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);

			return ActionResultType.SUCCESS;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(world.isRemote)
			return;

		checkAndPlaceAppropriately(world, pos);

	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		InventoryScannerTileEntity connectedScanner = getConnectedInventoryScanner(world, pos);

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

		CustomizableTileEntity.link((CustomizableTileEntity)world.getTileEntity(pos), connectedScanner);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isRemote)
			return;

		InventoryScannerTileEntity connectedScanner = null;
		for(Direction facing : Direction.Plane.HORIZONTAL)
		{
			for(int i = 1; i <= ConfigHandler.CONFIG.inventoryScannerRange.get(); i++)
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
								if(field.get(InventoryScannerFieldBlock.FACING) == Direction.WEST || field.get(InventoryScannerFieldBlock.FACING) == Direction.EAST)
									world.destroyBlock(offsetJPos, false);
							}
							else if(facing == Direction.NORTH || facing == Direction.SOUTH)
							{
								if(field.get(InventoryScannerFieldBlock.FACING) == Direction.NORTH || field.get(InventoryScannerFieldBlock.FACING) == Direction.SOUTH)
									world.destroyBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (InventoryScannerTileEntity)world.getTileEntity(offsetIPos);
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

	public static InventoryScannerTileEntity getConnectedInventoryScanner(World world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).get(FACING);

		for(int i = 0; i <= ConfigHandler.CONFIG.inventoryScannerRange.get(); i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.isAir(world, offsetPos) && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
				return null;

			if(block == SCContent.inventoryScanner && state.get(FACING) == facing.getOpposite())
				return (InventoryScannerTileEntity)world.getTileEntity(offsetPos);
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

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
	{
		return false;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(!(blockAccess.getTileEntity(pos) instanceof InventoryScannerTileEntity) || ((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).getScanType() == null){
			return 0;
		}

		return (((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).getScanType() == null)
			return 0 ;

		return (((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).getScanType().equals("redstone") && ((InventoryScannerTileEntity) blockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new InventoryScannerTileEntity();
	}

}
