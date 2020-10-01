package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.PortalSize;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SCWorldListener;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<>();

	public SCEventHandler()
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!ConfigHandler.sayThanksMessage || !event.player.getEntityWorld().isRemote)
			return;

		String tipKey = getRandomTip();
		ITextComponent message;

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.VERSION) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		else
			message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.VERSION) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey));

		event.player.sendMessage(message);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.player) && event.player.getRidingEntity() instanceof EntitySecurityCamera)
			event.player.getRidingEntity().setDead();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
		if(event.getEntityLiving() != null && PlayerUtils.isPlayerMountedOnCamera(event.getEntityLiving())){
			event.setCanceled(true);
			return;
		}

		if(event.getSource() == CustomDamageSources.ELECTRICITY)
			SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, SCSounds.ELECTRIFIED.path, 0.25F, "block"));
	}

	@SubscribeEvent
	public void onBucketUsed(FillBucketEvent event){
		if(event.getTarget() == null)
			return;

		ItemStack result = fillBucket(event.getWorld(), event.getTarget().getBlockPos());
		if(result.isEmpty())
			return;
		event.setFilledBucket(result);
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onRightClickBlock(RightClickBlock event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
		{
			event.setCanceled(true);
			return;
		}

		if(event.getHand() == EnumHand.MAIN_HAND)
		{
			World world = event.getWorld();

			if(!world.isRemote){
				TileEntity tileEntity = world.getTileEntity(event.getPos());
				Block block = world.getBlockState(event.getPos()).getBlock();

				if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.keyPanel))
				{
					for(Block pc : IPasswordConvertible.BLOCKS)
					{
						if(((IPasswordConvertible)pc).getOriginalBlock() == block)
						{
							event.setUseBlock(Result.DENY);
							event.setUseItem(Result.ALLOW);
						}
					}

					return;
				}

				if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.codebreaker) && handleCodebreaking(event)) {
					event.setCanceled(true);
					return;
				}

				if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.universalBlockModifier)){
					if(tileEntity instanceof IModuleInventory) {
						event.setCanceled(true);

						if(tileEntity instanceof IOwnable && !((IOwnable) tileEntity).getOwner().isOwner(event.getEntityPlayer())){
							if(!(tileEntity instanceof TileEntityDisguisable) || (((ItemBlock)((BlockDisguisable)((TileEntityDisguisable)tileEntity).getBlockType()).getDisguisedStack(world, event.getPos()).getItem()).getBlock() instanceof BlockDisguisable))
								PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), ClientUtils.localize("item.securitycraft:universalBlockModifier.name"), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);

							return;
						}

						event.getEntityPlayer().openGui(SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
						return;
					}
				}

				if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getEntityPlayer(), Items.NAME_TAG) && event.getEntityPlayer().inventory.getCurrentItem().hasDisplayName()){
					event.setCanceled(true);

					for(String character : new String[]{"(", ")"})
						if(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName().contains(character)) {
							PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.error").replace("#n", event.getEntityPlayer().inventory.getCurrentItem().getDisplayName()).replace("#c", character), TextFormatting.RED);
							return;
						}

					if(((INameable) tileEntity).getCustomName().equals(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName())) {
						PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomName()), TextFormatting.RED);
						return;
					}

					if(!event.getEntityPlayer().isCreative())
						event.getEntityPlayer().inventory.getCurrentItem().shrink(1);

					((INameable) tileEntity).setCustomName(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName());
					return;
				}

			}

			//outside !world.isRemote for properly checking the interaction
			//all the sentry functionality for when the sentry is diguised
			List<EntitySentry> sentries = world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
			{
				event.setCanceled(sentries.get(0).processInteract(event.getEntityPlayer(), event.getHand())); //cancel if an action was taken
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}
		}
	}

	@SubscribeEvent
	public void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		List<EntitySentry> sentries = event.getWorld().getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty())
		{
			BlockPos pos = event.getPos();

			if (!sentries.get(0).getDisguiseModule().isEmpty())
			{
				ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
				List<Block> blocks = ((ItemModule)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTagCompound());

				if(blocks.size() > 0)
				{
					if(blocks.get(0) == event.getWorld().getBlockState(pos).getBlock())
						event.setCanceled(true);
				}
			}

			return;
		}

		sentries = event.getWorld().getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos().up()));

		//remove sentry if block below is broken
		if(!sentries.isEmpty())
			sentries.get(0).remove();
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if(event.getModID().equals(SecurityCraft.MODID))
			ConfigManager.sync(SecurityCraft.MODID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent event) {
		handleOwnableTEs(event);

		//reinforced obsidian portal handling
		if(event.getState().getBlock() == Blocks.FIRE && event.getWorld().getBlockState(event.getPos().down()).getBlock() == SCContent.reinforcedObsidian)
		{
			PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

			if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
				portalSize.placePortalBlocks();
			else
			{
				portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
					portalSize.placePortalBlocks();
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		//fix for spawning under the portal
		if(event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) //nether
		{
			BlockPos pos = event.getEntity().getPosition();

			//check for obsidian or reinforced obsidian from the player's position up to the world height
			do
			{
				if(event.getWorld().getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
				{
					//check if the block is part of a valid portal, and if so move the entity down
					BlockPortal.Size portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL) //sometimes the top of the portal is more valid than the bottom o.O
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else //check other axis
					{
						portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
				else if(event.getWorld().getBlockState(pos).getBlock() == SCContent.reinforcedObsidian) //analogous to if check above
				{
					PortalSize portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else
					{
						portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
			}
			while((pos = pos.up()).getY() < Math.min(event.getWorld().getHeight(), 256)); //open cubic chunks "fix"
		}
	}

	@SubscribeEvent
	public void onNeighborNotify(NeighborNotifyEvent event)
	{
		//prevent portal blocks from disappearing because they think they're not inside of a proper portal frame
		if(event.getState().getBlock() == Blocks.PORTAL)
		{
			EnumFacing.Axis axis = event.getState().getValue(BlockPortal.AXIS);

			if (axis == EnumFacing.Axis.X)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
			else if (axis == EnumFacing.Axis.Z)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(!event.getWorld().isRemote) {
			if(event.getWorld().getTileEntity(event.getPos()) instanceof IModuleInventory){
				IModuleInventory te = (IModuleInventory) event.getWorld().getTileEntity(event.getPos());

				for(int i = 100; i - 100 < te.getMaxNumberOfModules(); i++) {
					if(!te.getStackInSlot(i).isEmpty()){
						ItemStack stack = te.getStackInSlot(i);
						EntityItem item = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().spawnEntity(item));

						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());

						if(te instanceof CustomizableSCTE)
							((CustomizableSCTE)te).createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, (CustomizableSCTE)te);

						if(te instanceof TileEntitySecurityCamera)
						{
							TileEntitySecurityCamera cam = (TileEntitySecurityCamera)te;

							cam.getWorld().notifyNeighborsOfStateChange(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).getValue(BlockSecurityCamera.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock(), true);
						}
					}
				}
			}

			List<EntitySentry> sentries = event.getWorld().getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
			{
				BlockPos pos = event.getPos();

				if (!sentries.get(0).getDisguiseModule().isEmpty())
				{
					ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
					List<Block> blocks = ((ItemModule)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTagCompound());

					if(blocks.size() > 0)
					{
						IBlockState state = blocks.get(0).getDefaultState();

						event.getWorld().setBlockState(pos, state.isFullBlock() ? state : Blocks.AIR.getDefaultState());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
			return;

		if(event.getTarget() instanceof EntityPlayer && event.getTarget() != event.getEntityLiving().getAttackingEntity())
		{
			if(PlayerUtils.isPlayerMountedOnCamera(event.getTarget()))
				((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
		}
		else if(event.getTarget() instanceof EntitySentry)
			((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		event.getWorld().addEventListener(new SCWorldListener());
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getEntityPlayer() != null)
		{
			Item held = event.getEntityPlayer().getHeldItemMainhand().getItem();

			if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			{
				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

					if(reinforcedBlock.getVanillaBlocks().contains(event.getState().getBlock()))
					{
						event.setNewSpeed(10000.0F);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event)
	{
		if(event.isDismounting() && event.getEntityBeingMounted() instanceof EntitySecurityCamera && event.getEntityMounting() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityMounting();
			TileEntity te = event.getWorldObj().getTileEntity(event.getEntityBeingMounted().getPosition());

			if(PlayerUtils.isPlayerMountedOnCamera(player) && te instanceof TileEntitySecurityCamera && ((TileEntitySecurityCamera)te).hasModule(EnumModuleType.SMART))
			{
				((TileEntitySecurityCamera)te).lastPitch = player.rotationPitch;
				((TileEntitySecurityCamera)te).lastYaw = player.rotationYaw;
			}
		}
	}

	@SubscribeEvent
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()) && event.getItemStack().getItem() != SCContent.cameraMonitor)
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().player.getRidingEntity().getPosition().equals(event.getTarget().getBlockPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(Minecraft.getMinecraft().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player)){
			if(event.getType() == ElementType.EXPERIENCE && (BlockUtils.getBlock(Minecraft.getMinecraft().world, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posX), (int)Minecraft.getMinecraft().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posZ))) instanceof BlockSecurityCamera))
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.getResolution(), Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posX), (int)Minecraft.getMinecraft().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.size())
				return;

			ItemStack stack = player.inventory.mainInventory.get(held);

			if(!stack.isEmpty() && stack.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == Type.BLOCK && world.getTileEntity(mop.getBlockPos()) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = stack.getTagCompound();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.hasKey("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == mop.getBlockPos().getX() && Integer.parseInt(coords[1]) == mop.getBlockPos().getY() && Integer.parseInt(coords[2]) == mop.getBlockPos().getZ())
							{
								textureToUse = "item_bound";
								break;
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.remoteAccessMine)
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == Type.BLOCK && world.getBlockState(mop.getBlockPos()).getBlock() instanceof IExplosive)
				{
					NBTTagCompound mines = stack.getTagCompound();

					if(mines != null)
						for(int i = 1; i <= 6; i++)
						{
							if(stack.getTagCompound().getIntArray("mine" + i).length > 0)
							{
								int[] coords = mines.getIntArray("mine" + i);

								if(coords[0] == mop.getBlockPos().getX() && coords[1] == mop.getBlockPos().getY() && coords[2] == mop.getBlockPos().getZ())
								{
									textureToUse = "item_bound";
									break;
								}
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.remoteAccessSentry)
			{
				String textureToUse = "item_not_bound";
				Entity hitEntity = Minecraft.getMinecraft().pointedEntity;

				if(hitEntity instanceof EntitySentry)
				{
					NBTTagCompound sentries = stack.getTagCompound();

					if(sentries != null)
						for(int i = 1; i <= 12; i++)
						{
							if(stack.getTagCompound().getIntArray("sentry" + i).length > 0)
							{
								int[] coords = sentries.getIntArray("sentry" + i);
								if(coords[0] == hitEntity.getPosition().getX() && coords[1] == hitEntity.getPosition().getY() && coords[2] == hitEntity.getPosition().getZ())
								{
									textureToUse = "item_bound";
									break;
								}
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDestroyEvent(LivingDestroyBlockEvent event)
	{
		event.setCanceled(event.getEntity() instanceof EntityWither && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntity()))
			event.setNewfov(((EntitySecurityCamera) event.getEntity().getRidingEntity()).getZoomAmount());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(MouseEvent event) {
		if(Minecraft.getMinecraft().world != null)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && event.getButton() != 1) //anything other than rightclick
				event.setCanceled(true);
		}
	}

	private void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		double z = 200;
		double widthFactor = 1F / (double) textureWidth;
		double heightFactor = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(u * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y + height, z).tex((u + width) * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y, z).tex((u + width) * widthFactor, v * heightFactor).endVertex();
		buffer.pos(x, y, z).tex(u * widthFactor, v * heightFactor).endVertex();
		tessellator.draw();
	}

	private ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.fakeWater){
			world.setBlockToAir(pos);
			return new ItemStack(SCContent.fWaterBucket, 1);
		}else if(block == SCContent.fakeLava){
			world.setBlockToAir(pos);
			return new ItemStack(SCContent.fLavaBucket, 1);
		}
		else
			return ItemStack.EMPTY;
	}

	private void handleOwnableTEs(PlaceEvent event) {
		if(event.getWorld().getTileEntity(event.getPos()) instanceof IOwnable) {
			String name = event.getPlayer().getName();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) event.getWorld().getTileEntity(event.getPos())).getOwner().set(uuid, name);
		}
	}

	private boolean handleCodebreaking(PlayerInteractEvent event) {
		if(ConfigHandler.allowCodebreakerItem)
		{
			World world = event.getEntityPlayer().world;
			TileEntity tileEntity = event.getEntityPlayer().world.getTileEntity(event.getPos());

			if(tileEntity instanceof IPasswordProtected)
			{
				if(event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == SCContent.codebreaker)
					event.getEntityPlayer().getHeldItem(event.getHand()).damageItem(1, event.getEntityPlayer());

				if(event.getEntityPlayer().isCreative() || new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getEntityPlayer(), !ConfigHandler.allowCodebreakerItem);
				else return true;
			}
		}

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
}
