package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;

public class ClaymoreTileEntity extends SecurityCraftTileEntity{

	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;

	public ClaymoreTileEntity()
	{
		super(SCContent.teTypeClaymore);
	}

	@Override
	public void tick() {
		if(!getWorld().isRemote)
		{
			if(getWorld().getBlockState(getPos()).get(ClaymoreBlock.DEACTIVATED))
				return;

			if(cooldown > 0){
				cooldown--;
				return;
			}

			if(cooldown == 0){
				getWorld().destroyBlock(getPos(), false);
				getWorld().createExplosion((Entity) null, entityX, entityY + 0.5F, entityZ, 3.5F, true, Mode.BREAK);
				return;
			}

			Direction dir = BlockUtils.getBlockProperty(getWorld(), getPos(), ClaymoreBlock.FACING);
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

			if(dir == Direction.NORTH)
				area = area.contract(-0, -0, ConfigHandler.CONFIG.claymoreRange.get());
			else if(dir == Direction.SOUTH)
				area = area.contract(-0, -0, -ConfigHandler.CONFIG.claymoreRange.get());
			else if(dir == Direction.EAST)
				area = area.contract(-ConfigHandler.CONFIG.claymoreRange.get(), -0, -0);
			else if(dir == Direction.WEST)
				area = area.contract(ConfigHandler.CONFIG.claymoreRange.get(), -0, -0);

			List<?> entities = getWorld().getEntitiesWithinAABB(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e));
			Iterator<?> iterator = entities.iterator();
			LivingEntity entity;

			while(iterator.hasNext()){
				entity = (LivingEntity) iterator.next();

				if(PlayerUtils.isPlayerMountedOnCamera(entity) || EntityUtils.doesEntityOwn(entity, world, pos))
					continue;

				entityX = entity.getPosX();
				entityY = entity.getPosY();
				entityZ = entity.getPosZ();
				cooldown = 20;
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
				break;
			}
		}

	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		tag.putInt("cooldown", cooldown);
		tag.putDouble("entityX", entityX);
		tag.putDouble("entityY", entityY);
		tag.putDouble("entityZ", entityZ);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT tag)
	{
		super.func_230337_a_(state, tag);

		if (tag.contains("cooldown"))
			cooldown = tag.getInt("cooldown");

		if (tag.contains("entityX"))
			entityX = tag.getDouble("entityX");

		if (tag.contains("entityY"))
			entityY = tag.getDouble("entityY");

		if (tag.contains("entityZ"))
			entityZ = tag.getDouble("entityZ");
	}

}
