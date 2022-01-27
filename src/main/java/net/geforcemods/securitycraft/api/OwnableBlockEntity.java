package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
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
		this(SCContent.beTypeOwnable);
	}

	public OwnableBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	/**
	 * Writes a tile entity to NBT.
	 *
	 * @return
	 */
	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (owner != null) {
			owner.write(tag, needsValidation());
		}

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void load(CompoundNBT tag) {
		super.load(tag);

		owner.read(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(packet.getTag());
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}
}
