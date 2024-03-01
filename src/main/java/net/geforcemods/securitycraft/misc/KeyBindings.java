package net.geforcemods.securitycraft.misc;

import javax.swing.text.JTextComponent.KeyBinding;

import org.lwjgl.glfw.GLFW;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

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
	public static KeyMapping setDefaultViewingDirection;

	private KeyBindings() {}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		cameraZoomIn = register(event, "cameraZoomIn", GLFW.GLFW_KEY_EQUAL);
		cameraZoomOut = register(event, "cameraZoomOut", GLFW.GLFW_KEY_MINUS);
		cameraEmitRedstone = register(event, "cameraEmitRedstone", GLFW.GLFW_KEY_R);
		cameraActivateNightVision = register(event, "cameraActivateNightVision", GLFW.GLFW_KEY_N);
		setDefaultViewingDirection = register(event, "setDefaultViewingDirection", GLFW.GLFW_KEY_U);
	}

	private static KeyMapping register(RegisterKeyMappingsEvent event, String name, int defaultKey) {
		KeyMapping keyMapping = new KeyMapping("key.securitycraft." + name, defaultKey, "key.categories.securitycraft");

		event.register(keyMapping);
		return keyMapping;
	}
}
