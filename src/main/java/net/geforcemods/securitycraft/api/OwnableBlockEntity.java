package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class OwnableBlockEntity extends BlockEntity implements IOwnable {
	private Owner owner = new Owner();

	public OwnableBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	public OwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		if (owner != null)
			owner.save(tag, needsValidation());
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		owner.load(tag);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null && !ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && this instanceof IModuleInventory inv && (!state.hasProperty(DoorBlock.HALF) || state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER))
			inv.dropAllModules();

		if (this instanceof IPasscodeProtected passcodeProtected)
			SaltData.removeSalt(passcodeProtected.getSaltKey());

		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(saveCustomOnly(lookupProvider));
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}
}
