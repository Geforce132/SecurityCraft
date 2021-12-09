package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NamedTileEntity extends OwnableTileEntity implements INameSetter {
	private ITextComponent customName = StringTextComponent.EMPTY;

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
		tag.putString("customName", customName.getString());
		return tag;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if (tag.contains("customName"))
			customName = new StringTextComponent(tag.getString("customName"));
	}

	@Override
	public ITextComponent getName()
	{
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		ITextComponent name = getCustomName();

		return name != null && !StringTextComponent.EMPTY.equals(name) && !getDefaultName().equals(name);
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
