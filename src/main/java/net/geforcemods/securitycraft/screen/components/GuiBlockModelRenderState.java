package net.minecraft.client.gui.render.state.pip;

import org.joml.Quaternionf;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("rawtypes")
public record GuiBlockModelRenderState(BlockState blockState, BlockEntity be, BlockEntityRenderer beRenderer, BlockAndTintGetter blockAndTintGetter, Quaternionf rotation, int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, ScreenRectangle bounds) implements PictureInPictureRenderState {
	public GuiBlockModelRenderState(BlockState blockState, BlockEntity be, BlockEntityRenderer beRenderer, BlockAndTintGetter blockAndTintGetter, Quaternionf rotation, int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea) {
		this(blockState, be, beRenderer, blockAndTintGetter, rotation, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
	}
}
