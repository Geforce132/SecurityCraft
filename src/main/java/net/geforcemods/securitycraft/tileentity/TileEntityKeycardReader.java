package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityKeycardReader extends TileEntityDisguisable implements ILockable {

	private boolean[] acceptedLevels = {true, false, false, false, false};
	private int signature = 0;
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagCompound acceptedLevelsTag = new NBTTagCompound();

		for(int i = 1; i <= 5; i++)
		{
			acceptedLevelsTag.setBoolean("lvl" + i, acceptedLevels[i - 1]);
		}

		tag.setTag("acceptedLevels", acceptedLevelsTag);
		tag.setInteger("signature", signature);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		//carry over old data
		if(tag.hasKey("passLV"))
		{
			boolean oldRequiresExactKeycard = false;
			int oldPassLV = tag.getInteger("passLV") - 1; //old data was 1-indexed, new one is 0-indexed

			if(tag.hasKey("requiresExactKeycard"))
				oldRequiresExactKeycard = tag.getBoolean("requiresExactKeycard");

			for(int i = 0; i < 5; i++)
			{
				acceptedLevels[i] = oldRequiresExactKeycard ? i == oldPassLV : i >= oldPassLV;
			}
		}

		//don't try to load this data if it doesn't exist, otherwise everything will be "false"
		if(tag.hasKey("acceptedLevels", NBT.TAG_COMPOUND))
		{
			NBTTagCompound acceptedLevelsTag = tag.getCompoundTag("acceptedLevels");

			for(int i = 1; i <= 5; i++)
			{
				acceptedLevels[i - 1] = acceptedLevelsTag.getBoolean("lvl" + i);
			}
		}

		signature = tag.getInteger("signature");
	}

	public void setAcceptedLevels(boolean[] acceptedLevels)
	{
		this.acceptedLevels = acceptedLevels;
	}

	public boolean[] getAcceptedLevels()
	{
		return acceptedLevels;
	}

	public void setSignature(int signature)
	{
		this.signature = signature;
	}

	public int getSignature()
	{
		return signature;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.ALLOWLIST, EnumModuleType.DENYLIST, EnumModuleType.DISGUISE, EnumModuleType.SMART};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ sendMessage, signalLength };
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}
}
