package net.geforcemods.securitycraft.compat.rologia;

import net.geforcemods.rologia.os.RologiaOS;
import net.geforcemods.rologia.os.gui.components.ScreenComponent;
import net.geforcemods.rologia.os.gui.screens.Screen;
import net.geforcemods.rologia.os.gui.screens.input.InputYesNoScreen;
import net.geforcemods.rologia.os.misc.Position;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class RequestOpenScreen extends InputYesNoScreen {

	private BlockPos blockPos;
	
	public RequestOpenScreen(RologiaOS os, Position pos, Screen returnScreen, String promptText, String blockPosition) {
		super(os, pos, returnScreen, promptText);
		String[] position = blockPosition.split("\\|");
		this.blockPos = new BlockPos(Integer.parseInt(position[0]), Integer.parseInt(position[1]), Integer.parseInt(position[2]));
	}
	
	@Override
	public void onComponentClicked(ScreenComponent component, Position mousePos, int mouseButtonClicked) {
		if(component == getYesButton()) {
			SecurityCraft.channel.sendToServer(new CheckPassword(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((IPasswordProtected) Minecraft.getInstance().world.getTileEntity(blockPos)).getPassword()));
			getOS().setScreen(getReturnScreen().getScreenName());
		}
		else if(component == getNoButton()) {
			getOS().setScreen(getReturnScreen().getScreenName());
		}
	}

}
