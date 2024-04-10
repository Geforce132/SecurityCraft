package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class OverlayToggleHandler {
	private static final List<IGuiOverlay> DISABLED_OVERLAYS = new ArrayList<>();

	private OverlayToggleHandler() {}

	@SubscribeEvent
	public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
		if (isDisabled(event.getOverlay()))
			event.setCanceled(true);
	}

	public static boolean isDisabled(VanillaGuiOverlay overlay) {
		NamedGuiOverlay namedGuiOverlay = GuiOverlayManager.findOverlay(overlay.id());

		if (namedGuiOverlay != null)
			return isDisabled(namedGuiOverlay.overlay());
		else
			return true;
	}

	public static boolean isDisabled(NamedGuiOverlay overlay) {
		return isDisabled(overlay.overlay());
	}

	public static boolean isDisabled(IGuiOverlay overlay) {
		return DISABLED_OVERLAYS.contains(overlay);
	}

	public static void enable(VanillaGuiOverlay overlay) {
		enable(overlay.type().overlay());
	}

	public static void enable(NamedGuiOverlay overlay) {
		enable(overlay.overlay());
	}

	public static void enable(IGuiOverlay overlay) {
		DISABLED_OVERLAYS.remove(overlay);
	}

	public static void disable(VanillaGuiOverlay overlay) {
		disable(overlay.type().overlay());
	}

	public static void disable(NamedGuiOverlay overlay) {
		disable(overlay.overlay());
	}

	public static void disable(IGuiOverlay overlay) {
		if (!isDisabled(overlay))
			DISABLED_OVERLAYS.add(overlay);
	}
}
