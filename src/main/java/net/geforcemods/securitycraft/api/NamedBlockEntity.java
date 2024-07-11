package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class NamedBlockEntity extends OwnableBlockEntity implements INameSetter {
	private String customName;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (hasCustomName())
			tag.setString("customName", getName());

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("customName")) {
			String name = tag.getString("customName");

			if (!name.equals("name"))
				customName = name;
		}
	}

	public void sync() {
		if (world == null)
			return;

		if (world.isRemote)
			SecurityCraft.network.sendToServer(new SyncTENBTTag(getPos(), writeToNBT(new NBTTagCompound())));
		else
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getUpdatePacket());
	}

	@Override
	public String getName() {
		return hasCustomName() ? this.customName : getDefaultName().getFormattedText();
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && !customName.isEmpty() && !customName.equals(getDefaultName().getFormattedText());
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(getBlockType());
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
		sync();
	}
}
