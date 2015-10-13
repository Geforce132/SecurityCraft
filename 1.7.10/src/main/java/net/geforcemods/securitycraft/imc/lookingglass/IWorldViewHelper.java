package net.geforcemods.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.Utils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Simple helper class I wrote to add some additional functionality to {@link IWorldView}.
 * 
 * @author Geforce
 */
public class IWorldViewHelper {
	
	private final IWorldView view;
	
	private boolean isGrabbed = false;
	
	public IWorldViewHelper(IWorldView view){
		this.view = view;
	}
	
	public void markDirty(){
		view.markDirty();
	}
	
	public IWorldView getView(){
		return this.view;
	}

	public boolean isGrabbed() {
		return this.isGrabbed;
	}
	
}
