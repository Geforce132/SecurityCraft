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

	public BlockCageTrap(Material par2Material) {
		super(par2Material);
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
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
		if(BlockUtils.getBlock(par1World, pos) == SCContent.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(par1World, pos, DEACTIVATED))
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
				BlockUtils.setBlock(world, pos.up(4), SCContent.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), SCContent.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), SCContent.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, SCContent.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, SCContent.unbreakableIronBars);

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), SCContent.unbreakableIronBars);
				setTileEntities(world, pos.getX(), pos.getY(), pos.getZ(), ((IOwnable)world.getTileEntity(pos)).getOwner().getUUID(), ((IOwnable)world.getTileEntity(pos)).getOwner().getName());

				world.playSoundEffect(pos.getX(),pos.getY(),pos.getZ(), "random.anvil_use", 3.0F, 1.0F);

				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.cageTrap.captured").replace("#player", ((EntityPlayer) entity).getName()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCageTrap().intersectsEntities();
	}

	public void setTileEntities(World par1World, int par2, int par3, int par4, String uuid, String name)
	{
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3, par4))).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 4, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 4, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 4, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 4, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 4, par4 - 1))).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 1, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 2, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 3, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 1, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 2, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 3, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 1, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 2, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 3, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 1, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 2, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 3, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 1, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 2, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 3, par4 + 1))).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 1, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 2, par4))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 3, par4))).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 1, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 2, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2, par3 + 3, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 1, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 2, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 3, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 1, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 2, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 3, par4 - 1))).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 4, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 + 1, par3 + 4, par4 - 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 4, par4 + 1))).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(BlockUtils.toPos(par2 - 1, par3 + 4, par4 - 1))).getOwner().set(uuid, name);
	}
}
