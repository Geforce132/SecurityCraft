package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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
public class SecurityCraftTileEntity extends OwnableTileEntity implements ITickableTileEntity, INameSetter {

	protected boolean intersectsEntities = false;
	private ITextComponent customName = StringTextComponent.EMPTY;

	public SecurityCraftTileEntity()
	{
		this(SCContent.teTypeAbstract);
	}

	public SecurityCraftTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void tick() {
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
		if(!(world.getBlockState(getPos()).getBlock() instanceof IIntersectable)) return;

		((IIntersectable) world.getBlockState(getPos()).getBlock()).onEntityIntersected(getWorld(), getPos(), entity);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		tag.putBoolean("intersectsEntities", intersectsEntities);
		tag.putString("customName", customName.getString());
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if (tag.contains("intersectsEntities"))
			intersectsEntities = tag.getBoolean("intersectsEntities");

		if (tag.contains("customName"))
			customName = new StringTextComponent(tag.getString("customName"));
	}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's CompoundNBT
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(world == null) return;

		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}

	/**
	 * Sets the TileEntity able to be intersected with.
	 * <p>
	 * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, LivingEntity) when a {@link LivingEntity} comes into contact with this block.
	 * <p>
	 * Implement IIntersectable in your Block class in order to do stuff with that event.
	 */
	public SecurityCraftTileEntity intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	@Override
	public ITextComponent getName()
	{
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		ITextComponent name = getCustomName();

		return name != null && !StringTextComponent.EMPTY.equals(name) && !getDefaultName().equals(name);
	}

	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
		sync();
	}

	public ITextComponent getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getTranslationKey());
	}
}
