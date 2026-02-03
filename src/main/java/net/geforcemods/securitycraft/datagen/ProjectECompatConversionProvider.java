//package net.geforcemods.securitycraft.datagen;
//
//import moze_intel.projecte.api.data.CustomConversionBuilder;
//import moze_intel.projecte.api.data.CustomConversionProvider;
//import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SecurityCraft;
//import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Items;
//
//public class ProjectECompatConversionProvider extends CustomConversionProvider {
//	protected ProjectECompatConversionProvider(DataGenerator generator) {
//		super(generator);
//	}
//
//	@Override
//	protected void addCustomConversions() {
//		CustomConversionBuilder reinforcedBlocksConversionBuilder = createConversionBuilder(SecurityCraft.resLoc("reinforced_blocks"));
//		CustomConversionBuilder passcodeProtectedConversionBuilder = createConversionBuilder(SecurityCraft.resLoc("passcode_protected"));
//		long keyPanelEMC = 520;
//
//		SecurityCraft.collectSCContentData();
//		reinforcedBlocksConversionBuilder.comment("Conversions for vanilla blocks to reinforced blocks. The only cost is durability of the reinforcer, so they are close enough to balance out.");
//		//@formatter:off
//		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.entrySet().stream()
//			.filter(entry -> entry.getKey().asItem() != Items.AIR && entry.getValue().asItem() != Items.AIR)
//			.forEach(entry -> reinforcedBlocksConversionBuilder.conversion(entry.getValue()).ingredient(entry.getKey()).end());
//		//@formatter:on
//		passcodeProtectedConversionBuilder.comment("Passcode-protected blocks are created by rightclicking a non-passcode-protected block with a key panel, so the EMC is that block's EMC + the key panel's EMC.");
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD.get(), 1856 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_CHEST.get(), 64 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_FURNACE.get(), 8 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_SMOKER.get(), 136 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_BLAST_FURNACE.get(), 1291 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_TRAPDOOR.get(), 1024 + keyPanelEMC);
//		passcodeProtectedConversionBuilder.before(SCContent.KEYPAD_BARREL.get(), 56 + keyPanelEMC);
//	}
//}
