package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientProxy implements IProxy {

	/**
	 * Register the texture files used by blocks with metadata/variants with the ModelBakery.
	 */
	@Override
	public void registerVariants() {
		Item fakeWater = findItem(SecurityCraft.MODID, "bogus_water");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(SecurityCraft.MODID, "bogus_water_flowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(SecurityCraft.MODID, "bogus_Lava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(SecurityCraft.MODID, "bogus_lava_flowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing");
			}
		});
	}

	private Item findItem(String modid, String resourceName)
	{
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid + ":" + resourceName));
	}

	@Override
	public void registerRenderThings(){
		KeyBindings.init();

		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, manager -> new RenderBouncingBetty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, manager -> new RenderIMSBomb(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, manager -> new RenderSentry(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, manager -> new RenderBullet(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());

		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();
	}
}
