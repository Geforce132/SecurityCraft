package net.geforcemods.securitycraft.misc;

import javax.swing.text.JTextComponent.KeyBinding;

import org.lwjgl.glfw.GLFW;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Custom {@link KeyBinding}s that SecurityCraft uses.
 *
 * @author Geforce
 */
@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class KeyBindings {
	public static KeyMapping cameraZoomIn;
	public static KeyMapping cameraZoomOut;
	public static KeyMapping cameraEmitRedstone;
	public static KeyMapping cameraActivateNightVision;

	private KeyBindings() {}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		cameraZoomIn = new KeyMapping("key.securitycraft.cameraZoomIn", GLFW.GLFW_KEY_EQUAL, "key.categories.securitycraft");
		cameraZoomOut = new KeyMapping("key.securitycraft.cameraZoomOut", GLFW.GLFW_KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyMapping("key.securitycraft.cameraEmitRedstone", GLFW.GLFW_KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyMapping("key.securitycraft.cameraActivateNightVision", GLFW.GLFW_KEY_N, "key.categories.securitycraft");

		event.register(cameraZoomIn);
		event.register(cameraZoomOut);
		event.register(cameraEmitRedstone);
		event.register(cameraActivateNightVision);
	}
}
