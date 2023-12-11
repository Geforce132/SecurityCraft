package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NamedBlockEntity extends OwnableBlockEntity implements INameSetter {
	private ITextComponent customName;

	public NamedBlockEntity() {
		this(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}

	public NamedBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (customName != null)
			tag.putString("customName", customName.getString());

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("customName")) {
			String name = tag.getString("customName");

			if (!name.equals("name"))
				customName = new StringTextComponent(name);
		}
	}

	@Override
	public ITextComponent getName() {
		return hasCustomName() ? getCustomName() : getDefaultName();
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
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public ITextComponent getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getDescriptionId());
	}
}
