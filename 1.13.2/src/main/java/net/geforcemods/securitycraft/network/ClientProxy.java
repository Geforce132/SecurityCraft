package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.KeyBindings;
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
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class ClientProxy implements IProxy {
	private final List<Block> toTint = new ArrayList<>();

	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		SecurityCraft.proxy.registerRenderThings();
	}

	@Override
	public void registerKeybindings()
	{
		KeyBindings.init();
	}

	@Override
	public void registerRenderThings(){
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, manager -> new RenderBouncingBetty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, manager -> new RenderIMSBomb(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, manager -> new RenderSentry(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, manager -> new RenderBullet(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
	}

	@Override
	public List<Block> getOrPopulateToTint()
	{
		if(toTint.isEmpty())
		{
			for(Field field : SCContent.class.getFields())
			{
				if(field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).hasTint())
				{
					try
					{
						toTint.add((Block)field.get(null));
					}
					catch(IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return toTint;
	}

	@Override
	public void cleanup()
	{
		toTint.clear();
	}
}
