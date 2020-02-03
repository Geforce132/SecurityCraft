package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeypadFurnaceBlock extends OwnableBlock implements IPasswordConvertible {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	private static final VoxelShape NORTH_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 3, 16, 16, 16), Block.makeCuboidShape(1, 1, 2, 15, 2, 3)), VoxelShapes.combine(Block.makeCuboidShape(4, 1, 0, 12, 2, 2), Block.makeCuboidShape(5, 1, 1, 11, 2, 2), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(1, 2, 3, 15, 15, 4), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape NORTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 3, 16, 16, 16), Block.makeCuboidShape(1, 1, 2, 15, 15, 3)), VoxelShapes.combine(Block.makeCuboidShape(4, 14, 0, 12, 15, 2), Block.makeCuboidShape(5, 14, 1, 11, 15, 2), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape EAST_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 13, 16, 16), Block.makeCuboidShape(13, 1, 1, 14, 2, 15)), VoxelShapes.combine(Block.makeCuboidShape(14, 1, 4, 16, 2, 12), Block.makeCuboidShape(14, 1, 5, 15, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(12, 2, 1, 13, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape EAST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 13, 16, 16), Block.makeCuboidShape(13, 1, 1, 14, 15, 15)), VoxelShapes.combine(Block.makeCuboidShape(14, 14, 4, 16, 15, 12), Block.makeCuboidShape(14, 14, 5, 15, 15, 11), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape SOUTH_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 13), Block.makeCuboidShape(1, 1, 13, 15, 2, 14)), VoxelShapes.combine(Block.makeCuboidShape(4, 1, 14, 12, 2, 16), Block.makeCuboidShape(5, 1, 14, 11, 2, 15), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(1, 2, 12, 15, 15, 13), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape SOUTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 13), Block.makeCuboidShape(1, 1, 13, 15, 15, 14)), VoxelShapes.combine(Block.makeCuboidShape(4, 14, 14, 12, 15, 16), Block.makeCuboidShape(5, 14, 14, 11, 15, 15), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape WEST_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(3, 0, 0, 16, 16, 16), Block.makeCuboidShape(2, 1, 1, 3, 2, 15)), VoxelShapes.combine(Block.makeCuboidShape(0, 1, 4, 2, 2, 12), Block.makeCuboidShape(1, 1, 5, 2, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(3, 2, 1, 4, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape WEST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(3, 0, 0, 16, 16, 16), Block.makeCuboidShape(2, 1, 1, 3, 15, 15)), VoxelShapes.combine(Block.makeCuboidShape(0, 14, 4, 2, 15, 12), Block.makeCuboidShape(1, 14, 5, 2, 15, 11), IBooleanFunction.ONLY_FIRST));

	public KeypadFurnaceBlock(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.get(FACING))
		{
			case NORTH:
				if(state.get(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if(state.get(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if(state.get(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if(state.get(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(!(newState.getBlock() instanceof KeypadFurnaceBlock))
		{
			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof IInventory)
			{
				InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
				world.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(!world.isRemote)
		{
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker))
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		if(!BlockUtils.getBlockProperty(world, pos, KeypadFurnaceBlock.OPEN))
			BlockUtils.setBlockProperty(world, pos, KeypadFurnaceBlock.OPEN, true);

		if(player instanceof ServerPlayerEntity)
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof INamedContainerProvider)
			{
				world.playEvent((PlayerEntity)null, 1006, pos, 0);
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(OPEN, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(OPEN);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeypadFurnaceTileEntity();
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.FURNACE;
	}

	@Override
	public boolean convert(PlayerEntity player, World world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).get(FACING);
		FurnaceTileEntity furnace = (FurnaceTileEntity)world.getTileEntity(pos);
		CompoundNBT tag = furnace.write(new CompoundNBT());

		furnace.clear();
		world.setBlockState(pos, SCContent.keypadFurnace.getDefaultState().with(FACING, facing).with(OPEN, false));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName().getFormattedText());
		((KeypadFurnaceTileEntity)world.getTileEntity(pos)).read(tag);
		return true;
	}
}
