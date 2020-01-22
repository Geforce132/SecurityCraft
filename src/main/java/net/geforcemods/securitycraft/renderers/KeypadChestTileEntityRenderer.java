package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadChestTileEntityRenderer extends TileEntityRenderer<KeypadChestTileEntity> {
	private static final Material ACTIVE = createMaterial("active");
	private static final Material INACTIVE = createMaterial("inactive");
	private static final Material LEFT_ACTIVE = createMaterial("left_active");
	private static final Material LEFT_INACTIVE = createMaterial("left_inactive");
	private static final Material RIGHT_ACTIVE = createMaterial("right_active");
	private static final Material RIGHT_INACTIVE = createMaterial("right_inactive");
	private static final Material CHRISTMAS = createMaterial("christmas");
	private static final Material CHRISTMAS_LEFT = createMaterial("christmas_left");
	private static final Material CHRISTMAS_RIGHT = createMaterial("christmas_right");
	private final ModelRenderer field_228862_a_;
	private final ModelRenderer field_228863_c_;
	private final ModelRenderer field_228864_d_;
	private final ModelRenderer field_228865_e_;
	private final ModelRenderer field_228866_f_;
	private final ModelRenderer field_228867_g_;
	private final ModelRenderer field_228868_h_;
	private final ModelRenderer field_228869_i_;
	private final ModelRenderer field_228870_j_;
	private boolean isChristmas;

	public KeypadChestTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);

		Calendar calendar = Calendar.getInstance();

		if(calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;

		field_228863_c_ = new ModelRenderer(64, 64, 0, 19);
		field_228863_c_.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		field_228862_a_ = new ModelRenderer(64, 64, 0, 0);
		field_228862_a_.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		field_228862_a_.rotationPointY = 9.0F;
		field_228862_a_.rotationPointZ = 1.0F;
		field_228864_d_ = new ModelRenderer(64, 64, 0, 0);
		field_228864_d_.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		field_228864_d_.rotationPointY = 8.0F;
		field_228866_f_ = new ModelRenderer(64, 64, 0, 19);
		field_228866_f_.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		field_228865_e_ = new ModelRenderer(64, 64, 0, 0);
		field_228865_e_.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		field_228865_e_.rotationPointY = 9.0F;
		field_228865_e_.rotationPointZ = 1.0F;
		field_228867_g_ = new ModelRenderer(64, 64, 0, 0);
		field_228867_g_.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		field_228867_g_.rotationPointY = 8.0F;
		field_228869_i_ = new ModelRenderer(64, 64, 0, 19);
		field_228869_i_.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		field_228868_h_ = new ModelRenderer(64, 64, 0, 0);
		field_228868_h_.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		field_228868_h_.rotationPointY = 9.0F;
		field_228868_h_.rotationPointZ = 1.0F;
		field_228870_j_ = new ModelRenderer(64, 64, 0, 0);
		field_228870_j_.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		field_228870_j_.rotationPointY = 8.0F;
	}

	@Override
	public void render(KeypadChestTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		World world = te.getWorld();
		boolean hasWorld = world != null;
		BlockState state = hasWorld ? te.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType type = state.has(ChestBlock.TYPE) ? state.get(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = state.getBlock();

		if(block instanceof AbstractChestBlock)
		{
			AbstractChestBlock<?> chest = (AbstractChestBlock<?>)block;
			boolean isDouble = type != ChestType.SINGLE;
			float horizontalAngle = state.get(ChestBlock.FACING).getHorizontalAngle();
			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> mergerCallback;

			matrix.push();
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.rotate(Vector3f.field_229181_d_.func_229187_a_(-horizontalAngle)); //YP, rotationDegrees
			matrix.translate(-0.5D, -0.5D, -0.5D);

			if(hasWorld)
				mergerCallback = chest.func_225536_a_(state, world, te.getPos(), true);
			else
				mergerCallback = TileEntityMerger.ICallback::func_225537_b_;

			float callbackResult = mergerCallback.apply(ChestBlock.func_226917_a_(te)).get(partialTicks);
			int brightness = mergerCallback.apply(new DualBrightnessCallback<>()).applyAsInt(p_225616_5_);
			Material material = getMaterial(te, type, isChristmas, te.getLidAngle(partialTicks) >= 0.9F);
			IVertexBuilder builder = material.func_229311_a_(buffer, RenderType::entityCutout);

			callbackResult = 1.0F - callbackResult;
			callbackResult = 1.0F - callbackResult * callbackResult * callbackResult;

			if(isDouble)
			{
				if(type == ChestType.LEFT)
					renderChest(matrix, builder, field_228868_h_, field_228870_j_, field_228869_i_, callbackResult, brightness, p_225616_6_);
				else
					renderChest(matrix, builder, field_228865_e_, field_228867_g_, field_228866_f_, callbackResult, brightness, p_225616_6_);
			}
			else
				this.renderChest(matrix, builder, field_228862_a_, field_228864_d_, field_228863_c_, callbackResult, brightness, p_225616_6_);

			matrix.pop();
		}
	}

	private void renderChest(MatrixStack matrix, IVertexBuilder builder, ModelRenderer p_228871_3_, ModelRenderer p_228871_4_, ModelRenderer p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_)
	{
		p_228871_3_.rotateAngleX = -(p_228871_6_ * ((float)Math.PI / 2F));
		p_228871_4_.rotateAngleX = p_228871_3_.rotateAngleX;
		p_228871_3_.render(matrix, builder, p_228871_7_, p_228871_8_);
		p_228871_4_.render(matrix, builder, p_228871_7_, p_228871_8_);
		p_228871_5_.render(matrix, builder, p_228871_7_, p_228871_8_);
	}

	private Material getMaterial(KeypadChestTileEntity te, ChestType type, boolean isChristmas, boolean isActive)
	{
		if(isChristmas)
			return getMaterialForType(type, CHRISTMAS_LEFT, CHRISTMAS_RIGHT, CHRISTMAS);
		else if(isActive)
			return getMaterialForType(type, LEFT_ACTIVE, RIGHT_ACTIVE, ACTIVE);
		else
			return getMaterialForType(type, LEFT_INACTIVE, RIGHT_INACTIVE, INACTIVE);
	}

	private Material getMaterialForType(ChestType type, Material left, Material right, Material single)
	{
		switch(type)
		{
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case SINGLE: default:
				return single;
		}
	}

	private static Material createMaterial(String name)
	{
		return new Material(Atlases.CHEST_ATLAS, new ResourceLocation("securitycraft", "entity/chest/" + name));
	}
}