package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadBarrelBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;

public class KeypadBarrelBlock extends DisguisableBlock {
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<LidFacing> LID_FACING = EnumProperty.create("lid_facing", LidFacing.class);
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty FROG = BooleanProperty.create("frog");

	public KeypadBarrelBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(OPEN, false).setValue(LID_FACING, LidFacing.UP).setValue(FROG, false).setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LID_FACING, LidFacing.fromDirection(ctx.getNearestLookingDirection().getOpposite()));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof KeypadBarrelBlockEntity barrel)
			barrel.setCustomName(stack.getHoverName());

		if (entity instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			KeypadBarrelBlockEntity be = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);

			if (player.getItemInHand(hand).is(Items.FROG_SPAWN_EGG) && be.isOwnedBy(player)) {
				level.setBlockAndUpdate(pos, state.cycle(FROG));
				return InteractionResult.SUCCESS;
			}

			if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(state, level, pos, player);
				}
				else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get()))
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			MenuProvider menuProvider = getMenuProvider(state, level, pos);

			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_BARREL));
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof Container container) {
				Containers.dropContents(level, pos, container);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			if (be instanceof IPasscodeProtected passcodeProtected)
				SaltData.removeSalt(passcodeProtected.getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof KeypadBarrelBlockEntity barrel)
			barrel.recheckOpen();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadBarrelBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, LID_FACING, OPEN, FROG, WATERLOGGED);
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(Tags.Blocks.BARRELS_WOODEN);
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(SCContent.KEYPAD_BARREL.get());
		}

		@Override
		public boolean protect(Player player, Level level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			BarrelBlockEntity barrel = (BarrelBlockEntity) level.getBlockEntity(pos);
			LidFacing generalFacing = LidFacing.fromDirection(state.getValue(BarrelBlock.FACING));
			Direction horizontalFacing;
			CompoundTag tag;
			KeypadBarrelBlockEntity keypadBarrel;

			barrel.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = barrel.saveWithFullMetadata();
			barrel.clearContent();
			horizontalFacing = switch (generalFacing) {
				case UP, DOWN -> player == null ? Direction.NORTH : player.getDirection().getOpposite();
				case SIDEWAYS -> state.getValue(BarrelBlock.FACING);
			};
			level.setBlockAndUpdate(pos, SCContent.KEYPAD_BARREL.get().defaultBlockState().setValue(HORIZONTAL_FACING, horizontalFacing).setValue(LID_FACING, generalFacing).setValue(OPEN, false));
			keypadBarrel = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);
			keypadBarrel.load(tag);
			keypadBarrel.setPreviousBarrel(state.getBlock());

			if (player != null)
				keypadBarrel.setOwner(player.getUUID().toString(), player.getName().getString());

			return true;
		}

		@Override
		public boolean unprotect(Player player, Level level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			KeypadBarrelBlockEntity keypadBarrel = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);
			LidFacing lidFacing = state.getValue(LID_FACING);
			Direction direction = switch (lidFacing) {
				case UP -> Direction.UP;
				case SIDEWAYS -> state.getValue(KeypadBarrelBlock.HORIZONTAL_FACING);
				case DOWN -> Direction.DOWN;
			};
			CompoundTag tag;
			BarrelBlockEntity barrel;
			Block convertedBlock = BuiltInRegistries.BLOCK.get(keypadBarrel.getPreviousBarrel());

			if (convertedBlock == Blocks.AIR)
				convertedBlock = Blocks.BARREL;

			keypadBarrel.dropAllModules();
			keypadBarrel.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = keypadBarrel.saveWithFullMetadata();
			keypadBarrel.clearContent();
			level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(BarrelBlock.FACING, direction).setValue(OPEN, false));
			barrel = (BarrelBlockEntity) level.getBlockEntity(pos);
			barrel.load(tag);
			return true;
		}
	}

	public enum LidFacing implements StringRepresentable {
		UP("up"),
		SIDEWAYS("sideways"),
		DOWN("down");

		private final String name;

		private LidFacing(String name) {
			this.name = name;
		}

		public static LidFacing fromDirection(Direction direction) {
			return switch (direction) {
				case UP -> UP;
				case NORTH, SOUTH, EAST, WEST -> SIDEWAYS;
				case DOWN -> DOWN;
			};
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
