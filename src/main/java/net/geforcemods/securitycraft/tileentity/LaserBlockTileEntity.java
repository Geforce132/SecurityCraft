package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.item.ItemStack;

public class LaserBlockTileEntity extends DisguisableTileEntity {

	private BooleanOption enabledOption = new BooleanOption("enabled", true) {
		@Override
		public void toggle() {
			setValue(!getValue());

			toggleLaser(this);
		}
	};

	public LaserBlockTileEntity()
	{
		super(SCContent.teTypeLaserBlock);
	}

	private void toggleLaser(BooleanOption option) {
		if(BlockUtils.getBlock(world, pos) != SCContent.laserBlock) return;

		if(option.getValue())
			((LaserBlock) BlockUtils.getBlock(world, pos)).setLaser(world, pos);
		else
			LaserBlock.destroyAdjacentLasers(world, pos);
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableTileEntity> excludedTEs) {
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
			CustomModules module = (CustomModules) parameters[1];

			removeModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.MODULE_REMOVED, parameters, excludedTEs);
		}
	}

	@Override
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{CustomModules.HARMING, CustomModules.WHITELIST, CustomModules.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ enabledOption };
	}

}
