package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadBarrelBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;

public class KeypadBarrelBlock extends DisguisableBlock {
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<LidFacing> LID_FACING = EnumProperty.create("lid_facing", LidFacing.class);
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty FROG = BooleanProperty.create("frog");

	public KeypadBarrelBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(OPEN, false).setValue(LID_FACING, LidFacing.UP).setValue(FROG, false).setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LID_FACING, LidFacing.fromDirection(ctx.getNearestLookingDirection().getOpposite()));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof KeypadBarrelBlockEntity)
				((KeypadBarrelBlockEntity) te).setCustomName(stack.getHoverName());
		}

		if (entity instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) entity));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			KeypadBarrelBlockEntity be = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);

			if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, level, pos, player);
				}
				else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		if (!level.isClientSide) {
			INamedContainerProvider menuProvider = getMenuProvider(state, level, pos);

			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_BARREL));
			}
		}
	}

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		return te instanceof INamedContainerProvider ? (INamedContainerProvider) te : null;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IInventory) {
				InventoryHelper.dropContents(level, pos, (IInventory) te);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			if (te instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof KeypadBarrelBlockEntity)
			((KeypadBarrelBlockEntity) te).recheckOpen();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeypadBarrelBlockEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
		return Container.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, LID_FACING, OPEN, FROG, WATERLOGGED);
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isValidStateForConversion(BlockState state) {
			return state.is(Tags.Blocks.BARRELS_WOODEN);
		}

		@Override
		public boolean convert(PlayerEntity player, World level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			BarrelTileEntity barrel = (BarrelTileEntity) level.getBlockEntity(pos);
			LidFacing generalFacing = LidFacing.fromDirection(state.getValue(BarrelBlock.FACING));
			Direction horizontalFacing = Direction.NORTH;
			CompoundNBT tag;
			KeypadBarrelBlockEntity keypadBarrel;

			barrel.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = barrel.save(new CompoundNBT());
			barrel.clearContent();

			switch (generalFacing) {
				case UP:
				case DOWN:
					horizontalFacing = player.getDirection().getOpposite();
					break;
				case SIDEWAYS:
					horizontalFacing = state.getValue(BarrelBlock.FACING);
			}

			level.setBlockAndUpdate(pos, SCContent.KEYPAD_BARREL.get().defaultBlockState().setValue(HORIZONTAL_FACING, horizontalFacing).setValue(LID_FACING, generalFacing).setValue(OPEN, state.getValue(BarrelBlock.OPEN)));
			keypadBarrel = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);
			keypadBarrel.load(keypadBarrel.getBlockState(), tag);
			keypadBarrel.setOwner(player.getUUID().toString(), player.getName().getString());
			return true;
		}
	}

	public enum LidFacing implements IStringSerializable {
		UP("up"),
		SIDEWAYS("sideways"),
		DOWN("down");

		private final String name;

		private LidFacing(String name) {
			this.name = name;
		}

		public static LidFacing fromDirection(Direction direction) {
			switch (direction) {
				case UP:
					return UP;
				case NORTH:
				case SOUTH:
				case EAST:
				case WEST:
					return SIDEWAYS;
				case DOWN:
					return DOWN;
				default:
					return UP;
			}
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
