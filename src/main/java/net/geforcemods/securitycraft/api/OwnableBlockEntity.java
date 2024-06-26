package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OwnableBlockEntity extends BlockEntity implements IOwnable {
	private Owner owner = new Owner();

	public OwnableBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	public OwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		return PasscodeUtils.filterPasscodeAndSaltFromTag(saveWithoutMetadata());
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

	public boolean shouldRender() {
		return false;
	}
}
