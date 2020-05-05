package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.item.ItemStack;

public class TileEntityLaserBlock extends TileEntityDisguisable {

	private OptionBoolean enabledOption = new OptionBoolean("enabled", true) {
		@Override
		public void toggle() {
			setValue(!getValue());

			toggleLaser(this);
		}
	};

	private void toggleLaser(OptionBoolean option) {
		if(BlockUtils.getBlock(world, pos) != SCContent.laserBlock) return;

		if(option.getValue())
			((BlockLaserBlock) BlockUtils.getBlock(world, pos)).setLaser(((TileEntityLaserBlock)world.getTileEntity(pos)).getOwner(), world, pos);
		else
			BlockLaserBlock.destroyAdjacentLasers(world, pos);
	}

	@Override
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs) {
		if(action == EnumLinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];
			enabledOption.copy(option);
			toggleLaser((OptionBoolean) option);

			excludedTEs.add(this);
			createLinkedBlockAction(EnumLinkedAction.OPTION_CHANGED, new Option[]{ option }, excludedTEs);
		}
		else if(action == EnumLinkedAction.MODULE_INSERTED) {
			ItemStack module = (ItemStack) parameters[0];

			insertModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(EnumLinkedAction.MODULE_INSERTED, parameters, excludedTEs);
		}
		else if(action == EnumLinkedAction.MODULE_REMOVED) {
			EnumModuleType module = (EnumModuleType) parameters[1];

			removeModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, parameters, excludedTEs);
		}
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.HARMING, EnumModuleType.WHITELIST, EnumModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ enabledOption };
	}

}
