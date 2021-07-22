package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.containers.KeycardReaderContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;

public class KeycardReaderTileEntity extends DisguisableTileEntity implements INamedContainerProvider {

	private boolean[] acceptedLevels = {true, false, false, false, false};
	private int signature = 0;
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max

	public KeycardReaderTileEntity()
	{
		super(SCContent.teTypeKeycardReader);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag){
		super.save(tag);

		CompoundNBT acceptedLevelsTag = new CompoundNBT();

		for(int i = 1; i <= 5; i++)
		{
			acceptedLevelsTag.putBoolean("lvl" + i, acceptedLevels[i - 1]);
		}

		tag.put("acceptedLevels", acceptedLevelsTag);
		tag.putInt("signature", signature);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag){
		super.load(state, tag);

		//carry over old data
		if(tag.contains("passLV"))
		{
			boolean oldRequiresExactKeycard = false;
			int oldPassLV = tag.getInt("passLV") - 1; //old data was 1-indexed, new one is 0-indexed

			if(tag.contains("requiresExactKeycard"))
				oldRequiresExactKeycard = tag.getBoolean("requiresExactKeycard");

			for(int i = 0; i < 5; i++)
			{
				acceptedLevels[i] = oldRequiresExactKeycard ? i == oldPassLV : i >= oldPassLV;
			}
		}

		//don't try to load this data if it doesn't exist, otherwise everything will be "false"
		if(tag.contains("acceptedLevels", NBT.TAG_COMPOUND))
		{
			CompoundNBT acceptedLevelsTag = tag.getCompound("acceptedLevels");

			for(int i = 1; i <= 5; i++)
			{
				acceptedLevels[i - 1] = acceptedLevelsTag.getBoolean("lvl" + i);
			}
		}

		signature = tag.getInt("signature");
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART};
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

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new KeycardReaderContainer(windowId, inv, level, worldPosition);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.KEYCARD_READER.get().getDescriptionId());
	}
}
