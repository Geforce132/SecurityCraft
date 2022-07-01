package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntityLinkable;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.util.TileEntityRenderDelegate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityLaserBlock extends TileEntityLinkable {
	private DisabledOption disabled = new DisabledOption(false) {
		@Override
		public void toggle() {
			setValue(!get());

			toggleLaser(this);
		}
	};

	private void toggleLaser(OptionBoolean option) {
		Block block = world.getBlockState(pos).getBlock();

		if (block != SCContent.laserBlock)
			return;

		if (option.get())
			((BlockLaserBlock) block).setLaser(((TileEntityLaserBlock) world.getTileEntity(pos)).getOwner(), world, pos);
		else
			BlockLaserBlock.destroyAdjacentLasers(world, pos);
	}

	@Override
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<TileEntityLinkable> excludedTEs) {
		if (action == EnumLinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];

			disabled.copy(option);
			toggleLaser((OptionBoolean) option);
		}
		else if (action == EnumLinkedAction.MODULE_INSERTED) {
			ItemStack module = (ItemStack) parameters[0];

			insertModule(module, (boolean) parameters[2]);
		}
		else if (action == EnumLinkedAction.MODULE_REMOVED) {
			EnumModuleType module = (EnumModuleType) parameters[1];

			removeModule(module, (boolean) parameters[2]);
			excludedTEs.add(this);
			createLinkedBlockAction(action, parameters, excludedTEs);
		}
		else if (action == EnumLinkedAction.OWNER_CHANGED) {
			Owner owner = (Owner) parameters[0];

			setOwner(owner.getUUID(), owner.getName());
		}

		excludedTEs.add(this);
		createLinkedBlockAction(action, parameters, excludedTEs);
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == EnumModuleType.DISGUISE)
			onInsertDisguiseModule(stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == EnumModuleType.DISGUISE)
			onRemoveDisguiseModule(stack, toggled);
	}

	private void onInsertDisguiseModule(ItemStack stack, boolean toggled) {
		if (!world.isRemote)
			SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, true, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
		else {
			IBlockState state = world.getBlockState(pos);

			TileEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

			if (state.getLightValue(world, pos) > 0)
				world.checkLight(pos);
		}
	}

	private void onRemoveDisguiseModule(ItemStack stack, boolean toggled) {
		if (!world.isRemote)
			SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, false, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
		else {
			IBlockState disguisedState = ((BlockDisguisable) blockType).getDisguisedBlockStateFromStack(null, null, stack);

			TileEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

			if (disguisedState != null && disguisedState.getLightValue(world, pos) > 0)
				world.checkLight(pos);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world.isRemote)
			TileEntityRenderDelegate.putDisguisedTeRenderer(this, getModule(EnumModuleType.DISGUISE));
	}

	@Override
	public void readOptions(NBTTagCompound tag) {
		if (tag.hasKey("enabled"))
			tag.setBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.readFromNBT(tag);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (world.isRemote)
			TileEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.HARMING, EnumModuleType.ALLOWLIST, EnumModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled
		};
	}

	public boolean isEnabled() {
		return !disabled.get();
	}
}
