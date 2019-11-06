package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionFloat;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class SecurityCameraTileEntity extends CustomizableTileEntity {

	private final float CAMERA_SPEED = 0.0180F;

	public float cameraRotation = 0.0F;
	public boolean addToRotation = true;
	public boolean down = false, downSet = false;
	public float lastPitch = Float.MAX_VALUE;
	public float lastYaw = Float.MAX_VALUE;
	private OptionFloat rotationSpeedOption = new OptionFloat("rotationSpeed", CAMERA_SPEED, 0.0100F, 0.0250F, 0.001F);
	private OptionBoolean shouldRotateOption = new OptionBoolean("shouldRotate", true);
	private OptionDouble customRotationOption = new OptionDouble(this, "customRotation", (double)cameraRotation, 1.55D, -1.55D, (double)rotationSpeedOption.asFloat(), true);

	public SecurityCameraTileEntity()
	{
		super(SCContent.teTypeSecurityCamera);
	}

	@Override
	public void tick(){
		super.tick();

		if(!downSet)
		{
			down = getBlockState().get(SecurityCameraBlock.FACING) == Direction.DOWN;
			downSet = true;
		}

		if(!shouldRotateOption.asBoolean())
		{
			cameraRotation = (float)customRotationOption.asDouble();
			return;
		}

		if(addToRotation && cameraRotation <= 1.55F)
			cameraRotation += rotationSpeedOption.asFloat();
		else
			addToRotation = false;

		if(!addToRotation && cameraRotation >= -1.55F)
			cameraRotation -= rotationSpeedOption.asFloat();
		else
			addToRotation = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		tag.putFloat("LastPitch", lastPitch);
		tag.putFloat("LastYaw", lastYaw);
		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		lastPitch = tag.getFloat("LastPitch");
		lastYaw = tag.getFloat("LastYaw");
	}

	@Override
	public void onModuleInserted(ItemStack stack, CustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}

	@Override
	public void onModuleRemoved(ItemStack stack, CustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}

	@Override
	public CustomModules[] acceptedModules(){
		return new CustomModules[] { CustomModules.REDSTONE, CustomModules.SMART };
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ rotationSpeedOption, shouldRotateOption, customRotationOption };
	}
}
