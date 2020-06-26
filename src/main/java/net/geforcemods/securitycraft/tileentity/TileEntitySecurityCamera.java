package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntitySecurityCamera extends CustomizableSCTE {

	private final double CAMERA_SPEED = 0.0180D;
	public double cameraRotation = 0.0D;
	public boolean addToRotation = true;
	public boolean down = false;
	public float lastPitch = Float.MAX_VALUE;
	public float lastYaw = Float.MAX_VALUE;
	private OptionDouble rotationSpeedOption = new OptionDouble("rotationSpeed", CAMERA_SPEED, 0.0100D, 0.0250D, 0.001D);
	private OptionBoolean shouldRotateOption = new OptionBoolean("shouldRotate", true);
	private OptionDouble customRotationOption = new OptionDouble(this, "customRotation", cameraRotation, 1.55D, -1.55D, rotationSpeedOption.get(), true);

	@Override
	public void update(){
		super.update();

		if(!shouldRotateOption.get())
		{
			cameraRotation = customRotationOption.get();
			return;
		}

		if(addToRotation && cameraRotation <= 1.55F)
			cameraRotation += rotationSpeedOption.get();
		else
			addToRotation = false;

		if(!addToRotation && cameraRotation >= -1.55F)
			cameraRotation -= rotationSpeedOption.get();
		else
			addToRotation = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setFloat("LastPitch", lastPitch);
		tag.setFloat("LastYaw", lastYaw);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		lastPitch = tag.getFloat("LastPitch");
		lastYaw = tag.getFloat("LastYaw");
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == EnumModuleType.REDSTONE)
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSecurityCamera.POWERED, false));
	}

	@Override
	public EnumModuleType[] acceptedModules(){
		return new EnumModuleType[] { EnumModuleType.REDSTONE, EnumModuleType.SMART };
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ rotationSpeedOption, shouldRotateOption, customRotationOption };
	}

	@Override
	public void onLoad()
	{
		super.onLoad();

		if(world != null && world.getBlockState(pos).getBlock() instanceof BlockSecurityCamera)
			down = world.getBlockState(pos).getValue(BlockSecurityCamera.FACING) == EnumFacing.DOWN;
	}
}
