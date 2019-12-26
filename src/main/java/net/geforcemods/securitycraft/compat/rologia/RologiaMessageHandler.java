package net.geforcemods.securitycraft.compat.rologia;

import net.geforcemods.rologia.os.RologiaOS;
import net.geforcemods.rologia.os.imc.IRologiaMessageHandler;
import net.geforcemods.rologia.os.imc.RologiaMessage;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class RologiaMessageHandler implements IRologiaMessageHandler {
	
	public static final String REMOTE_ACCESS_REQUEST = "remote_access_request";

	@Override
	public void handleMessage(RologiaOS os, World world, RologiaMessage message) {
		String requester = message.senderName;
		String key = message.key;
		String body = message.body;

		if(key.equals(REMOTE_ACCESS_REQUEST) && EffectiveSide.get() != LogicalSide.SERVER) {	
			// TODO add easier way to send BlockPos in RologiaMessage and add customization to the prompt message
			os.setScreen(new RequestOpenScreen(os, os.getCurrentScreen().getPosition(), os.getCurrentScreen(), requester + " has requested that a keypad be opened.", body));
		}
	}

}
