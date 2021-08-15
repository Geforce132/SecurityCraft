package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class LaserBlockBlockEntity extends DisguisableBlockEntity {

	private BooleanOption enabledOption = new BooleanOption("enabled", true) {
		@Override
		public void toggle() {
			setValue(!get());

			toggleLaser(this);
		}
	};

	public LaserBlockBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeLaserBlock, pos, state);
	}

	private void toggleLaser(BooleanOption option) {
		if(option.get())
			((LaserBlock)getBlockState().getBlock()).setLaser(level, worldPosition);
		else
			LaserBlock.destroyAdjacentLasers(level, worldPosition);
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableBlockEntity> excludedTEs) {
		if(action == LinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];
			enabledOption.copy(option);
			toggleLaser((BooleanOption) option);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.OPTION_CHANGED, new Option[]{ option }, excludedTEs);
		}
		else if(action == LinkedAction.MODULE_INSERTED) {
			ItemStack module = (ItemStack) parameters[0];

			insertModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.MODULE_INSERTED, parameters, excludedTEs);
		}
		else if(action == LinkedAction.MODULE_REMOVED) {
			ModuleType module = (ModuleType) parameters[1];

			removeModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.MODULE_REMOVED, parameters, excludedTEs);
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.HARMING, ModuleType.ALLOWLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ enabledOption };
	}

	public boolean isEnabled()
	{
		return enabledOption.get();
	}
}
