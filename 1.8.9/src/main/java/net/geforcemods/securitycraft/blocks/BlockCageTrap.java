package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCageTrap extends BlockOwnable implements IIntersectable {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockCageTrap(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state){
		if(BlockUtils.getBlock(world, pos) == SCContent.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
			return null;
		else
			return AxisAlignedBB.fromBounds(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ);
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

				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				BlockUtils.setBlock(world, pos.up(4), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, SCContent.reinforcedIronBars);

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), SCContent.reinforcedIronBars);
				setTileEntities(world, pos.getX(), pos.getY(), pos.getZ(), ((IOwnable)world.getTileEntity(pos)).getOwner().getUUID(), ((IOwnable)world.getTileEntity(pos)).getOwner().getName());

				world.playSoundEffect(pos.getX(),pos.getY(),pos.getZ(), "random.anvil_use", 3.0F, 1.0F);

				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.securitycraft:cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.securitycraft:cageTrap.captured").replace("#player", ((EntityPlayer) entity).getName()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {DEACTIVATED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCageTrap().intersectsEntities();
	}

	public void setTileEntities(World world, int x, int y, int z, String uuid, String name)
	{
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y, z))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z - 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 3, z + 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 3, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z - 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z - 1))).getOwner().set(uuid, name);
	}
}
