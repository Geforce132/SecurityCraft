package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.imc.lookingglass.LookingGlassAPIProvider;
import net.geforcemods.securitycraft.misc.CameraShutoffTimer;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class TileEntityFrame extends TileEntityOwnable {

	//private int[] boundCameraLocation = new int[3];
	private CameraView cameraView;
	private boolean shouldShowView = false;
	private boolean createdView = false;

	@Override
	public void update(){
		if(world.isRemote && world.isBlockLoaded(pos) && hasCameraLocation() && !SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()) && !createdView){
			if(BlockUtils.getBlockMeta(world, pos) == 0 || createdView) return;

			LookingGlassAPIProvider.createLookingGlassView(world, cameraView.dimension, pos, 192, 192);

			createdView = true;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		if(hasCameraLocation())
			par1NBTTagCompound.setString("cameraLoc", cameraView.toNBTString());

		return par1NBTTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		if(par1NBTTagCompound.hasKey("cameraLoc")){
			String[] coords = par1NBTTagCompound.getString("cameraLoc").split(" ");

			setCameraLocation(new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])), coords.length == 4 ? Integer.parseInt(coords[3]) : 0);
		}
	}

	public void setCameraLocation(BlockPos pos, int dimension){
		if(cameraView == null) {
			cameraView = new CameraView(pos, dimension);
			return;
		}

		cameraView.setLocation(pos, dimension);
	}

	public CameraView getCameraView(){
		return cameraView;
	}

	public int getCamDimension(){
		return cameraView.dimension;
	}

	public boolean hasCameraLocation(){
		return cameraView != null;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldShowView(){
		return SecurityCraft.config.fiveMinAutoShutoff ? shouldShowView : true;
	}

	@SideOnly(Side.CLIENT)
	public void enableView(){
		shouldShowView = true;

		if(SecurityCraft.config.fiveMinAutoShutoff){
			if(!SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()))
				LookingGlassAPIProvider.createLookingGlassView(world, cameraView.dimension, cameraView.getLocation(), 192, 192);

			new CameraShutoffTimer(this);
		}
	}

	@SideOnly(Side.CLIENT)
	public void disableView(){
		WorldUtils.addScheduledTask(Minecraft.getMinecraft().world, () -> {
			if(SecurityCraft.config.fiveMinAutoShutoff && SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString())){
				SecurityCraft.instance.lookingGlass.cleanupWorldView(SecurityCraft.instance.getViewFromCoords(cameraView.toNBTString()).getView());
				SecurityCraft.instance.removeViewForCoords(cameraView.toNBTString());
			}

			shouldShowView = false;
		});
	}

}
