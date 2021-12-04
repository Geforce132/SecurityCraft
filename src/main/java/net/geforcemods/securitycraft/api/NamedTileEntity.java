package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NamedTileEntity extends OwnableTileEntity implements INameSetter {
	private ITextComponent customName = null;

	public NamedTileEntity()
	{
		this(SCContent.teTypeAbstract);
	}

	public NamedTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		tag.putString("customName", customName == null ? "" : customName.getFormattedText());
		return tag;
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		if (tag.contains("customName"))
			customName = new StringTextComponent(tag.getString("customName"));
		else
			customName = new StringTextComponent("");
	}

	@Override
	public ITextComponent getName()
	{
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		ITextComponent name = getCustomName();

		return name != null && !name.getFormattedText().isEmpty() && !getDefaultName().equals(name);
	}

	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}

	public ITextComponent getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getTranslationKey());
	}
}
