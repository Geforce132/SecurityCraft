package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityRetinalScannerRenderer extends TileEntitySpecialRenderer<TileEntityRetinalScanner> {
	private static final float CORRECT_FACTOR = 1 / 550F;

	@Override
	public void render(TileEntityRetinalScanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());

		if(state.getBlock() == SCContent.retinalScanner)
		{
			EnumFacing direction = state.getValue(BlockRetinalScanner.FACING);

			if (!te.hasModule(EnumModuleType.DISGUISE))
				render((float)x, (float)y, (float)z, direction, te.getPlayerProfile(), destroyStage);
		}
	}

	public void render(float x, float y, float z, @Nullable EnumFacing facing, @Nullable GameProfile playerProfile, int destroyStage) {
		if (facing != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(this.getSkinTexture(playerProfile));
			GlStateManager.pushMatrix();

			switch (facing) {
				case NORTH:
					GlStateManager.translate(x + 0.25F, y + 1.0F / 16.0F, z);
					break;
				case SOUTH:
					GlStateManager.translate(x + 0.75F, y + 1.0F / 16.0F, z + 1.0F);
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case WEST:
					GlStateManager.translate(x, y + 1.0F / 16.0F, z + 0.75F);
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
					break;
				case EAST:
					GlStateManager.translate(x + 1.0F, y + 1.0F / 16.0F, z + 0.25F);
					GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
					break;
				default:
					break;
			}

			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(-1.0F, -1.0F, 1.0F);
			GlStateManager.enableAlpha();
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
		if (ConfigHandler.retinalScannerFace && profile != null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
			if (map.containsKey(Type.SKIN)) {
				resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
			} else {
				resourcelocation = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
			}
		}

		return resourcelocation;
	}
}