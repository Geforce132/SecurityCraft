package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntitySecurityCamera extends CustomizableSCTE implements IEMPAffected, ITickable {

	private final double CAMERA_SPEED = 0.0180D;
	public double cameraRotation = 0.0D;
	public boolean addToRotation = true;
	public boolean down = false;
	public float lastPitch = Float.MAX_VALUE;
	public float lastYaw = Float.MAX_VALUE;
	private boolean shutDown = false;
	private OptionDouble rotationSpeedOption = new OptionDouble(this::getPos, "rotationSpeed", CAMERA_SPEED, 0.01D, 0.025D, 0.001D, true);
	private OptionBoolean shouldRotateOption = new OptionBoolean("shouldRotate", true);
	private OptionDouble customRotationOption = new OptionDouble(this::getPos, "customRotation", cameraRotation, 1.55D, -1.55D, rotationSpeedOption.get(), true);
	private int playersViewing = 0;

	@Override
	public void update(){
		if(!shutDown)
		{
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
	}

	@Override
	public boolean isShutDown()
	{
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown)
	{
		this.shutDown = shutDown;
	}

	@Override
	public TileEntity getTileEntity()
	{
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setFloat("LastPitch", lastPitch);
		tag.setFloat("LastYaw", lastYaw);
		tag.setBoolean("ShutDown", shutDown);
		tag.setInteger("PlayersViewing", playersViewing);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		lastPitch = tag.getFloat("LastPitch");
		lastYaw = tag.getFloat("LastYaw");
		shutDown = tag.getBoolean("ShutDown");
		playersViewing = tag.getInteger("PlayersViewing");
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == EnumModuleType.REDSTONE)
		{
			IBlockState newState = world.getBlockState(pos).withProperty(BlockSecurityCamera.POWERED, false);

			world.setBlockState(pos, newState);
			world.notifyNeighborsOfStateChange(pos, blockType, false);
			world.notifyNeighborsOfStateChange(pos.offset(newState.getValue(BlockSecurityCamera.FACING).getOpposite()), blockType, false);
		}
	}

	@Override
	public EnumModuleType[] acceptedModules(){
		return new EnumModuleType[] { EnumModuleType.REDSTONE, EnumModuleType.ALLOWLIST };
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

	public void startViewing()
	{
		playersViewing++;
		sync();
	}

	public void stopViewing()
	{
		playersViewing--;
		sync();
	}

	public boolean isSomeoneViewing()
	{
		return playersViewing > 0;
	}
}
