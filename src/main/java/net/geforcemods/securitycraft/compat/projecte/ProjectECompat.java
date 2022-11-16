package net.geforcemods.securitycraft.compat.projecte;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ProjectECompat {
	public static void registerConversions() {
		IConversionProxy reinforcedBlocksConversionProxy = ProjectEAPI.getConversionProxy();
		IEMCProxy passwordProtectedEMCProxy = ProjectEAPI.getEMCProxy();
		long keyPanelEMC = 520;

		IReinforcedBlock.BLOCKS.forEach(block -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock) block;
			List<Block> vanillaBlocks = reinforcedBlock.getVanillaBlocks();

			for (Block vanillaBlock : vanillaBlocks) {
				if (vanillaBlocks.size() == reinforcedBlock.getAmount()) {
					int meta = vanillaBlocks.indexOf(vanillaBlock);
					ItemStack vanillaStack = new ItemStack(vanillaBlock, 1, 0);
					ItemStack reinforcedStack = new ItemStack(block, 1, meta);

					if (!vanillaStack.isEmpty() && !reinforcedStack.isEmpty())
						reinforcedBlocksConversionProxy.addConversion(1, reinforcedStack, ImmutableMap.<Object, Integer>builder().put(vanillaStack, 1).build());
				}
				else {
					for (int meta = 0; meta < reinforcedBlock.getAmount(); meta++) {
						ItemStack vanillaStack = new ItemStack(vanillaBlock, 1, meta);
						ItemStack reinforcedStack = new ItemStack(block, 1, meta);

						if (!vanillaStack.isEmpty() && !reinforcedStack.isEmpty())
							reinforcedBlocksConversionProxy.addConversion(1, reinforcedStack, ImmutableMap.<Object, Integer>builder().put(vanillaStack, 1).build());
					}
				}
			}
		});
		passwordProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypad), 1856 + keyPanelEMC);
		passwordProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypadChest), 64 + keyPanelEMC);
		passwordProtectedEMCProxy.registerCustomEMC(new ItemStack(SCContent.keypadFurnace), 8 + keyPanelEMC);
	}
}
