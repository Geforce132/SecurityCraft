package net.geforcemods.securitycraft.datagen;

import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCModelTemplates;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ReinforcedWoodProvider {
	private final TextureMapping mapping;
	private final BiConsumer<ResourceLocation, ModelInstance> modelOutput = BlockModelAndStateGenerator.modelOutput;

	private ReinforcedWoodProvider(TextureMapping mapping) {
		this.mapping = mapping;
	}

	public static ReinforcedWoodProvider of(Block reinforcedLog, LogGenerator logGenerator) {
		Block vanillaLog = IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.get(reinforcedLog);

		if (vanillaLog == null)
			throw new IllegalStateException("Couldn't find vanilla block for " + Utils.getRegistryName(reinforcedLog));

		ReinforcedWoodProvider provider = new ReinforcedWoodProvider(TextureMapping.logColumn(vanillaLog));

		logGenerator.generate(provider, reinforcedLog);
		return provider;
	}

	public ReinforcedWoodProvider wood(Block woodBlock) {
		TextureMapping textureMapping = mapping.copyAndUpdate(TextureSlot.END, mapping.get(TextureSlot.SIDE));
		ResourceLocation modelLocation = SCModelTemplates.REINFORCED_CUBE_COLUMN.create(woodBlock, textureMapping, modelOutput);

		BlockModelAndStateGenerator.generate(woodBlock, BlockModelGenerators.createAxisAlignedPillarBlock(woodBlock, modelLocation));
		BlockModelAndStateGenerator.registerReinforcedItemModel(woodBlock);
		return this;
	}

	public ReinforcedWoodProvider log(Block logBlock) {
		ResourceLocation modelLocation = SCModelTemplates.REINFORCED_CUBE_COLUMN.create(logBlock, mapping, modelOutput);

		BlockModelAndStateGenerator.generate(logBlock, BlockModelGenerators.createAxisAlignedPillarBlock(logBlock, modelLocation));
		BlockModelAndStateGenerator.registerReinforcedItemModel(logBlock);
		return this;
	}

	public ReinforcedWoodProvider logWithHorizontal(Block logBlock) {
		ResourceLocation modelLocation = SCModelTemplates.REINFORCED_CUBE_COLUMN.create(logBlock, mapping, modelOutput);
		ResourceLocation horizontalModelLocation = SCModelTemplates.REINFORCED_CUBE_COLUMN_HORIZONTAL.create(logBlock, mapping, modelOutput);

		BlockModelAndStateGenerator.generate(logBlock, BlockModelGenerators.createRotatedPillarWithHorizontalVariant(logBlock, modelLocation, horizontalModelLocation));
		BlockModelAndStateGenerator.registerReinforcedItemModel(logBlock);
		return this;
	}

	public ReinforcedWoodProvider logUVLocked(Block logBlock) {
		BlockModelAndStateGenerator.generate(logBlock, BlockModelAndStateGenerator.createReinforcedPillarBlockUVLocked(logBlock, mapping, modelOutput));
		BlockModelAndStateGenerator.registerReinforcedItemModel(logBlock);
		return this;
	}

	public enum LogGenerator {
		DEFAULT(ReinforcedWoodProvider::log),
		HORIZONTAL(ReinforcedWoodProvider::logWithHorizontal),
		UV_LOCKED(ReinforcedWoodProvider::logUVLocked);

		private final BiConsumer<ReinforcedWoodProvider, Block> generator;

		LogGenerator(BiConsumer<ReinforcedWoodProvider, Block> generator) {
			this.generator = generator;
		}

		public void generate(ReinforcedWoodProvider provider, Block log) {
			generator.accept(provider, log);
		}
	}
}