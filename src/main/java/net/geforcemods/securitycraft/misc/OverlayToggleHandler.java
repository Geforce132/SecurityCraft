package net.geforcemods.securitycraft.misc;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.SecurityCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.GuiOverlayManager;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.neoforge.client.gui.overlay.NamedGuiOverlay;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class OverlayToggleHandler {
	private static final Map<IGuiOverlay, Boolean> OVERLAY_STATES = new HashMap<>();

	private OverlayToggleHandler() {}

	@SubscribeEvent
	public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
		IGuiOverlay overlay = event.getOverlay().overlay();

		if (OVERLAY_STATES.containsKey(overlay) && !isEnabled(overlay))
			event.setCanceled(true);
	}

	public static boolean isEnabled(VanillaGuiOverlay overlay) {
		return isEnabled(GuiOverlayManager.findOverlay(overlay.id()).overlay());
	}

	public static boolean isEnabled(NamedGuiOverlay overlay) {
		return isEnabled(overlay.overlay());
	}

	public static boolean isEnabled(IGuiOverlay overlay) {
		return OVERLAY_STATES.get(overlay);
	}

	public static void enable(VanillaGuiOverlay overlay) {
		enable(overlay.type().overlay());
	}

	public static void enable(NamedGuiOverlay overlay) {
		enable(overlay.overlay());
	}

	public static void enable(IGuiOverlay overlay) {
		OVERLAY_STATES.put(overlay, true);
	}

	public static void disable(VanillaGuiOverlay overlay) {
		disable(overlay.type().overlay());
	}

	public static void disable(NamedGuiOverlay overlay) {
		disable(overlay.overlay());
	}

	public static void disable(IGuiOverlay overlay) {
		OVERLAY_STATES.put(overlay, false);
	}
}
