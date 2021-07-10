--------------------------Changelog for v1.8.23 of SecurityCraft--------------------------

- New: The Frame block will now send a message when being rightclicked with a Camera Monitor, notifying the player that viewing a Camera Monitor in a Frame is currently not possible
- New: The Trophy System now supports the Allowlist Module. Projectiles by listed players (as well as their Sentries' and I.M.S.' projectiles) will be ignored
- Fix: Blocks with a tint (Grass, leaves, reinforced blocks, ...) projected by the Projector are not tinted correctly
- Fix: Blocks projected by the projector have incorrect lighting
- Fix: The Protecto does not attack players
- Fix: The Frame's model does not match up with the vanilla iron block (this fix may break resource packs slightly)
- Fix: Tooltips in the Block Pocket Manager's GUI still show up even if the block pocket is activated

--------------------------Changelog for v1.8.22.2 of SecurityCraft--------------------------

- Fix: Players cannot open other players' Password-protected Chests/Furnaces, or Keypads with the correct code

--------------------------Changelog for v1.8.22 of SecurityCraft--------------------------

- New: SecurityCraft's blocks and items can now properly be used by the offhand
- New: Server configuration option "force_reinforced_block_tint". This can be used to force players to use the setting of the server config value "reinforced_block_tint" 
- New: Client configuration option "reinforced_block_tint". If the server does not force the reinforced block tint, this value will be used
- New: The Codebreaker will now send a message when it failed to break a code
- New: The Admin Tool will now send a message when used while it has been disabled in the config
- New: Players that have been added to an Allow- or Denylist Module will now be visible in a list in the GUI
- New: Changing the owner of a block will now drop contained modules and storage module inventory
- New: Configuration option "mineExplosionsBreakBlocks" to control whether explosions of mines break blocks
- New: The Username Logger now supports the Allowlist Module, making it possible to prevent players on the list from being logged
- New: The Trophy System is now also capable of targeting Ender Pearls, Snowballs, Eggs, Llama Spit and Firework Rockets
- New: Inserting a Smart Module into a Trophy System now allows its owner to manually configure which projectile types the Trophy System is allowed to target
- New: Keycards can now be linked to Keycard Readers via a signature that can be shared with multiple other readers. See the SecurityCraft Manual for more info
- New: The Allowlist Module can now be used in the Cage Trap
- New: Reinforced Cauldron
- New: Speed Module. Can be used in an I.M.S., Protecto, Sentry, or Trophy System
- New: The Portable Radar can now be placed on all sides of a block and will rotate accordingly
- New: The Admin Tool now shows information about briefcases by holding the Briefcase in the off hand and the Admin Tool in the main hand and rightclicking
- Change: The information on how to look around inside a camera is now displayed in the camera's overlay instead of being sent to the chat
- Change: Messages sent due to changing a Sentry's mode will now show up above the hotbar to avoid spamming the chat
- Change: The Whitelist Module has been renamed to "Allowlist Module" to be more clear about its function
- Change: The Blacklist Module has been renamed to "Denylist Module" to be more clear about its function
- Change: Auto-assembling the Block Pocket no longer instantly builds the structure
- Change: The Redstone and Speed Modules now both use the resource pack's redstone/sugar texture
- API: Added hook for mods to define a block that can open Reinforced Doors/Reinforced Trapdoors/Reinforced Fence Gates. For more info, see IDoorActivator
- Fix: Double Crystal Quartz Slab does not drop two slab items
- Fix: An item stack cannot be put into the Universal Block Reinforcer's slots if the stack's count is larger than the Reinforcer's durability
- Fix: I.M.S. entity gets stuck in mid-air when leaving and rejoining the world
- Fix: Portable Radar does not respect the "respect_invisibility" config option
- Fix: I.M.S. attacks players in spectator mode
- Fix: The Briefcase's NBT contains empty Briefcase inventory slots
- Fix: Some of SecurityCraft's tools don't lose durability when used on a block while held in the offhand
- Fix: Reinforced Buttons and the Reinforced Lever can get washed away by water and destroyed by pistons
- Fix: Mines can be placed on some blocks that don't have a solid top side
- Fix: Claymore explosions ignore the "shouldSpawnFire" configuration option
- Fix: Using TAB to navigate menus does not work properly in some of SecurityCraft's interfaces
- Fix: Pressing ENTER to press buttons in SecurityCraft's interfaces does not work
- Fix: Placing a Scanner-/Keypad Door in a block space that is powered will place the door in an open state
- Fix: Universal Block Remover does not take damage when breaking a deactivated Cage Trap
- Fix: The message that the Codebreaker has been disabled in the configuration file doesn't get sent
- Fix: Some mines ignore the "smallerMinesExplosion" configuration option
- Fix: Placing a Sentry in replaceable blocks, such as grass, does not work
- Fix: Copying/Pasting module lists does not work properly
- Fix: Mobs can spawn inside of a block pocket
- Fix: Potential crash when placing down blocks next to a password-protected chest
- Fix: Laser-/Inventory Scanner Fields don't have the proper owner assigned
- Fix: Newly placed Laser Blocks/Inventory Scanners don't synchronize with the Laser Block/Inventory Scanner they connected to
- Fix: Rejoining a server/world while being mounted to a camera teleports the player to that camera
- Fix: The fourth I.M.S. Bomb doesn't get shot properly
- Fix: The SecurityCraft Manual does not properly reflect recipes that have been disabled via datapacks/3rd-party mods
- Fix: Sentry does not attack hostile mobs from Lycanites Mobs
- Fix: Rightclicking a block while attempting to change a Briefcase's owner does not change the owner
- Removed: Ability to take screenshots via middle mouse click while being mounted to a camera, as it is unneeded due to Minecraft's own screenshot feature
- Removed: All configuration values that disable recipes. Use datapacks or 3rd-party mods to disable recipes instead
- Removed: Version Checker integration (Forge's update checker is already being used instead)
- Misc.: All of SecurityCraft's blocks now have loot tables
- Misc.: Added data generators for more blockstates and models and renamed some textures and models along the way. This will break resourcepacks
- Misc.: The minimum required Forge version is now 36.0.42

--------------------------Changelog for v1.8.21.1 of SecurityCraft--------------------------

- Fix: Converting chests and furnaces to Password-protected Chests/Furnaces does not set the owner correctly

--------------------------Changelog for v1.8.21 of SecurityCraft--------------------------

**READ BEFORE UPDATING TO THIS VERSION:
1. Due to the new sentry modes, your sentries will not behave as in previous mod versions. Make sure to set them to the correct mode again, otherwise they will attack something you don't want to be attacked! Do note, that due to an additional fix regarding the Sentry, Sentries placed above water, lava, or other liquids will be removed and dropped as an item!
2. Your SecurityCraft config will reset! This is because the mod's configuration file has been split up into client and server configs, and some config options have been removed in favor of ingame per-block configuration (accessible by rightclicking the block with a Universal Block Modifier). Read the changelog below for details. If a configuration option is not mentioned, it has simply been moved to the server config. The server configuration can be found in the "serverconfig" folder within your world folder. The client configuration can still be found in the normal "config" folder. You can find your old configuration in the "config/securitycraft-common.toml" file.**

- New: Sentries now have more modes. They can now attack only players, only hostile mobs, or both, and can be either always active, camouflaged, or idle
- New: Hovering over a Sentry's name in the Sentry Remote Access Tool now shows the Sentry's position
- New: The Whitelist Module can now be used in the Scanner Door
- New: Adding a whitelist module to a Reinforced Hopper will allow whitelisted players to access the hopper and use it to extract items out of their own blocks
- New: Customization option to change how long the Retinal Scanner emits a redstone signal when it has been activated
- New: Customization option to change the player search radius of the Username Logger
- New: Customization option to change the length of the pause between alarm sounds
- New: Customization option to change the range of blocks in which the Claymore can be tripped
- New: Customization option to change the range of blocks in which the I.M.S. can find potential targets
- New: Briefcases can now be dyed the same way as leather armor
- New: Nameable SecurityCraft blocks and Sentries will keep the custom name of their item form when placed
- New: Customization option to change the time the Scanner Door will stay open before it closes again (set to 0 to disable)
- New: Keypad Door
- New: The Projector can now project horizontally
- New: The height of a Projector's projection can now be changed
- New: Customization option to allow Inventory Scanner fields to solidify when a prohibited item is detected
- New: Quark's wooden chests can now be converted to Password-protected Chests
- New: Wire Cutters can now deactivate a Cage Trap. Use Redstone to reactivate it
- New: Sounds when defusing/arming mines
- New: An offset can now be set before automatically building a Block Pocket, removing the restriction that the Block Pocket Manager has to be in the middle
- New: The Storage Module can now be used in the Block Pocket Manager to add an inventory for storing building materials for the Block Pocket
- Change: Reinforced Hoppers can no longer be accessed by anyone
- Change: The configuration option "sayThanksMessage" has been moved to the client configuration
- Change: The configuration option "cameraSpeed" has been moved to the client configuration
- Change: Some customizable options have been adapted to allow for finer control
- Change: Reinforced Iron Bars spawned by a Cage Trap no longer drop
- Change: If not in creative mode, auto-assembling the Block Pocket now only works if the proper building materials are provided in the Block Pocket Manager's inventory (accessible by adding a Storage Module)
- Change: Removing a Storage Module from an Inventory Scanner or Block Pocket Manager will now drop the contents of the block
- API: Added hook for mods to alter if the Sentry can attack their entities. For more info, see IAttackTargetCheck
- API: Added hook for mods to add a way to have their own blocks be convertible to password-protected variants. For more info, see IPasswordConvertible
- API: Inter mod communications related code has been moved to the SecurityCraftAPI class
- Fix: Sentry Remote Access Tool tooltip shows incorrectly
- Fix: Trophy Systems shoot bullets of their owner's sentries
- Fix: Modules sometimes do not get synched to a linked block (e.g. Laser Block, Inventory Scanner)
- Fix: Removing the block under a Sentry does not remove the Sentry in numerous cases
- Fix: Sentries target invulnerable entities
- Fix: Sentries can be placed above liquids
- Fix: Players in spectator mode can activate some blocks (e.g. Portable Radar)
- Fix: Fake liquids can sometimes be picked up when they shouldn't
- Fix: Some recipes don't get displayed correctly in the SecurityCraft Manual
- Fix: Rightclicking a block while attempting to remove a Briefcase's code does not remove the code
- Fix: Slider tooltips overlap sliders when changing their value (affects Projector, block customization, ...)
- Fix: Reinforced Light Gray Stained Glass/Stained Glass Panes/Terracotta have faulty recipes (Thanks shroomdog27!)
- Fix: The names of nameable SecurityCraft blocks cannot contain braces
- Fix: SecurityCraft's doors cannot be placed underwater
- Fix: Inventory Scanner does not drop stored items when broken
- Fix: Projector does not drop its contained block when broken
- Fix: Some text in the Inventory Scanner's GUI is not translated
- Fix: Some text in the Inventory Scanner's GUI may not display correctly when using certain languages
- Fix: Prohibited Items slots in the Inventory Scanner's GUI are overlapping
- Fix: Prohibited Items in Shulker Boxes get destroyed by the Inventory Scanner when a redstone module is equipped
- Fix: The custom name of Password-protected Chests doesn't show up at the top of the chest's screen
- Fix: The default name of the Password-protected Chest's screen doesn't get translated
- Fix: Large Password-protected Chests have the wrong label at the top of their screen
- Fix: Defusing/arming some mines does not decrease the durability of the wire cutters/flint and steel
- Fix: Defusing some mines decreases the durability of the wire cutters when in creative mode
- Fix: Attempting to defuse some mines despite them already being defused, decreases the durability of the wire cutters
- Fix: Size and show outline settings of a Block Pocket Manager don't get synchronized to other players
- Fix: Crash involving capabilities of Password-protected Chests/Furnaces
- Fix: Sliders (e.g. in the Projector or the customization screens) give no feedback when the player hovers their mouse over them
- Fix: Tile Entity data does not get synchronized properly in some cases
- Fix: SecurityCraft's Doors and the Reinforced Trapdoor are open when placed while powered by redstone
- Fix: Reinforced Doors sometimes drop an item when broken in Creative mode
- Fix: Crystal Quartz blocks drop when breaking them by hand
- Fix: The Block Pocket Manager can sometimes allow to activate block pockets that aren't built correctly
- Fix: Players mounted to cameras are able to break blocks and hit/interact with entities
- Fix: The camera's zoom can't be decreased when increased for too long (and vice versa)
- Fix: Switching cameras while being mounted to a camera does not work
- Fix: Bouncing Betty is invisible after being tripped
- Fix: The Intelligent Munition System cannot target players
- Removed: Configuration option "alarmSoundVolume"
- Removed: Configuration option "alarmTickDelay" This is now a per-block option
- Removed: Configuration option "claymoreRange" This is now a per-block option
- Removed: Configuration option "imsRange". This is now a per-block option
- Removed: Configuration option "motionActivatedLightSearchRadius". This is already a per-block option
- Removed: Configuration option "portableRadarDelay". This is already a per-block option
- Removed: Configuration option "portableRadarSearchRadius". This is already a per-block option
- Removed: Configuration option "usernameLoggerSearchRadius". This is now a per-block option
- Misc.: The messages sent on joining a world have been updated to remove outdated messages, improve existing ones, and add one notifying the user of an outdated mod version

--------------------------Changelog for v1.8.20.2 of SecurityCraft--------------------------

- Fix: Track Mine is see-through in certain cases
- Fix: Potential crash while starting the game
- Fix: Reinforced (Stained) Glass Panes and Reinforced Iron Bars do not connect to walls and vanilla panes

--------------------------Changelog for v1.8.20.1 of SecurityCraft--------------------------

- Fix: Crash when reinforcing blocks in the world

--------------------------Changelog for v1.8.20 of SecurityCraft--------------------------

- New: Reinforced Blocks: Crimson Nylium, Warped Nylium, Crimson Planks, Warped Planks, Crimson Stem, Warped Stem, Stripped Crimson Stem, Stripped Warped Stem, Stripped Crimson Hyphae, Stripped Warped Hyphae, Crimson Hyphae, Warped Hyphae, Crismon Slab, Warped Slab, Soul Soil, Basalt, Polished Basalt, Cracked Nether Bricks, Chiseled Nether Bricks, Crimson Stairs, Warped Stairs, Quartz Bricks, Warped Wart Block, Netherite Block, Crying Obsidian, Blackstone, Blackstone Slab, Blackstone Stairs, Polished Blackstone, Polished Blackstone Slab, Polished Blackstone Stairs, Polished Blackstone Bricks, Polished Blackstone Brick Slab, Polished Blackstone Brick Stairs, Chain, Blackstone Wall, Polished Blackstone Wall, Polished Blackstone Brick Wall, Soul Lantern, Shroomlight, Crimson Pressure Plate, Warped Pressure Plate, Polished Blackstone Pressure Plate, Crimson Button, Warped Button, Polished Blackstone Button (Thanks Redstone_Dubstep!)
- New: Reinforced Lantern (Thanks Redstone_Dubstep!)
- New: Ancient Debris Mine, Gilded Blackstone Mine, Nether Gold Ore Mine (Thanks Redstone_Dubstep!)
- New: Secret Crimson and Secret Warped Signs (Thanks Redstone_Dubstep!)
- New: Piglins now love Reinforced Gold Blocks (Thanks Redstone_Dubstep!)
- New: Striders now find Fake Lava comfortably warm (Thanks Redstone_Dubstep!)
- New: Reinforced Blocks are now immune to lava (Thanks Redstone_Dubstep!)
- New: Block/item tags: securitycraft:reinforced/crimson_stems, securitycraft:reinforced/nylium, securitycraft:reinforced/pressure_plates, securitycraft:reinforced/warped_stems (Thanks Redstone_Dubstep!)
- New: Support for the following block/item tags: minecraft:infiniburn_overworld, minecraft:mushroom_grow_block, minecraft:nylium, minecraft:pressure_plates, minecraft:soul_fire_base_blocks, minecraft:soul_speed_blocks, minecraft:strider_warm_blocks, minecraft:wither_summon_base_blocks, minecraft:piglin_loved (Thanks Redstone_Dubstep!)
- New: The name of a camera is now displayed in the top right when the player is mounted to it
- New: Inventory Scanner modifying option to have inventory scanner fields be horizontal
- New: A briefcase's owner can now be changed if its owner rightclicks while holding the briefcase in their off hand and a named Universal Owner Changer in their main hand (Thanks Redstone_Dubstep!)
- New: The codebreaker can now be used on a briefcase by holding the briefcase in the off hand and the codebreaker in the main hand and rightclicking (Thanks Redstone_Dubstep!)
- New: Customization option to change how long the Keycard Reader emits a redstone signal when it has been activated (Thanks Redstone_Dubstep!)
- New: Secret Signs can now have a whitelist (Thanks Redstone_Dubstep!)
- New: New customization option to make the text of Secret Signs visible to everyone (Thanks Redstone_Dubstep!)
- New: Modules can now also be added to a block by rightclicking them onto the block instead of using the Universal Block Modifier
- New: In inventories, block mines can now be distinguished from their vanilla counterparts
- Change: Sounds of reinforced blocks now match the sounds of their vanilla equivalent (Thanks Redstone_Dubstep!)
- Change: Inventory Scanner Fields now cannot be destroyed when between two Inventory Scanners (Thanks Redstone_Dubstep!)
- Change: Laser and Taser damage no longer bypasses armor
- Change: The Admin Tool now only works in creative mode
- Change: The inventory scanner's texture is now up to date with the new Minecraft textures
- Change: Instead of using a crafting table, a briefcase's code can now be reset if its owner rightclicks while holding the briefcase in their off hand and a Universal Key Changer in their main hand. (Thanks Redstone_Dubstep!)
- Change: Balanced Fake Liquids: Reduced damage of Fake Water by 70% and added regeneration effect to Fake Lava instead of healing instantly
- Change: The Panic Button now emits a light level of 4 when turned on
- Change: Just Enough Items now shows proper recipes for un-/reinforcing blocks - the old info screen has been removed in favor of this
- Change: The level 1 Universal Block Reinforcer can no longer unreinforce blocks. At least level 2 is required for this now
- Change: The codebreaker is now 100% effective when using in creative mode
- Change: Reinforced Carpets can no longer be used as fuel
- API: Added hook for mods to define blocks that can extract from Password-protected Chests/Furnaces. For more info, see IExtractionBlock
- Fix: Taser does not reliably hit entities (Thanks Redstone_Dubstep!)
- Fix: Camera overlay's time and hotkey texts sometimes get obscured or render partly offscreen
- Fix: Walking through a block pocket wall blocks vision
- Fix: Anyone can reset a briefcase's passcode (Thanks Redstone_Dubstep!)
- Fix: Confirm button in the Universal Key Changer's GUI doesn't properly react to changes in the textboxes (Thanks Redstone_Dubstep!)
- Fix: Defused Claymore model is incorrect (Thanks Redstone_Dubstep!)
- Fix: Keycard Reader sometimes sends incorrect messages (Thanks Redstone_Dubstep!)
- Fix: Tooltips in SecurityCraft Manual don't completely show on the screen
- Fix: Secret Signs can be colored by anyone (Thanks Redstone_Dubstep!)
- Fix: Reinforced Hopper does not drop
- Fix: Redstone cannot be placed on Reinforced Hoppers
- Fix: Fire doesn't get removed after exiting Fake Lava
- Fix: Reinforced Doors and Scanner Doors cannot be placed by rightclick the side of a block
- Fix: Reinforced Doors and Scanner Doors can remove blocks
- Fix: Crashes
- Fix: Transparent pixels in Codebreaker and Sentry Remote Access Tool textures
- Fix: Panic button has no sound
- Fix: Sliders in GUIs sometimes don't stop sliding after the mouse button has been released
- Fix: Universal Block Reinforcer dupe
- Fix: Pipe interaction with Password-protected Chest/Furnace
- Fix: Portable Radar sometimes doesn't turn off the redstone signal when it's supposed to
- Fix: Rotating/mirroring some blocks (e.g. using mods like World Edit) does not work correctly
- Fix: Sentry bullets don't disappear upon hitting a block
- Fix: Vanilla redstone power sources can still activate Reinforced Doors and similar in specific cases
- Fix: Reinforcing a hopper in the world drops its contents
- Fix: Disguised blocks don't get tinted correctly when the reinforced_block_tint configuration option is turned off
- Removed: Taser Bullet entity
- Removed: Fix for Cyclic's Sack of Holding (Cyclic has a fix in place itself)
- Potential Fix: First Alarm sound sometimes does not play
- Misc.: Various French language fixes (Thanks supercat95!)
- Misc.: The minimum required Forge version is now 34.0.3

--------------------------Changelog for v1.8.19.3 of SecurityCraft--------------------------

- Fix: Harming Module recipe doesn't work
- Fix: Incompatibility with Forge 33.0.22+

--------------------------Changelog for v1.8.19.2 of SecurityCraft--------------------------

- Fix: Incompatibility with Forge 33.0.10+
- Fix: WAILA exploit
- Fix: Reinforced double stone slab is not tinted (Thanks Redstone_Dubstep!)
- Fix: Incorrect item group name

--------------------------Changelog for v1.8.19.1 of SecurityCraft (since beta2)--------------------------

- Fix: Server crash with disguised blocks

--------------------------Changelog for v1.8.19.1-beta2 of SecurityCraft--------------------------

- Misc.: Re-enable fix for Cyclic's Sack of Holding
- Misc.: Re-enable HWYLA integration

--------------------------Changelog for v1.8.19.1-beta1 of SecurityCraft--------------------------

- Fix: Welcome message doesn't show
- Fix: Stuttering when quickly reinforcing blocks placed in the world
- Misc.: The minimum required Forge version is 32.0.67