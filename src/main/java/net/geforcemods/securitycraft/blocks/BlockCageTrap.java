package net.geforcemods.securitycraft.blocks;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedIronBars;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCageTrap extends BlockOwnable implements IIntersectable {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockCageTrap(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos) == SCContent.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
			return null;
		else
			return blockState.getBoundingBox(world, pos);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			TileEntityCageTrap tileEntity = (TileEntityCageTrap) world.getTileEntity(pos);
			boolean isPlayer = entity instanceof EntityPlayer;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();

			if(isPlayer || (entity instanceof EntityMob && shouldCaptureMobs)){
				if((isPlayer && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner((EntityPlayer)entity)))
					return;

				if(BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
					return;

				BlockPos topMiddle = pos.up(4);
				BlockModifier placer = new BlockModifier(world, new MutableBlockPos(pos), tileEntity.getOwner());

				placer.loop((w, p, o) -> {
					if(w.isAirBlock(p))
					{
						if(p.equals(topMiddle))
							w.setBlockState(p, SCContent.horizontalReinforcedIronBars.getDefaultState());
						else
							w.setBlockState(p, ((BlockReinforcedIronBars)SCContent.reinforcedIronBars).getActualState(SCContent.reinforcedIronBars.getDefaultState(), w, p));
					}
				});
				placer.loop((w, p, o) -> {
					TileEntity te = w.getTileEntity(p);

					if(te instanceof IOwnable)
						((IOwnable)te).getOwner().set(o);
				});
				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if(isPlayer)
					world.getMinecraftServer().sendMessage(new TextComponentTranslation("["+ TextFormatting.BLACK + ClientUtils.localize("tile.securitycraft:cageTrap.name") + TextFormatting.RESET + "] " + ClientUtils.localize("messages.securitycraft:cageTrap.captured").replace("#player", ((EntityPlayer) entity).getName()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(DEACTIVATED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(DEACTIVATED, (meta == 1 ? true : false));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(DEACTIVATED).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {DEACTIVATED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCageTrap().intersectsEntities();
	}

	public static class BlockModifier
	{
		private World world;
		private MutableBlockPos pos;
		private BlockPos origin;
		private Owner owner;

		public BlockModifier(World world, MutableBlockPos origin, Owner owner)
		{
			this.world = world;
			pos = origin.setPos(origin.getX() - 1, origin.getY() + 1, origin.getZ() - 1);
			this.origin = origin.toImmutable();
			this.owner = owner;
		}

		public void loop(TriConsumer<World,MutableBlockPos,Owner> ifTrue)
		{
			for(int y = 0; y < 4; y++)
			{
				for(int x = 0; x < 3; x++)
				{
					for(int z = 0; z < 3; z++)
					{
						//skip the middle column above the cage trap, but not the place where the horiztonal iron bars are
						if(!(x == 1 && z == 1 && y != 3))
							ifTrue.accept(world, pos, owner);

						pos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
					}

					pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() - 3);
				}

				pos.setPos(pos.getX() - 3, pos.getY() + 1, pos.getZ());
			}

			pos.setPos(origin); //reset the mutable block pos for the next usage
		}

		@FunctionalInterface
		public interface TriFunction<T,U,V,R>
		{
			R apply(T t, U u, V v);
		}
	}
}
