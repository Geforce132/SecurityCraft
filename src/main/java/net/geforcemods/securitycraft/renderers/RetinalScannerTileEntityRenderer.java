package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RetinalScannerTileEntityRenderer extends TileEntityRenderer<RetinalScannerTileEntity> {
	private static final float CORRECT_FACTOR = 1 / 550F;

	@Override
	public void render(RetinalScannerTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		Direction direction = tileEntityIn.getBlockState().get(RetinalScannerBlock.FACING);
		if (!tileEntityIn.hasModule(ModuleType.DISGUISE))
			this.render((float)x, (float)y, (float)z, direction, tileEntityIn.getPlayerProfile(), destroyStage);
	}

	public void render(float x, float y, float z, @Nullable Direction facing, @Nullable GameProfile playerProfile, int destroyStage) {
		if (facing != null) {
			this.bindTexture(this.getSkinTexture(playerProfile));
			GlStateManager.pushMatrix();

			switch (facing) {
				case NORTH:
					GlStateManager.translatef(x + 0.25F, y + 1.0F / 16.0F, z);
					break;
				case SOUTH:
					GlStateManager.translatef(x + 0.75F, y + 1.0F / 16.0F, z + 1.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case WEST:
					GlStateManager.translatef(x, y + 1.0F / 16.0F, z + 0.75F);
					GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
					break;
				case EAST:
					GlStateManager.translatef(x + 1.0F, y + 1.0F / 16.0F, z + 0.25F);
					GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
					break;
				default:
					break;
			}

			GlStateManager.enableRescaleNormal();
			GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
			GlStateManager.enableAlphaTest();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();

			// face
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0 + CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.125, 0.25).endVertex();
			bufferbuilder.pos(0 + CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.125, 0.125).endVertex();
			bufferbuilder.pos(-0.5 - CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.25, 0.125).endVertex();
			bufferbuilder.pos(-0.5 - CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.25, 0.25).endVertex();
			tessellator.draw();

			// helmet
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0 + CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.625, 0.25).endVertex();
			bufferbuilder.pos(0 + CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.625, 0.125).endVertex();
			bufferbuilder.pos(-0.5 - CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.75, 0.125).endVertex();
			bufferbuilder.pos(-0.5 - CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.75, 0.25).endVertex();
			tessellator.draw();

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private ResourceLocation getSkinTexture(@Nullable GameProfile profile) {
		ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
		if (ConfigHandler.CONFIG.retinalScannerFace.get() && profile != null) {
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
			if (map.containsKey(Type.SKIN)) {
				resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
			} else {
				resourcelocation = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(profile));
			}
		}

		return resourcelocation;
	}
}