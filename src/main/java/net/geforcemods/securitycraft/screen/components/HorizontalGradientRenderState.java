package net.geforcemods.securitycraft.screen.components;

import javax.annotation.Nullable;

import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record HorizontalGradientRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int left, int top, int right, int bottom, int fromColor, int toColor, ScreenRectangle scissorArea, ScreenRectangle bounds) implements GuiElementRenderState {
	public HorizontalGradientRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int left, int top, int right, int bottom, int fromColor, int toColor, ScreenRectangle scissorArea) {
		this(pipeline, textureSetup, pose, left, top, right, bottom, fromColor, toColor, scissorArea, getBounds(left, top, right, bottom, pose, scissorArea));
	}

	@Override
	public void buildVertices(VertexConsumer builder, float zLevel) {
		builder.addVertexWith2DPose(pose, right, top, zLevel).setColor(toColor);
		builder.addVertexWith2DPose(pose, left, top, zLevel).setColor(fromColor);
		builder.addVertexWith2DPose(pose, left, bottom, zLevel).setColor(fromColor);
		builder.addVertexWith2DPose(pose, right, bottom, zLevel).setColor(toColor);
	}

	@Nullable
	private static ScreenRectangle getBounds(int p_421840_, int p_422646_, int p_422274_, int p_421750_, Matrix3x2f p_421574_, ScreenRectangle scissorArea) {
		ScreenRectangle rectangle = new ScreenRectangle(p_421840_, p_422646_, p_422274_ - p_421840_, p_421750_ - p_422646_).transformMaxBounds(p_421574_);

		return scissorArea != null ? scissorArea.intersection(rectangle) : rectangle;
	}
}
