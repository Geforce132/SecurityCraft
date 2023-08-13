package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			if (!world.isRemote)
				SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, true, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
			else {
				IBlockState state = world.getBlockState(pos);

				BlockEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

				if (state.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			if (!world.isRemote)
				SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, false, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
			else {
				IBlockState disguisedState = ((DisguisableBlock) blockType).getDisguisedBlockStateFromStack(null, null, stack);

				BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

				if (disguisedState != null && disguisedState.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world.isRemote)
			BlockEntityRenderDelegate.putDisguisedTeRenderer(this, getModule(ModuleType.DISGUISE));
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (world.isRemote)
			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}
}
