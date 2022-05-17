package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.util.TileEntityRenderDelegate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class TileEntityDisguisable extends CustomizableSCTE {
	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == EnumModuleType.DISGUISE) {
			if (!world.isRemote)
				SecurityCraft.network.sendToAll(new RefreshDiguisedModel(pos, true, stack, toggled));
			else {
				IBlockState state = world.getBlockState(pos);

				TileEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

				if (state.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == EnumModuleType.DISGUISE) {
			if (!world.isRemote)
				SecurityCraft.network.sendToAll(new RefreshDiguisedModel(pos, false, stack, toggled));
			else {
				IBlockState disguisedState = ((BlockDisguisable) blockType).getDisguisedBlockStateFromStack(null, null, stack);

				TileEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

				if (disguisedState != null && disguisedState.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world.isRemote)
			TileEntityRenderDelegate.putDisguisedTeRenderer(this, getModule(EnumModuleType.DISGUISE));
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
				EnumModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
