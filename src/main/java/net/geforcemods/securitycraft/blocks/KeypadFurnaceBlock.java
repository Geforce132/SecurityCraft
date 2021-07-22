package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeypadFurnaceBlock extends OwnableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape NORTH_OPEN = VoxelShapes.joinUnoptimized(VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 3, 16, 16, 16), Block.box(1, 1, 2, 15, 2, 3)), VoxelShapes.joinUnoptimized(Block.box(4, 1, 0, 12, 2, 2), Block.box(5, 1, 1, 11, 2, 2), IBooleanFunction.ONLY_FIRST)), Block.box(1, 2, 3, 15, 15, 4), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape NORTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 3, 16, 16, 16), Block.box(1, 1, 2, 15, 15, 3)), VoxelShapes.joinUnoptimized(Block.box(4, 14, 0, 12, 15, 2), Block.box(5, 14, 1, 11, 15, 2), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape EAST_OPEN = VoxelShapes.joinUnoptimized(VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 0, 13, 16, 16), Block.box(13, 1, 1, 14, 2, 15)), VoxelShapes.joinUnoptimized(Block.box(14, 1, 4, 16, 2, 12), Block.box(14, 1, 5, 15, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.box(12, 2, 1, 13, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape EAST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 0, 13, 16, 16), Block.box(13, 1, 1, 14, 15, 15)), VoxelShapes.joinUnoptimized(Block.box(14, 14, 4, 16, 15, 12), Block.box(14, 14, 5, 15, 15, 11), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape SOUTH_OPEN = VoxelShapes.joinUnoptimized(VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 13), Block.box(1, 1, 13, 15, 2, 14)), VoxelShapes.joinUnoptimized(Block.box(4, 1, 14, 12, 2, 16), Block.box(5, 1, 14, 11, 2, 15), IBooleanFunction.ONLY_FIRST)), Block.box(1, 2, 12, 15, 15, 13), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape SOUTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 13), Block.box(1, 1, 13, 15, 15, 14)), VoxelShapes.joinUnoptimized(Block.box(4, 14, 14, 12, 15, 16), Block.box(5, 14, 14, 11, 15, 15), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape WEST_OPEN = VoxelShapes.joinUnoptimized(VoxelShapes.or(VoxelShapes.or(Block.box(3, 0, 0, 16, 16, 16), Block.box(2, 1, 1, 3, 2, 15)), VoxelShapes.joinUnoptimized(Block.box(0, 1, 4, 2, 2, 12), Block.box(1, 1, 5, 2, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.box(3, 2, 1, 4, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape WEST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.box(3, 0, 0, 16, 16, 16), Block.box(2, 1, 1, 3, 15, 15)), VoxelShapes.joinUnoptimized(Block.box(0, 14, 4, 2, 15, 12), Block.box(1, 14, 5, 2, 15, 11), IBooleanFunction.ONLY_FIRST));

	public KeypadFurnaceBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(LIT, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.getValue(FACING))
		{
			case NORTH:
				if(state.getValue(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if(state.getValue(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if(state.getValue(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if(state.getValue(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default: return VoxelShapes.block();
		}
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(!(newState.getBlock() instanceof KeypadFurnaceBlock))
		{
			TileEntity tileentity = world.getBlockEntity(pos);

			if (tileentity instanceof IInventory)
			{
				InventoryHelper.dropContents(world, pos, (IInventory)tileentity);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isClientSide)
		{
			KeypadFurnaceTileEntity te = (KeypadFurnaceTileEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return ActionResultType.FAIL;
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(world, pos, player);
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		BlockState state = world.getBlockState(pos);
		if(!state.getValue(KeypadFurnaceBlock.OPEN))
			world.setBlockAndUpdate(pos, state.setValue(KeypadFurnaceBlock.OPEN, true));

		if(player instanceof ServerPlayerEntity)
		{
			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof INamedContainerProvider)
			{
				world.levelEvent((PlayerEntity)null, 1006, pos, 0);
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite()).setValue(OPEN, false).setValue(LIT, false);
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(state.getValue(OPEN) && state.getValue(LIT))
		{
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;

			if(rand.nextDouble() < 0.1D)
				world.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

			Direction direction = state.getValue(FACING);
			Axis axis = direction.getAxis();
			double randomNumber = rand.nextDouble() * 0.6D - 0.3D;
			double xOffset = axis == Axis.X ? direction.getStepX() * 0.52D : randomNumber;
			double yOffset = rand.nextDouble() * 6.0D / 16.0D;
			double zOffset = axis == Axis.Z ? direction.getStepZ() * 0.52D : randomNumber;

			world.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, OPEN, LIT);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeypadFurnaceTileEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class Convertible implements IPasswordConvertible
	{
		@Override
		public Block getOriginalBlock()
		{
			return Blocks.FURNACE;
		}

		@Override
		public boolean convert(PlayerEntity player, World world, BlockPos pos)
		{
			BlockState state = world.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			boolean lit = state.getValue(LIT);
			FurnaceTileEntity furnace = (FurnaceTileEntity)world.getBlockEntity(pos);
			CompoundNBT tag = furnace.save(new CompoundNBT());

			furnace.clearContent();
			world.setBlockAndUpdate(pos, SCContent.KEYPAD_FURNACE.get().defaultBlockState().setValue(FACING, facing).setValue(OPEN, false).setValue(LIT, lit));
			((KeypadFurnaceTileEntity)world.getBlockEntity(pos)).load(world.getBlockState(pos), tag);
			((IOwnable) world.getBlockEntity(pos)).setOwner(player.getUUID().toString(), player.getName().getString());
			return true;
		}
	}
}
