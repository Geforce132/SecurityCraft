package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.PortalSize;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<String, String>();

	public SCEventHandler()
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!SecurityCraft.config.sayThanksMessage || !event.player.getEntityWorld().isRemote)
			return;

		String tipKey = getRandomTip();
		IChatComponent message;

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.securitycraft:tip") + " " + StatCollector.translateToLocal(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		else
			message = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.securitycraft:tip") + " " + StatCollector.translateToLocal(tipKey));

		event.player.addChatComponentMessage(message);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.player) && event.player.ridingEntity instanceof EntitySecurityCamera)
			event.player.ridingEntity.setDead();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
		if(event.entityLiving != null && PlayerUtils.isPlayerMountedOnCamera(event.entityLiving)){
			event.setCanceled(true);
			return;
		}

		if(event.source == CustomDamageSources.electricity)
			SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(event.entity.posX, event.entity.posY, event.entity.posZ, SCSounds.ELECTRIFIED.path, 0.25F));
	}

	@SubscribeEvent
	public void onBucketUsed(FillBucketEvent event){
		ItemStack result = fillBucket(event.world, event.target.getBlockPos());
		if(result == null)
			return;
		event.result = result;
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(!event.entityPlayer.worldObj.isRemote){
			World world = event.entityPlayer.worldObj;
			TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.pos);
			Block block = event.entityPlayer.worldObj.getBlockState(event.pos).getBlock();

			if(event.action != Action.RIGHT_CLICK_BLOCK) return;

			if(event.action == Action.RIGHT_CLICK_BLOCK && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.codebreaker) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && tileEntity instanceof CustomizableSCTE && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.universalBlockModifier)){
				event.setCanceled(true);

				if(!((IOwnable) tileEntity).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.securitycraft:universalBlockModifier.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				event.entityPlayer.openGui(SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.pos.getX(), event.pos.getY(), event.pos.getZ());
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.entityPlayer, Items.name_tag) && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);

				for(String character : new String[]{"(", ")"})
					if(event.entityPlayer.getCurrentEquippedItem().getDisplayName().contains(character)) {
						PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.securitycraft:naming.error").replace("#n", event.entityPlayer.getCurrentEquippedItem().getDisplayName()).replace("#c", character), EnumChatFormatting.RED);
						return;
					}

				if(((INameable) tileEntity).getCustomName().matches(event.entityPlayer.getCurrentEquippedItem().getDisplayName())) {
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.securitycraft:naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomName()), EnumChatFormatting.RED);
					return;
				}

				if(!event.entityPlayer.capabilities.isCreativeMode)
					event.entityPlayer.getCurrentEquippedItem().stackSize--;

				((INameable) tileEntity).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.universalBlockRemover)){
				event.setCanceled(true);

				if(!((IOwnable) tileEntity).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.securitycraft:universalBlockRemover.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				if(block == SCContent.laserBlock){
					world.destroyBlock(event.pos, true);
					BlockLaserBlock.destroyAdjacentLasers(event.world, event.pos);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else if(block == SCContent.cageTrap && BlockUtils.getBlockPropertyAsBoolean(world, event.pos, BlockCageTrap.DEACTIVATED)) {
					BlockPos originalPos = event.pos;
					BlockPos pos = originalPos.east().up();

					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(2);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(3);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up().south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(2).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(3).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(2);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(3);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up().south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(2).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(3).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up().south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(2).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(3).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(2);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(3);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up().north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(2).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(3).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up().north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(2).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(3).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up().north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(2).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(3).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(4).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(4).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(4).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(4).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(4).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.east().up(4);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.west().up(4);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(4).south();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(4).north();
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					pos = originalPos.up(4);
					if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos, false);

					BlockUtils.destroyBlock(world, originalPos, false);
				}else{
					world.destroyBlock(event.pos, true);
					world.removeTileEntity(event.pos);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.modID.equals("securitycraft")){
			SecurityCraft.configFile.save();

			SecurityCraft.config.setupConfiguration();
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent event) {
		handleOwnableTEs(event);

		//reinforced obsidian portal handling
		if(event.state.getBlock() == Blocks.fire && event.world.getBlockState(event.pos.down()).getBlock() == SCContent.reinforcedObsidian)
		{
			PortalSize portalSize = new PortalSize(event.world, event.pos, EnumFacing.Axis.X);

			if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
				portalSize.placePortalBlocks();
			else
			{
				portalSize = new PortalSize(event.world, event.pos, EnumFacing.Axis.Z);

				if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
					portalSize.placePortalBlocks();
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		//fix for spawning under the portal
		if(event.entity instanceof EntityPlayer && !event.world.isRemote) //nether
		{
			BlockPos pos = event.entity.getPosition();

			//check for obsidian or reinforced obsidian from the player's position up to the world height
			do
			{
				if(event.world.getBlockState(pos).getBlock() == Blocks.obsidian)
				{
					//check if the block is part of a valid portal, and if so move the entity down
					BlockPortal.Size portalSize = new BlockPortal.Size(event.world, pos, EnumFacing.Axis.X);

					if (portalSize.func_150860_b()) //isValid
					{
						double y = pos.getY() + 0.5D;

						if(event.world.getBlockState(pos.down()).getBlock() == Blocks.portal) //sometimes the top of the portal is more valid than the bottom o.O
							y -= 3.0D;

						event.entity.setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else //check other axis
					{
						portalSize = new BlockPortal.Size(event.world, pos, EnumFacing.Axis.Z);

						if (portalSize.func_150860_b()) //isValid
						{
							double y = pos.getY() + 0.5D;

							if(event.world.getBlockState(pos.down()).getBlock() == Blocks.portal)
								y -= 3.0D;

							event.entity.setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
				else if(event.world.getBlockState(pos).getBlock() == SCContent.reinforcedObsidian) //analogous to if check above
				{
					PortalSize portalSize = new PortalSize(event.world, pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.world.getBlockState(pos.down()).getBlock() == Blocks.portal)
							y -= 3.0D;

						event.entity.setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else
					{
						portalSize = new PortalSize(event.world, pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.world.getBlockState(pos.down()).getBlock() == Blocks.portal)
								y -= 3.0D;

							event.entity.setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
			}
			while((pos = pos.up()).getY() < event.world.getHeight());

		}
	}

	@SubscribeEvent
	public void onNeighborNotify(NeighborNotifyEvent event)
	{
		//prevent portal blocks from disappearing because they think they're not inside of a proper portal frame
		if(event.state.getBlock() == Blocks.portal)
		{
			EnumFacing.Axis axis = (EnumFacing.Axis)event.state.getValue(BlockPortal.AXIS);

			if (axis == EnumFacing.Axis.X)
			{
				PortalSize portalSize = new PortalSize(event.world, event.pos, EnumFacing.Axis.X);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
			else if (axis == EnumFacing.Axis.Z)
			{
				PortalSize portalSize = new PortalSize(event.world, event.pos, EnumFacing.Axis.Z);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(!event.world.isRemote)
			if(event.world.getTileEntity(event.pos) != null && event.world.getTileEntity(event.pos) instanceof CustomizableSCTE){
				CustomizableSCTE te = (CustomizableSCTE) event.world.getTileEntity(event.pos);

				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++)
					if(te.itemStacks[i] != null){
						ItemStack stack = te.itemStacks[i];
						EntityItem item = new EntityItem(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ(), stack);
						event.world.spawnEntityInWorld(item);

						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
						te.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, te);

						if(te instanceof TileEntitySecurityCamera)
							te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset((EnumFacing)te.getWorld().getBlockState(te.getPos()).getValue(BlockSecurityCamera.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock());
					}
			}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.target != null && event.target instanceof EntityPlayer && event.target != event.entityLiving.func_94060_bK())
			if(PlayerUtils.isPlayerMountedOnCamera(event.target))
				((EntityLiving)event.entityLiving).setAttackTarget(null);
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.entityPlayer != null && event.entityPlayer.getHeldItem() != null)
		{
			Item held = event.entityPlayer.getHeldItem().getItem();

			if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			{
				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

					if(reinforcedBlock.getVanillaBlocks().contains(event.state.getBlock()))
					{
						event.newSpeed = 10000.0F;
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.entityPlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().thePlayer.ridingEntity.getPosition().equals(event.target.getBlockPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(Minecraft.getMinecraft().thePlayer != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer)){
			if(event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE && ((BlockUtils.getBlock(Minecraft.getMinecraft().theWorld, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)Minecraft.getMinecraft().thePlayer.ridingEntity.posY, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ))) instanceof BlockSecurityCamera)))
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.resolution, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)Minecraft.getMinecraft().thePlayer.ridingEntity.posY, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ)));
		}
		else if(event.type == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.thePlayer;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.length)
				return;

			ItemStack monitor = player.inventory.mainInventory[held];

			if(monitor != null && monitor.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "cameraNotBound";
				double eyeHeight = player.getEyeHeight();
				Vec3 lookVec = new Vec3((player.posX + (player.getLookVec().xCoord * 5)), ((eyeHeight + player.posY) + (player.getLookVec().yCoord * 5)), (player.posZ + (player.getLookVec().zCoord * 5)));
				MovingObjectPosition mop = world.rayTraceBlocks(new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK && world.getTileEntity(mop.getBlockPos()) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = monitor.getTagCompound();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.hasKey("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == mop.getBlockPos().getX() && Integer.parseInt(coords[1]) == mop.getBlockPos().getY() && Integer.parseInt(coords[2]) == mop.getBlockPos().getZ())
							{
								textureToUse = "cameraBound";
								break;
							}
						}

					GlStateManager.enableBlend();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.resolution.getScaledWidth() / 2 - 90 + held * 20 + 2, event.resolution.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableBlend();
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.entity))
			event.newfov = ((EntitySecurityCamera) event.entity.ridingEntity).getZoomAmount();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(MouseEvent event) {
		if(Minecraft.getMinecraft().theWorld != null)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer) && event.button != 1) //anything other than rightclick
				event.setCanceled(true);
		}
	}

	private void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		double z = 200;
		double widthFactor = 1F / (double) textureWidth;
		double heightFactor = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.startDrawingQuads();
		worldRenderer.addVertexWithUV(x, y + height, z, u * widthFactor, (v + height) * heightFactor);
		worldRenderer.addVertexWithUV(x + width, y + height, z, (u + width) * widthFactor, (v + height) * heightFactor);
		worldRenderer.addVertexWithUV(x + width, y, z, (u + width) * widthFactor, v * heightFactor);
		worldRenderer.addVertexWithUV(x, y, z, u * widthFactor, v * heightFactor);
		tessellator.draw();
	}

	private ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.bogusWater){
			world.setBlockToAir(pos);
			return new ItemStack(SCContent.fWaterBucket, 1);
		}else if(block == SCContent.bogusLava){
			world.setBlockToAir(pos);
			return new ItemStack(SCContent.fLavaBucket, 1);
		}
		else
			return null;
	}

	private void handleOwnableTEs(PlaceEvent event) {
		if(event.world.getTileEntity(event.pos) instanceof IOwnable) {
			String name = event.player.getCommandSenderName();
			String uuid = event.player.getGameProfile().getId().toString();

			((IOwnable) event.world.getTileEntity(event.pos)).getOwner().set(uuid, name);
		}
	}

	private boolean handleCodebreaking(PlayerInteractEvent event) {
		World world = event.entityPlayer.worldObj;
		TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.pos);

		if(SecurityCraft.config.allowCodebreakerItem) //safety so when codebreakers are disabled they can't take damage
			event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);

		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1)
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.pos), event.entityPlayer, !SecurityCraft.config.allowCodebreakerItem);

		return false;
	}

	private String getRandomTip(){
		String[] tips = {
				"messages.tip.scHelp",
				"messages.tip.trello",
				"messages.tip.patreon",
				"messages.tip.discord",
				"messages.tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
	}

}
