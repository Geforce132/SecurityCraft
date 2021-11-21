package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityNamed extends TileEntityOwnable implements INameSetter {
	private String customName = "";

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setString("customName", customName);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("customName"))
			customName = tag.getString("customName");
	}

	public void sync() {
		if(world == null) return;

		if(world.isRemote)
			ClientUtils.syncTileEntity(this);
		else
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getUpdatePacket());
	}

	@Override
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return !customName.isEmpty() && !customName.equals(getDefaultName().getFormattedText());
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(customName) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(blockType.getTranslationKey() + ".name");
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
		sync();
	}
}
