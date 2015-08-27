package net.breakinbad.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;

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
