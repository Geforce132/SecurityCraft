package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class KeycardLockBlockEntity extends KeycardReaderBlockEntity {
	protected BooleanOption exactLevel = new BooleanOption("exactLevel", true);

	public KeycardLockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYCARD_LOCK_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals(exactLevel.getName())) {
			boolean[] acceptedLevels = getAcceptedLevels();
			boolean swap = false;

			for (int i = 0; i < acceptedLevels.length; i++) {
				if (swap)
					acceptedLevels[i] = !acceptedLevels[i];
				else if (acceptedLevels[i])
					swap = true;
			}
		}
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled, exactLevel
		};
	}
}
