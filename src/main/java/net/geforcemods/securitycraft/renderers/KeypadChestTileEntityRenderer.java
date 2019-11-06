package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.client.renderer.tileentity.model.LargeChestModel;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadChestTileEntityRenderer extends TileEntityRenderer<KeypadChestTileEntity>
{
	private static final ResourceLocation CHRISTMAS_DOUBLE = new ResourceLocation("securitycraft:textures/entity/chest/christmas_double.png");
	private static final ResourceLocation NORMAL_DOUBLE_INACTIVE = new ResourceLocation("securitycraft:textures/entity/chest/double_chest_unactive.png");
	private static final ResourceLocation NORMAL_DOUBLE_ACTIVE = new ResourceLocation("securitycraft:textures/entity/chest/double_chest_active.png");
	private static final ResourceLocation CHRISTMAS_SINGLE = new ResourceLocation("securitycraft:textures/entity/chest/christmas.png");
	private static final ResourceLocation NORMAL_SINGLE_INACTIVE = new ResourceLocation("securitycraft:textures/entity/chest/chest_unactive.png");
	private static final ResourceLocation NORMAL_SINGLE_ACTIVE = new ResourceLocation("securitycraft:textures/entity/chest/chest_active.png");
	private static final ChestModel SMALL_MODEL = new ChestModel();
	private static final ChestModel LARGE_MODEL = new LargeChestModel();
	private boolean isChristmas;

	public KeypadChestTileEntityRenderer()
	{
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

	@Override
	public void render(KeypadChestTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		BlockState BlockState = tileEntityIn.hasWorld() ? tileEntityIn.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = BlockState.has(ChestBlock.TYPE) ? BlockState.get(ChestBlock.TYPE) : ChestType.SINGLE;
		if (chesttype != ChestType.LEFT) {
			boolean flag = chesttype != ChestType.SINGLE;
			ChestModel modelchest = this.getChestModel(tileEntityIn, destroyStage, flag);
			if (destroyStage >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(flag ? 8.0F : 4.0F, 4.0F, 1.0F);
				GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			} else {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.translatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			float f = BlockState.get(ChestBlock.FACING).getHorizontalAngle();
			if (Math.abs(f) > 1.0E-5D) {
				GlStateManager.translatef(0.5F, 0.5F, 0.5F);
				GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			}

			this.rotateLid(tileEntityIn, partialTicks, modelchest);
			modelchest.renderAll();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (destroyStage >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}

	private ChestModel getChestModel(KeypadChestTileEntity te, int partialTicks, boolean flag) {
		ResourceLocation resourcelocation;

		if (partialTicks >= 0) {
			resourcelocation = DESTROY_STAGES[partialTicks];
		}
		else if (this.isChristmas) {
			resourcelocation = flag ? CHRISTMAS_DOUBLE : CHRISTMAS_SINGLE;
		}
		else if(te.getLidAngle(partialTicks) >= 0.9F){
			resourcelocation = flag ? NORMAL_DOUBLE_ACTIVE : NORMAL_SINGLE_ACTIVE;
		}
		else {
			resourcelocation = flag ? NORMAL_DOUBLE_INACTIVE : NORMAL_SINGLE_INACTIVE;
		}

		this.bindTexture(resourcelocation);
		return flag ? LARGE_MODEL : SMALL_MODEL;
	}

	private void rotateLid(KeypadChestTileEntity te, float partialTicks, ChestModel chestModel) {
		float f = ((IChestLid) te).getLidAngle(partialTicks);
		f = 1.0F - f;
		f = 1.0F - f * f * f;
		chestModel.getLid().rotateAngleX = -(f * ((float)Math.PI / 2F));
	}

}