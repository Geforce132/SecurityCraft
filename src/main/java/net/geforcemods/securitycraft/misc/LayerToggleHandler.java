package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class LayerToggleHandler {
	private static final List<Identifier> DISABLED_LAYERS = new ArrayList<>();

	private LayerToggleHandler() {}

	@SubscribeEvent
	public static void onRenderGuiOverlayPre(RenderGuiLayerEvent.Pre event) {
		if (isDisabled(event.getName()))
			event.setCanceled(true);
	}

	public static boolean isDisabled(Identifier layer) {
		return DISABLED_LAYERS.contains(layer);
	}

	public static void enable(Identifier layer) {
		DISABLED_LAYERS.remove(layer);
	}

	public static void disable(Identifier layer) {
		if (!isDisabled(layer))
			DISABLED_LAYERS.add(layer);
	}
}
