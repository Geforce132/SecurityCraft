package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.mines.BrushableMineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BrushableMineBlockEntity extends BrushableBlockEntity implements IOwnable {
	private Owner owner = new Owner();

	public BrushableMineBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean brush(long tickCount, ServerLevel level, LivingEntity player, Direction direction, ItemStack stack) {
		if (hitDirection == null)
			hitDirection = direction;

		brushCountResetsAtTick = tickCount + 40L;

		if (tickCount >= coolDownEndsAtTick) {
			coolDownEndsAtTick = tickCount + 10L;
			unpackLootTable(level, player, stack);

			int previousCompletionState = getCompletionState();

			if (++brushCount >= 10) {
				brushingCompleted(level, player, stack);
				return true;
			}
			else {
				level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 40);

				int newCompletionState = getCompletionState();

				if (previousCompletionState != newCompletionState) {
					if (newCompletionState > 1 && !getBlockState().getValue(BrushableMineBlock.SAFE) && !isOwnedBy(player))
						((BrushableMineBlock) getBlockState().getBlock()).explode(level, worldPosition);
					else
						level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.DUSTED, newCompletionState), 3);
				}
			}
		}

		return false;
	}

	@Override
	public void checkReset(ServerLevel level) {
		if (level != null) {
			if (brushCount != 0 && level.getGameTime() >= brushCountResetsAtTick) {
				int previousCompletionState = getCompletionState();

				brushCount = Math.max(0, this.brushCount - 2);

				int currentCompletionState = this.getCompletionState();

				if (previousCompletionState != currentCompletionState)
					level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.DUSTED, currentCompletionState), 3);

				brushCountResetsAtTick = this.level.getGameTime() + 4L;
			}

			if (brushCount == 0) {
				hitDirection = null;
				brushCountResetsAtTick = 0L;
				coolDownEndsAtTick = 0L;
			}
			else
				level.scheduleTick(getBlockPos(), getBlockState().getBlock(), (int) (brushCountResetsAtTick - level.getGameTime()));
		}
	}

	@Override
	public void brushingCompleted(ServerLevel level, LivingEntity entity, ItemStack stack) {
		if (level != null && level.getServer() != null) {
			Block turnInto = Blocks.AIR;

			dropContent(level, entity, stack);
			level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_BRUSH_BLOCK_COMPLETE, getBlockPos(), Block.getId(getBlockState()));

			if (getBlockState().getBlock() instanceof BrushableMineBlock brushableMineBlock)
				turnInto = brushableMineBlock.getTurnsInto();

			level.setBlock(worldPosition, turnInto.defaultBlockState(), 3);
		}
	}

	@Override
	public boolean isValidBlockState(BlockState state) {
		return getType().isValid(state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return SCContent.BRUSHABLE_MINE_BLOCK_ENTITY.get();
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		if (owner != null)
			owner.save(tag, needsValidation());
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		owner.load(tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}
}
