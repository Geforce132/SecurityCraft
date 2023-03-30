package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.mines.BrushableMineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BrushableMineBlockEntity extends SuspiciousSandBlockEntity implements IOwnable {
	private Owner owner = new Owner();

	public BrushableMineBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean brush(long tickCount, Player player, Direction direction) {
		if (hitDirection == null)
			hitDirection = direction;

		brushCountResetsAtTick = tickCount + 40L;

		if (tickCount >= coolDownEndsAtTick && level instanceof ServerLevel) {
			coolDownEndsAtTick = tickCount + 10L;
			unpackLootTable(player);

			int previousCompletionState = getCompletionState();

			if (++brushCount >= 10) {
				brushingCompleted(player);
				return true;
			}
			else {
				level.scheduleTick(getBlockPos(), SCContent.SUSPICIOUS_SAND_MINE.get(), 40);

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
	public void checkReset() {
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
				level.scheduleTick(getBlockPos(), SCContent.SUSPICIOUS_SAND_MINE.get(), (int) (brushCountResetsAtTick - level.getGameTime()));
		}
	}

	@Override
	public BlockEntityType<?> getType() {
		return SCContent.BRUSHABLE_MINE_BLOCK_ENTITY.get();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		if (owner != null)
			owner.save(tag, needsValidation());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		owner.load(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		handleUpdateTag(packet.getTag());
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
