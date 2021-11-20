package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NamedBlockEntity extends OwnableBlockEntity implements INameSetter {
	private Component customName = TextComponent.EMPTY;

	public NamedBlockEntity(BlockPos pos, BlockState state)
	{
		this(SCContent.beTypeAbstract, pos, state);
	}

	public NamedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);
		tag.putString("customName", customName.getString());
		return tag;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		if (tag.contains("customName"))
			customName = new TextComponent(tag.getString("customName"));
	}

	@Override
	public Component getName()
	{
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		Component name = getCustomName();

		return name != null && !TextComponent.EMPTY.equals(name) && !getDefaultName().equals(name);
	}

	@Override
	public Component getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(Component customName) {
		this.customName = customName;
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public Component getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getDescriptionId());
	}
}
