package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayCaseRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity> {
	private final ResourceLocation texture = SecurityCraft.resLoc("textures/entity/display_case.png");
	private final ResourceLocation glowTexture = SecurityCraft.resLoc("textures/entity/glow_display_case.png");
	private final ModelPart main;
	private final ModelPart door;
	private final boolean glowing;

	public DisplayCaseRenderer(BlockEntityRendererProvider.Context ctx, boolean glowing) {
		ModelPart model = ctx.bakeLayer(glowing ? ClientHandler.GLOW_DISPLAY_CASE_LOCATION : ClientHandler.DISPLAY_CASE_LOCATION);

		main = model.getChild("main");
		door = model.getChild("door");
		this.glowing = glowing;
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		//@formatter:off
		partDefinition.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-6.0F, -3.0F, -8.0F, 12.0F, 1.0F, 5.0F)
				.texOffs(36, 0).addBox(-6.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F)
				.texOffs(13, 4).addBox(-5.0F, -13.0F, -8.0F, 10.0F, 10.0F, 1.0F)
				.texOffs(0, 0).addBox(5.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F)
				.texOffs(0, 16).addBox(-6.0F, -14.0F, -8.0F, 12.0F, 1.0F, 5.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
		partDefinition.addOrReplaceChild("door", CubeListBuilder.create()
				.texOffs(5, 31).addBox(1.0F, -5.0F, 0.0F, 10.0F, 10.0F, 1.0F)
				.texOffs(0, 31).addBox(11.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F)
				.texOffs(28, 31).addBox(0.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F)
				.texOffs(0, 43).addBox(0.0F, -6.0F, 0.0F, 12.0F, 1.0F, 1.0F)
				.texOffs(0, 46).addBox(0.0F, 5.0F, 0.0F, 12.0F, 1.0F, 1.0F)
				.texOffs(27, 43).addBox(11.0F, -1.5F, 1.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(-6.0F, 16.0F, -3.0F));
		//@formatter:on

		return LayerDefinition.create(meshDefinition, 48, 48);
	}

	@Override
	public void render(DisplayCaseBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		VertexConsumer consumer;
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(DisplayCaseBlock.FACING);
		float rotation = facing.toYRot();
		ItemStack displayedStack = be.getDisplayedStack();
		int light = glowing ? 0xF000D2 : packedLight;

		door.yRot = -(be.getOpenness(partialTick) * ((float) Math.PI / 2.0F));
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);

		if (!displayedStack.isEmpty()) {
			double insertionAmount = 0.40625D;

			pose.pushPose();

			switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
				case CEILING:
					pose.translate(0.0D, insertionAmount, 0.0D);
					pose.mulPose(Axis.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
					break;
				case FLOOR:
					pose.translate(0.0D, -insertionAmount, 0.0D);
					pose.mulPose(Axis.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Axis.XP.rotationDegrees(90.0F));
					break;
				case WALL:
					pose.mulPose(Axis.YP.rotationDegrees(180.0F));
					pose.mulPose(Axis.YP.rotationDegrees(-rotation));
					pose.translate(0.0D, 0.0D, insertionAmount);
					break;
			}

			pose.scale(0.5F, 0.5F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(displayedStack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffer, be.getLevel(), 0);
			pose.popPose();
		}

		pose.mulPose(Axis.YP.rotationDegrees(-rotation));

		switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
			case CEILING:
				pose.translate(0.0D, 0.0D, 1.0D);
				pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
				break;
			case FLOOR:
				pose.translate(0.0D, 0.0D, -1.0D);
				pose.mulPose(Axis.XP.rotationDegrees(90.0F));
				break;
			case WALL:
				pose.translate(0.0D, 1.0D, 0.0D);
				pose.mulPose(Axis.XP.rotationDegrees(180.0F));
				break;
		}

		consumer = buffer.getBuffer(RenderType.entityCutout(glowing ? glowTexture : texture));
		pose.scale(-1.0F, 1.0F, -1.0F);
		main.render(pose, consumer, light, packedOverlay);
		door.render(pose, consumer, light, packedOverlay);
		pose.popPose();
	}
}