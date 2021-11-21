package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Simple TileEntity that SecurityCraft uses to easily create blocks like
 * the retinal scanner (activated by looking at it) and attacking blocks
 * like the protecto. Everything can be overridden for easy customization
 * or use as an API.
 *
 * @version 1.1.3
 *
 * @author Geforce
 */
public class TileEntitySCTE extends TileEntityOwnable implements ITickable, INameSetter {

	protected boolean intersectsEntities = false;
	private String customName = "";

	@Override
	public void update() {
		if(intersectsEntities){
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			AxisAlignedBB area = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
			List<?> entities = world.getEntitiesWithinAABB(Entity.class, area);
			Iterator<?> iterator = entities.iterator();
			Entity entity;

			while (iterator.hasNext())
			{
				entity = (Entity)iterator.next();
				entityIntersecting(entity);
			}
		}
	}

	public void entityIntersecting(Entity entity) {
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof IIntersectable)
			((IIntersectable)state.getBlock()).onEntityIntersected(world, pos, state, entity);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("intersectsEntities", intersectsEntities);
		tag.setString("customName", customName);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("intersectsEntities"))
			intersectsEntities = tag.getBoolean("intersectsEntities");

		if (tag.hasKey("customName"))
			customName = tag.getString("customName");
	}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's NBTTagCompound
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(world == null) return;

		if(world.isRemote)
			ClientUtils.syncTileEntity(this);
		else
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getUpdatePacket());
	}

	/**
	 * Sets the TileEntity able to be intersected with.
	 * <p>
	 * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, EntityLivingBase) when a {@link EntityLivingBase} comes into contact with this block.
	 * <p>
	 * Implement IIntersectable in your Block class in order to do stuff with that event.
	 */
	public TileEntitySCTE intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	@Override
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return !customName.isEmpty() && !customName.equals(getDefaultName().getFormattedText());
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(customName) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(blockType.getTranslationKey() + ".name");
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
		sync();
	}
}
