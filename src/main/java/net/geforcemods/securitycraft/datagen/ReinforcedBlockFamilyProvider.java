package net.geforcemods.securitycraft.datagen;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.datagen.DataGenConstants.SCModelTemplates;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCTexturedModels;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.BlockModelGenerators.BlockFamilyProvider;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ReinforcedBlockFamilyProvider extends BlockFamilyProvider {
	private final TextureMapping mapping;
	private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();
	private ReinforcedBlockFamily reinforcedFamily;
	private ResourceLocation fullBlock;
	private final Set<Block> skipGeneratingModelsFor = new HashSet<>();
	private final BlockModelGenerators blockModelGenerators;
	private final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

	public ReinforcedBlockFamilyProvider(TextureMapping mapping) {
		BlockModelAndStateGenerator.blockModelGenerators.super(mapping);
		this.mapping = mapping;
		blockModelGenerators = BlockModelAndStateGenerator.blockModelGenerators;
		modelOutput = BlockModelAndStateGenerator.modelOutput;
	}

	@Override
	public ReinforcedBlockFamilyProvider fullBlock(Block block, ModelTemplate modelTemplate) {
		fullBlock = modelTemplate.create(block, mapping, modelOutput);

		if (blockModelGenerators.fullBlockModelCustomGenerators.containsKey(block))
			BlockModelAndStateGenerator.generate(block, blockModelGenerators.fullBlockModelCustomGenerators.get(block).create(block, fullBlock, mapping, modelOutput));
		else
			BlockModelAndStateGenerator.generate(block, BlockModelGenerators.createSimpleBlock(block, fullBlock));

		BlockModelAndStateGenerator.registerReinforcedItemModel(block);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider donateModelTo(Block sourceBlock, Block block) {
		ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(sourceBlock);

		BlockModelAndStateGenerator.generate(block, BlockModelGenerators.createSimpleBlock(block, modelLocation));
		blockModelGenerators.itemModelOutput.copy(sourceBlock.asItem(), block.asItem());
		skipGeneratingModelsFor.add(block);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider button(Block buttonBlock) {
		BlockModelAndStateGenerator.createReinforcedButton(buttonBlock, mapping);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider wall(Block wallBlock) {
		BlockModelAndStateGenerator.createReinforcedWall(wallBlock, mapping);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider customFence(Block fenceBlock) {
		BlockModelAndStateGenerator.createReinforcedCustomFence(fenceBlock, TextureMapping.customParticle(fenceBlock));
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider fence(Block fenceBlock) {
		BlockModelAndStateGenerator.createReinforcedFence(fenceBlock, mapping);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider customFenceGate(Block customFenceGateBlock) {
		BlockModelAndStateGenerator.createReinforcedCustomFenceGate(customFenceGateBlock, TextureMapping.customParticle(reinforcedFamily.family().get(BlockFamily.Variant.CUSTOM_FENCE_GATE)));
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider fenceGate(Block fenceGateBlock) {
		BlockModelAndStateGenerator.createReinforcedFenceGate(fenceGateBlock, mapping);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider pressurePlate(Block pressurePlateBlock) {
		BlockModelAndStateGenerator.createReinforcedPressurePlate(pressurePlateBlock, mapping);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider sign(Block signBlock) {
		return this; //There are no reinforced signs
	}

	@Override
	public ReinforcedBlockFamilyProvider slab(Block slabBlock) {
		if (fullBlock == null)
			throw new IllegalStateException("Full block not generated yet");
		else {
			ResourceLocation bottomModel = getOrCreateModel(SCModelTemplates.REINFORCED_SLAB_BOTTOM, slabBlock);
			ResourceLocation topModel = getOrCreateModel(SCModelTemplates.REINFORCED_SLAB_TOP, slabBlock);

			BlockModelAndStateGenerator.generate(slabBlock, BlockModelGenerators.createSlab(slabBlock, bottomModel, topModel, fullBlock));
			BlockModelAndStateGenerator.registerReinforcedItemModel(slabBlock, bottomModel);
			return this;
		}
	}

	@Override
	public ReinforcedBlockFamilyProvider stairs(Block stairsBlock) {
		ResourceLocation innerModel = getOrCreateModel(SCModelTemplates.REINFORCED_STAIRS_INNER, stairsBlock);
		ResourceLocation straightModel = getOrCreateModel(SCModelTemplates.REINFORCED_STAIRS_STRAIGHT, stairsBlock);
		ResourceLocation outerModel = getOrCreateModel(SCModelTemplates.REINFORCED_STAIRS_OUTER, stairsBlock);

		BlockModelAndStateGenerator.generate(stairsBlock, BlockModelGenerators.createStairs(stairsBlock, innerModel, straightModel, outerModel));
		BlockModelAndStateGenerator.registerReinforcedItemModel(stairsBlock, straightModel);
		return this;
	}

	@Override
	public ReinforcedBlockFamilyProvider fullBlockVariant(Block block) {
		TexturedModel texturedModel = blockModelGenerators.texturedModels.getOrDefault(block, SCTexturedModels.REINFORCED_CUBE.get(block));
		ResourceLocation modelLocation = texturedModel.create(block, modelOutput);

		BlockModelAndStateGenerator.generate(block, BlockModelGenerators.createSimpleBlock(block, modelLocation));
		BlockModelAndStateGenerator.registerReinforcedItemModel(block);
		return this;
	}

	@Override
	public BlockFamilyProvider door(Block doorBlock) {
		return this; //There are no reinforced doors
	}

	@Override
	public void trapdoor(Block trapdoorBlock) {
		//There are no reinforced trapdoors
	}

	@Override
	public ResourceLocation getOrCreateModel(ModelTemplate modelTemplate, Block block) {
		return models.computeIfAbsent(modelTemplate, mt -> mt.create(block, mapping, modelOutput));
	}

	@Override
	public ReinforcedBlockFamilyProvider generateFor(BlockFamily family) {
		reinforcedFamily = new ReinforcedBlockFamily(family);
		reinforcedFamily.getVariants().forEach((variant, block) -> {
			if (!skipGeneratingModelsFor.contains(block)) {
				BiConsumer<BlockFamilyProvider, Block> shapeConsumer = BlockModelGenerators.SHAPE_CONSUMERS.get(variant);

				if (shapeConsumer != null)
					shapeConsumer.accept(this, block);
			}
		});
		return this;
	}
}
