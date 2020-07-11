package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class SecretSignTileEntity extends SignTileEntity implements IOwnable
{
	private Owner owner = new Owner();

	@Override
	public TileEntityType<?> getType()
	{
		return SCContent.teTypeSecretSign;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		if(owner != null){
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if (tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		write(tag);
		return new SUpdateTileEntityPacket(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		read(getBlockState(), packet.getNbtCompound());
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getPos()));
	}
}
