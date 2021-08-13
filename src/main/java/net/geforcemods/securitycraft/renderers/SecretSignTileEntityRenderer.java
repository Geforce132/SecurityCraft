package net.geforcemods.securitycraft.renderers;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer.SignModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecretSignTileEntityRenderer implements BlockEntityRenderer<SecretSignBlockEntity>
{
	public static final int MAX_LINE_WIDTH = 90;
	private static final int LINE_HEIGHT = 10;
	private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
	private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
	private final Map<WoodType,SignModel> signModels;
	private final Font font;

	public SecretSignTileEntityRenderer(BlockEntityRendererProvider.Context ctx)
	{
		signModels = WoodType.values().collect(ImmutableMap.toImmutableMap(woodType -> woodType, woodType -> SignRenderer.createSignModel(ctx.getModelSet(), woodType)));
		font = ctx.getFont();
	}

	@Override
	public void render(SecretSignBlockEntity te, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
	{
		BlockState state = te.getBlockState();
		WoodType woodtype = SignRenderer.getWoodType(state.getBlock());
		SignModel model = signModels.get(woodtype);
		Material material = Sheets.getSignMaterial(woodtype);
		VertexConsumer builder;

		pose.pushPose();

		if(state.getBlock() instanceof SecretStandingSignBlock)
		{
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(Vector3f.YP.rotationDegrees(-(state.getValue(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.stick.visible = true;
		}
		else
		{
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(SecretWallSignBlock.FACING).toYRot()));
			pose.translate(0.0D, -0.3125D, -0.4375D);
			model.stick.visible = false;
		}

		pose.pushPose();
		pose.scale(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.buffer(buffer, model::renderType);
		model.root.render(pose, builder, combinedLight, combinedOverlay);
		pose.popPose();
		pose.translate(0.0D, 0.33333334F, 0.046666667F);
		pose.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.isPlayerAllowedToSeeText(Minecraft.getInstance().player))
		{
			int textColor;
			boolean drawOutline;
			int darkColor = getDarkColor(te);
			int packedLightCoords;
			FormattedCharSequence[] text = te.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), line -> {
				List<FormattedCharSequence> list = font.split(line, 90);
				return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
			});

			if(te.hasGlowingText())
			{
				textColor = te.getColor().getTextColor();
				drawOutline = isOutlineVisible(te, textColor);
				packedLightCoords = 15728880;
			}
			else
			{
				textColor = darkColor;
				drawOutline = false;
				packedLightCoords = combinedLight;
			}

			for(int lineIndex = 0; lineIndex < 4; ++lineIndex)
			{
				FormattedCharSequence line = text[lineIndex];

				float xPos = -this.font.width(line) / 2;

				if(drawOutline)
					font.drawInBatch8xOutline(line, xPos, lineIndex * LINE_HEIGHT - 20, textColor, darkColor, pose.last().pose(), buffer, packedLightCoords);
				else
					font.drawInBatch(line, xPos, lineIndex * LINE_HEIGHT - 20, textColor, false, pose.last().pose(), buffer, false, 0, packedLightCoords);

			}
		}

		pose.popPose();
	}

	private static boolean isOutlineVisible(SecretSignBlockEntity te, int textColor)
	{
		if(textColor == DyeColor.BLACK.getTextColor())
			return true;
		else
		{
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;

			if(player != null && mc.options.getCameraType().isFirstPerson() && player.isScoping())
				return true;
			else
			{
				Entity entity = mc.getCameraEntity();

				return entity != null && entity.distanceToSqr(Vec3.atCenterOf(te.getBlockPos())) < OUTLINE_RENDER_DISTANCE;
			}
		}
	}

	private static int getDarkColor(SecretSignBlockEntity te)
	{
		int textColor = te.getColor().getTextColor();
		int r = (int)(NativeImage.getR(textColor) * 0.4D);
		int g = (int)(NativeImage.getG(textColor) * 0.4D);
		int b = (int)(NativeImage.getB(textColor) * 0.4D);

		return textColor == DyeColor.BLACK.getTextColor() && te.hasGlowingText() ? BLACK_TEXT_OUTLINE_COLOR : NativeImage.combine(0, b, g, r);
	}
}