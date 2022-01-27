package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NamedTileEntity extends OwnableTileEntity implements INameSetter {
	private ITextComponent customName = null;

	public NamedTileEntity() {
		this(SCContent.teTypeAbstract);
	}

	public NamedTileEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putString("customName", customName == null ? "" : customName.getColoredString());
		return tag;
	}

	@Override
	public void load(CompoundNBT tag) {
		super.load(tag);

		if (tag.contains("customName")) {
			String name = tag.getString("customName");

			if (!name.equals("name"))
				customName = new StringTextComponent(name);
			else
				customName = new StringTextComponent("");
		}
		else {
			customName = new StringTextComponent("");
		}
	}

	@Override
	public ITextComponent getName() {
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		ITextComponent name = getCustomName();

		return name != null && !name.getColoredString().isEmpty() && !getDefaultName().equals(name);
	}

	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public ITextComponent getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getDescriptionId());
	}
}
