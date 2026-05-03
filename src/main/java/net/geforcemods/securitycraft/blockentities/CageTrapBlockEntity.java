package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CageTrapBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	private static final int BLOCK_PLACEMENTS_PER_TICK = 4;
	private final BooleanOption shouldCaptureMobsOption = new BooleanOption("captureMobs", false);
	private final DisabledOption disabled = new DisabledOption(false);
	private final IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private final List<Pair<BlockPos, BlockState>> multiblockModifyQueue = new ArrayList<>();

	public CageTrapBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.CAGE_TRAP_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!multiblockModifyQueue.isEmpty()) {
			for (int i = 0; i < BLOCK_PLACEMENTS_PER_TICK; i++) {
				Pair<BlockPos, BlockState> toPlace;
				BlockState stateInLevel, stateToPlace;
				BlockPos placeLocation;

				do {
					if (multiblockModifyQueue.isEmpty())
						return;

					toPlace = multiblockModifyQueue.removeFirst();
					stateInLevel = level.getBlockState(toPlace.getLeft());
					stateToPlace = toPlace.getRight();
					placeLocation = toPlace.getLeft();
				}
				//Repeat grabbing the next position to place if the state to be placed is already in the world
				while (stateInLevel == stateToPlace);

				if (stateInLevel.canBeReplaced()) {
					SoundType soundType = stateToPlace.getSoundType(level, placeLocation, null);
					BlockEntity placedBe;

					level.setBlockAndUpdate(placeLocation, stateToPlace);
					level.playSound(null, placeLocation, soundType.getPlaceSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
					level.gameEvent(null, GameEvent.BLOCK_PLACE, placeLocation);
					placedBe = level.getBlockEntity(placeLocation);

					if (placedBe instanceof OwnableBlockEntity ownable)
						ownable.setOwner(getOwner().getUUID(), getOwner().getName());

					if (placedBe instanceof ReinforcedIronBarsBlockEntity ironBarsBe)
						ironBarsBe.setCanDrop(false);
				}
			}
		}
	}

	public void assembleIronBars() {
		BlockPos topMiddle = worldPosition.above(4);

		loopIronBarPositions(worldPosition.mutable(), barPos -> {
			if (barPos.equals(topMiddle))
				multiblockModifyQueue.add(Pair.of(barPos.immutable(), SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get().defaultBlockState()));
			else
				multiblockModifyQueue.add(Pair.of(barPos.immutable(), SCContent.REINFORCED_IRON_BARS.get().getStateForPlacement(level, barPos)));
		});
		level.setBlockAndUpdate(worldPosition, blockState.setValue(CageTrapBlock.DEACTIVATED, true));
		level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 3.0F, 1.0F);
	}

	public void disassembleIronBars() {
		if (!level.isClientSide() && level.getBlockEntity(worldPosition) instanceof CageTrapBlockEntity be && blockState.getValue(CageTrapBlock.DEACTIVATED)) {
			be.loopIronBarPositions(worldPosition.mutable(), barPos -> {
				BlockEntity barBe = level.getBlockEntity(barPos);

				if (barBe instanceof IOwnable ownableBar && be.getOwner().owns(ownableBar)) {
					Block barBlock = level.getBlockState(barPos).getBlock();

					if (barBlock == SCContent.REINFORCED_IRON_BARS.get() || barBlock == SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get())
						level.destroyBlock(barPos, false);
				}
			});
		}
	}

	public void loopIronBarPositions(BlockPos.MutableBlockPos pos, Consumer<BlockPos.MutableBlockPos> positionAction) {
		pos.move(-1, 1, -1);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					//skip the middle column above the cage trap, but not the place where the horizontal iron bars are
					if (!(x == 1 && z == 1 && y != 3))
						positionAction.accept(pos);

					pos.move(0, 0, 1);
				}

				pos.move(1, 0, -3);
			}

			pos.move(-3, 1, 0);
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST
		};
	}

	public boolean capturesMobs() {
		return shouldCaptureMobsOption.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				shouldCaptureMobsOption, disabled, ignoreOwner
		};
	}
}
