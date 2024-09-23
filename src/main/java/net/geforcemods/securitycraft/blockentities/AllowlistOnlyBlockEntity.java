package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity {
	public AllowlistOnlyBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ALLOWLIST_ONLY_BLOCK_ENTITY.get(), pos, state);
	}

	public AllowlistOnlyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		if (denotation.contains("pressure"))
			return super.getModuleDescriptionId("generic.reinforced_pressure_plate", module);
		else if (denotation.contains("button"))
			return super.getModuleDescriptionId("generic.reinforced_button", module);
		else
			return super.getModuleDescriptionId(denotation, module);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		if (state.hasProperty(BlockStateProperties.POWERED)) {
			Block block = state.getBlock();

			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
			level.updateNeighborsAt(pos, block);

			if (block instanceof FaceAttachedHorizontalDirectionalBlock)
				level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), block);
			else
				level.updateNeighborsAt(pos.below(), block);
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	private static Direction getConnectedDirection(BlockState state) {
		switch (state.getValue(BlockStateProperties.ATTACH_FACE)) {
			case CEILING:
				return Direction.DOWN;
			case FLOOR:
				return Direction.UP;
			default:
				return state.getValue(BlockStateProperties.FACING);
		}
	}
}