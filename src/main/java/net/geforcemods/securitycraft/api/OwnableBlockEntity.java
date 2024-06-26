package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * Used to give this tile entity an owner
 */
public class OwnableBlockEntity extends TileEntity implements IOwnable {
	private Owner owner = new Owner();

	public OwnableBlockEntity() {
		this(SCContent.OWNABLE_BLOCK_ENTITY.get());
	}

	public OwnableBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		owner.load(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(save(new CompoundNBT()));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		handleUpdateTag(null, packet.getTag());
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public boolean shouldRender() {
		return false;
	}
}
