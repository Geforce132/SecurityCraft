package net.geforcemods.securitycraft.compat.projecte;

import com.google.common.collect.ImmutableMap;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ProjectECompat {
	private ProjectECompat() {}

	public static void registerConversions() {
		IConversionProxy reinforcedBlocksConversionProxy = ProjectEAPI.getConversionProxy();
		IEMCProxy passcodeProtectedEMCProxy = ProjectEAPI.getEMCProxy();
		long keyPanelEMC = 520;

		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.forEach((vanillaBlock, securityCraftBlock) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock) securityCraftBlock;
			NonNullList<ItemStack> subBlocks = NonNullList.create();

			vanillaBlock.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
			subBlocks.forEach(vanillaStack -> {
				if (!vanillaStack.isEmpty()) {
					ItemStack reinforcedStack = reinforcedBlock.convertToReinforcedStack(vanillaStack, vanillaBlock);

					if (!reinforcedStack.isEmpty())
						reinforcedBlocksConversionProxy.addConversion(1, reinforcedStack, ImmutableMap.<Object, Integer>builder().put(vanillaStack, 1).build());
				}
			});
		});
		passcodeProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypad), 1856 + keyPanelEMC);
		passcodeProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypadChest), 64 + keyPanelEMC);
		passcodeProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypadFurnace), 8 + keyPanelEMC);
		passcodeProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypadTrapdoor), 1024 + keyPanelEMC);
	}
}
