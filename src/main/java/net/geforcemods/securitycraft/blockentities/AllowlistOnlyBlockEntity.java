package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity {
	public AllowlistOnlyBlockEntity() {
		super(SCContent.ALLOWLIST_ONLY_BLOCK_ENTITY.get());
	}

	public AllowlistOnlyBlockEntity(TileEntityType<?> type) {
		super(type);
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
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		if (state.hasProperty(BlockStateProperties.POWERED)) {
			Block block = state.getBlock();

			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
			level.updateNeighborsAt(pos, block);

			if (block instanceof HorizontalFaceBlock)
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
				return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		}
	}
}