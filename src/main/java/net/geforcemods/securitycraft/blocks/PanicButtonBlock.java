package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends ButtonBlock implements EntityBlock {
	private static final VoxelShape FLOOR_NS_POWERED = Block.box(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.box(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.box(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.box(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.box(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.box(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.box(3, 5, 0, 13, 11, 1);
	private static final VoxelShape WALL_S_UNPOWERED = Block.box(3, 5, 0, 13, 11, 2);
	private static final VoxelShape WALL_E_POWERED = Block.box(0, 5, 3, 1, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.box(0, 5, 3, 2, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.box(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.box(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.box(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.box(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.box(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.box(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock(boolean isWooden, Block.Properties properties) {
		super(isWooden, properties);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		boolean newPowered = !state.getValue(POWERED);

		world.setBlockAndUpdate(pos, state.setValue(POWERED, newPowered));
		playSound(player, world, pos, newPowered);

		if(state.getValue(FACE) == AttachFace.WALL)
			notifyNeighbors(world, pos, state.getValue(FACING));
		else if(state.getValue(FACE) == AttachFace.CEILING)
			notifyNeighbors(world, pos, Direction.DOWN);
		else if(state.getValue(FACE) == AttachFace.FLOOR)
			notifyNeighbors(world, pos, Direction.UP);

		return InteractionResult.SUCCESS;
	}

	private void notifyNeighbors(Level world, BlockPos pos, Direction facing)
	{
		world.updateNeighborsAt(pos, this);
		world.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);
		world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param){
		super.triggerEvent(state, world, pos, id, param);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return switch(state.getValue(FACE)) {
			case FLOOR -> switch(state.getValue(FACING)) {
				case NORTH, SOUTH -> state.getValue(POWERED) ? FLOOR_NS_POWERED : FLOOR_NS_UNPOWERED;
				case EAST, WEST -> state.getValue(POWERED) ? FLOOR_EW_POWERED : FLOOR_EW_UNPOWERED;
				default -> Shapes.block();
			};
			case WALL -> switch(state.getValue(FACING)) {
				case NORTH -> state.getValue(POWERED) ? WALL_N_POWERED : WALL_N_UNPOWERED;
				case SOUTH -> state.getValue(POWERED) ? WALL_S_POWERED : WALL_S_UNPOWERED;
				case EAST -> state.getValue(POWERED) ? WALL_E_POWERED : WALL_E_UNPOWERED;
				case WEST -> state.getValue(POWERED) ? WALL_W_POWERED : WALL_W_UNPOWERED;
				default -> Shapes.block();
			};
			case CEILING -> switch(state.getValue(FACING)) {
				case NORTH, SOUTH -> state.getValue(POWERED) ? CEILING_NS_POWERED : CEILING_NS_UNPOWERED;
				case EAST, WEST -> state.getValue(POWERED) ? CEILING_EW_POWERED : CEILING_EW_UNPOWERED;
				default -> Shapes.block();
			};
			default -> Shapes.block();
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		return Shapes.empty();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}

	@Override
	protected SoundEvent getSound(boolean turningOn)
	{
		return turningOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}
}
