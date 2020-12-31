--------------------------Changelog for v1.8.21 of SecurityCraft--------------------------

**Disclaimer:
1. Due to the new sentry modes, your sentries will not behave as in the previous mod version. Make sure to set them to the correct mode again, otherwise they will attack something you don't want to be attacked! Do note, that due to an additional fix regarding the Sentry, Sentries placed above water, lava, or other liquids will be removed and dropped as an item!
2. Your SecurityCraft config will reset! This is because the mod's configuration file has been split up into client and server configs, and some config options have been removed in favor of ingame per-block configuration (accessible by rightclicking the block with a Universal Block Modifier). Read the changelog below for details. If a configuration option is not mentioned, it has simply been moved to the server config. The server configuration can be found in the "serverconfig" folder within your world folder. The client configuration can still be found in the normal "config" folder.**

- New: Sentries now have more modes. They can now attack only players, only hostile mobs, or both, and can be either always active, camouflaged, or idle
- New: Hovering over a Sentry's name in the Sentry Remote Access Tool now shows the Sentry's position
- New: The Whitelist Module can now be used in the Scanner Door
- New: Adding a whitelist module to a Reinforced Hopper will allow whitelisted players to access the hopper and use it to extract items out of their own blocks
- New: Customization option to change how long the Retinal Scanner emits a redstone signal when it has been activated
- Change: Reinforced Hoppers can no longer be accessed by anyone
- Change: The configuration option "sayThanksMessage" has been moved to the client configuration
- Change: The configuration option "cameraSpeed" has been moved to the client configuration
- Fix: Trophy Systems shoot bullets of their owner's sentries
- Fix: Modules sometimes do not get synched to a linked block (e.g. Laser Block, Inventory Scanner)
- Fix: Removing the block under a Sentry does not remove the Sentry in numerous cases
- Fix: Sentries target invulnerable entities
- Fix: Sentries can be placed above liquids
- Fix: Players in spectator mode can activate some blocks (e.g. Portable Radar)
- Fix: Some recipes don't get displayed correctly in the SecurityCraft Manual
- Fix: Rightclicking a block while attempting to remove a Briefcase's code does not remove the code

--------------------------Changelog for v1.8.20.2 of SecurityCraft--------------------------

- Fix: Potential crash while starting the game

--------------------------Changelog for v1.8.20.1 of SecurityCraft--------------------------

- Fix: Crash when reinforcing blocks in the world

--------------------------Changelog for v1.8.20 of SecurityCraft--------------------------

- New: Reinforced Lantern (Thanks Redstone_Dubstep!)
- New: The name of a camera is now displayed in the top right when the player is mounted to it
- New: Inventory Scanner modifying option to have inventory scanner fields be horizontal
- New: A briefcase's owner can now be changed if its owner rightclicks while holding the briefcase in their off hand and a named Universal Owner Changer in their main hand (Thanks Redstone_Dubstep!)
- New: The codebreaker can now be used on a briefcase by holding the briefcase in the off hand and the codebreaker in the main hand and rightclicking (Thanks Redstone_Dubstep!)
- New: Customization option to change how long the Keycard Reader emits a redstone signal when it has been activated (Thanks Redstone_Dubstep!)
- New: Secret Signs can now have a whitelist (Thanks Redstone_Dubstep!)
- New: New customization option to make the text of Secret Signs visible to everyone (Thanks Redstone_Dubstep!)
- New: Modules can now also be added to a block by rightclicking them onto the block instead of using the Universal Block Modifier
- New: In inventories, block mines can now be distinguished from their vanilla counterparts
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
- API: Added hook for mods to define blocks that can extract from Password-protected Chests/Furnaces. For more info, see IExtractionBlock
- Fix: WAILA exploit
- Fix: Double Stone Slab has no tint (Thanks Redstone_Dubstep!)
- Fix: Taser does not reliably hit entities (Thanks Redstone_Dubstep!)
- Fix: Crash on SC Manual pages with subpages, when using a language that does not use spaces to seperate words (Thanks jihuayu!)
- Fix: Camera overlay's time and hotkey texts sometimes get obscured or render partly offscreen
- Fix: Welcome message doesn't show
- Fix: Anyone can reset a briefcase's passcode (Thanks Redstone_Dubstep!)
- Fix: Confirm button in the Universal Key Changer's GUI doesn't properly react to changes in the textboxes (Thanks Redstone_Dubstep!)
- Fix: Defused Claymore model is incorrect (Thanks Redstone_Dubstep!)
- Fix: Keycard Reader sometimes sends incorrect messages (Thanks Redstone_Dubstep!)
- Fix: Secret Signs can be colored by anyone (Thanks Redstone_Dubstep!)
- Fix: Reinforced Hopper does not drop
- Fix: Fire doesn't get removed after exiting Fake Lava
- Fix: Reinforced Doors and Scanner Doors cannot be placed by rightclick the side of a block
- Fix: Reinforced Doors and Scanner Doors can remove blocks
- Fix: Crashes
- Fix: Transparent pixels in Codebreaker and Sentry Remote Access Tool textures
- Fix: Panic button has no sound
- Fix: Sliders in GUIs sometimes don't stop sliding after the mouse button has been released
- Fix: Universal Block Reinforcer dupe
- Fix: Pipe interaction with Password-protected Chest/Furnace
- Fix: Projector is missing its English translation
- Fix: Admin Tool has to be sneak-rightclicked in order to show information
- Fix: Portable Radar sometimes doesn't turn off the redstone signal when it's supposed to
- Fix: Rotating/mirroring some blocks (e.g. using mods like World Edit) does not work correctly
- Fix: Sentry bullets don't disappear upon hitting a block
- Fix: Vanilla redstone power sources can still activate Reinforced Doors and similar in specific cases
- Fix: Reinforcing a hopper in the world drops its contents
- Fix: Disguised blocks don't get tinted correctly when the reinforced_block_tint configuration option is turned off
- Fix: Reinforced Grass Block has a faulty model
- Removed: Taser Bullet entity
- Potential Fix: First Alarm sound sometimes does not play
- Misc.: Various French language fixes (Thanks supercat95!)

--------------------------Changelog for v1.8.19.1 of SecurityCraft--------------------------

- Fix: Tooltips of modules show a header even if no blocks/items/players have been added (Thanks Redstone_Dubstep!)
- Fix: Fake liquids can sometimes be picked up when they shouldn't
- Fix: Projector doesn't sync on servers

--------------------------Changelog for v1.8.19 of SecurityCraft--------------------------

**DISCLAIMER: The backend of the module system has been rewritten. Despite thoroughly testing it, there might still be issues we missed. Always backup your world before updating! If you find issues, please report them to us.**

- New: The Reinforced Hopper is now the only block that can pull out of Password-protected Chests and Furnaces. The owner of both blocks needs to be the same, or the hopper needs to be owned by a whitelisted player
- New: White-/Blacklist Modules now work for the Password-protected Chest and Furnace (including customization options to turn off the messages)
- New: Stonecutting recipes for numerous reinforced blocks (Thanks Redstone_Dubstep!)
- New: Players in creative can now remove any sentry and not just their own
- New: The Block Pocket can now be built automatically from within the Block Pocket Manager's GUI (Thanks Redstone_Dubstep!)
- New: Projector (Projects a fake wall of a block of your choice into the world)
- Change: Disguising a sentry will now respect previously placed blocks at the sentry's position (Thanks Redstone_Dubstep!)
- Change: Improved the Block Pocker Manager's GUI (Thanks Redstone_Dubstep!)
- API: New IModuleInventory interface that adds the ability to have modules in a TileEntity
- API: New ICustomizable interface that adds the ability to have customization options in a TileEntity
- Fix: Trophy System does not attack shulker/sentry bullets and all kinds of arrows
- Fix: Removing a Redstone Module from an active Portable Radar does not update the redstone output
- Fix: Whitelist Module does not work in the Keycard Reader
- Fix: Placing a block on an active Keypad plays no sound
- Fix: Potential crash when clicking the Clear List button in the Username Logger's GUI
- Fix: Trophy System beam is not always visible
- Fix: The customization GUI for Block Pocket blocks can be opened despite there being no functions
- Fix: Modules drop twice when breaking a block containing modules in creative mode
- Fix: Modules can duplicate when loading chunks
- Fix: Hoppers etc. can extract from the fake slots of an Inventory Scanner
- Fix: Username Logger crash
- Fix: Block Pocket Managers and Block Pocket Walls do not drop (Thanks Redstone_Dubstep!)
- Fix: Security Camera still powers blocks when removing the Redstone Module
- Fix: Modules don't get synched correctly on servers

--------------------------Changelog for v1.8.18.1 of SecurityCraft--------------------------

- API: Option is now abstract and some methods have been changed around for cleaner code
- Fix: Reinforced Levers etc. don't reliably open Reinforced Iron Doors etc. when powering a block next to them
- Fix: New customization options don't correctly load for existing blocks
- Fix: Retinal Scanner crash
- Fix: Camera dis-/mounting doesn't work correctly

--------------------------Changelog for v1.8.18 of SecurityCraft--------------------------

**DISCLAIMER: The Password-protected Furnace has been rewritten in this update. Please be aware that older Password-protected Furnaces may break. Backup your world before updating!**

- New: More reinforced block recipes for parity with vanilla (Thanks Redstone_Dubstep!)
- New: Fake Water/Lava Buckets can now be properly used in dispensers
- New: All reinforced blocks are now immune to the ender dragon and the wither (added them to the dragon_immune and wither_immune block tags)
- New: Trophy System now destroys shulker bullets, dragon fireballs and wither skulls (Thanks Redstone_Dubstep!)
- New: Reinforced Redstone Lamp
- New: Reinforced Blue Ice (Thanks Redstone_Dubstep!)
- New: Reinforced Cobblestone Wall, Reinforced Mossy Cobblestone Wall
- New: Reinforced Blocks: Brick Wall, Prismarine Wall, Red Sandstone Wall, Mossy Stone Brick Wall, Granite Wall, Stone Brick Wall, Nether Brick Wall, Andesite Wall, Red Nether Brick Wall, Sandstone Wall, End Stone Brick Wall, Diorite Wall
- New: Reinforced Blocks: Hopper, Observer, Buttons, Lever, Grass Block, Grass Path, Coarse Dirt, Podzol, Ice, Snow Block, Clay, Mycelium, Packed Ice, Nether Wart Block, Cobweb (Thanks Redstone_Dubstep!)
- New: Block mines for Coal Ore, Emerald Ore, Gold Ore, Iron Ore, Lapis Lazuli Ore, Nether Quartz Ore, and Redstone Ore
- New: Configuration option to turn off the darker textures of reinforced blocks
- New: Korean translation (Thanks mindy15963!)
- New: Pressing "Add", "Remove" or "Clear" in the Whitelist/Blacklist module screen clears the textbox (Thanks Redstone_Dubstep!)
- New: The buttons in the Whitelist/Blacklist module screen activate and deactivate depending on the text field input and the amount of stored players (Thanks Redstone_Dubstep!)
- New: HWYLA and TOP now show a Sentry's owner, its current mode, and the equipped modules (Thanks Redstone_Dubstep!)
- New: Config option to turn off the recipes of all SecurityCraft explosives
- New: Option in the Universal Block Modifier GUI of the Keypad and Keycard Reader to turn off messages for whitelisted/blacklisted players
- New: Added relevant blocks to the bamboo_plantable_on, impermeable, and doors block tags
- New: Configuration option to turn off showing the owner's face on a retinal scanner
- New: Customization option to change how long the Keypad emits a redstone signal when the correct code has been inserted
- Change: Bouncing Betty now jumps as high as the player's head (Thanks Redstone_Dubstep!)
- Change: Zooming when viewing a Security Camera is no longer restriced to only three zoom levels
- Change: Creepers, Ocelots, and Endermen can now trigger Mines
- Change: Some reinforced block models are now randomly rotated (Thanks Redstone_Dubstep!)
- Change: White-/Blacklist Modules can now hold up to 50 players
- Change: Reinforced Doors, Fence gates and trapdoors have a greater range to detect nearby active reinforced blocks (Thanks Redstone_Dubstep!)
- Change: Sentries now attack flying mobs (except bats), Slimes, Magma Slimes, Shulkers and the Ender Dragon (Thanks Redstone_Dubstep!)
- Change: Crystal Quartz Texture (Thanks Redstone_Dubstep!)
- Change: The Protecto can now attack charged creepers and zombie pigmen
- Change: Overhauled the inventory scanner. You can now choose what it does by inserting a storage module, redstone module, or both
- Change: /sc help no longer gives a free SecurityCraft Manual
- Fix: Crash when disguising a block with itself
- Fix: Localization for camera keybindings is incorrect
- Fix: Incorrect laser removal in creative mode
- Fix: Reinforced Glowstone and Reinforced Sea Lantern don't give off light (Thanks Redstone_Dubstep!)
- Fix: German language fixes (Thanks Redstone_Dubstep!)
- Fix: Incorrect version check with VersionChecker mod integration
- Fix: Cage Trap collision is incorrect
- Fix: Inventory Scanners can connect to Inventory Scanners by different owners
- Fix: Crash when pressing backspace in password screens and the textbox is empty
- Fix: Password setup screen displays long block names incorrectly
- Fix: Night vision icon does not show correctly in Security Camera overlay
- Fix: Bouncing Betty doesn't play a sound when triggered (Thanks Redstone_Dubstep!)
- Fix: Block Mines let items and the owner go through (Thanks Redstone_Dubstep!)
- Fix: Trophy System destroys projectiles even if they have already been removed (Thanks Redstone_Dubstep!)
- Fix: Textures of Universal Block Modifier, Universal Owner Changer and the Mine Remote Access Tool are transparent at the sides (Thanks Redstone_Dubstep!)
- Fix: Step sounds of disguised blocks are wrong
- Fix: Camera Monitor tooltip shows amount of first X bound cameras instead of amount of all bound cameras
- Fix: Mines and Portable Radar can exist without a support block (Thanks Redstone_Dubstep!)
- Fix: Claymore spawns too many particles when being broken (Thanks Redstone_Dubstep!)
- Fix: Claymore has no collision (Thanks Redstone_Dubstep!)
- Fix: Giving keycards via commands (e.g. /give @p securitycraft:keycard_lv1) hangs the game
- Fix: Backspace does not work in Universal Key Changer's GUI
- Fix: Crash when breaking the floor/ceiling of an activated Block Pocket
- Fix: Keycard Readers can be set up by players other than the owner
- Fix: Occasional Retinal Scanner crash
- Fix: Crash when disguising block with specific other blocks
- Fix: Reinforced Stairs can be destroyed by explosions
- Fix: Config option "mineExplodesWhenInCreative" does not work for block mines
- Fix: Backspace does not work in Briefcase setup screen
- Fix: When inserting a password, adding more characters after the limit has been reached results in having to press backspace more times than needed
- Fix: Pick Block on Sentry doesn't work (Thanks Redstone_Dubstep!)
- Fix: Crystal Quartz Block and its variants don't drop themselves when mined (Thanks Redstone_Dubstep!)
- Fix: Sentry Bullets can go through walls (Thanks Redstone_Dubstep!)
- Fix: Destroying an active SecurityCraft block while a reinforced door is open keeps the door open (Thanks Redstone_Dubstep!)
- Fix: Sentries shoot at targets that are in their death animation (Thanks Redstone_Dubstep!)
- Fix: The wooden reinforced pressure plates don't have a whitelist module description (Thanks Redstone_Dubstep!)
- Fix: Camera Monitor crash
- Fix: Hoppers/pipes/etc. can extract items out of a password-protected chest/furnace
- Fix: Password-protected Furnace does not work at all
- Fix: Lightning spawned by the Protecto does not show up
- Fix: The Protecto can attack Sentries
- Fix: Shift-clicking in customization gui does not work properly
- Fix: WAILA/TOP/Admin Tool show english module names instead of the translated counterpart
- Fix: Auth server related issue due to Retinal Scanner requests
- Fix: The Taser can be enchanted
- Fix: Message when changing a Sentry's owner gets sent twice
- Fix: Changing the owner of a Sentry does not correctly set the owner/uuid
- Fix: White-/Blacklist message of Keycard Reader getting sent twice
- Fix: The Scanner Door and Reinforced Iron Door have no place sound
- Fix: Can use other items than a Camera Monitor when mounted to a Security Camera
- Fix: Placing a Scanner Door or Reinforced Iron Door plays no hand animation
- Misc.: Several language file improvements, mainly French (Thanks supercat95!)

--------------------------Changelog for v1.8.17 of SecurityCraft--------------------------

- New: The Universal Block Reinforcer can now "unreinforce" blocks. Place items in the top slot to reinforce them, use the bottom slot instead for "unreinforcing" them
- New: Reinforced Cut Sandstone Slab and Reinforced Cut Red Sandstone Slab
- New: The Retinal Scanner now renders its owner's face (Thanks LorenaGdL!)
- New: The Username Logger now displays the UUID of logged players and the time they were logged at to its owner. Click an entry to copy the UUID
- New: Fire on Reinforced Netherrack now doesn't get removed
- New: The Mine-/Sentry Remote Access Tool GUI now shows explanatory tooltips when hovering over buttons
- New: The Mine-/Sentry Remote Access Tool now show whether a mine/sentry the player is looking at is bound to them on the hotbar (just like the Camera Monitor)
- New: Spanish translation (Thanks Ryo567!)
- New: Configuration option to account for invisibility (Sentries, Inventory Scanners, Lasers, etc. won't detect entities if they're invisible, config option is off by default)
- Change: The Username Logger now displays logged users in a scrollable list
- Change: The floor of Block Pockets is now solid (reactivate Block Pockets to fix this for already existing ones)
- Change: Slightly improve Reinforced (Stained) Glass textures
- Change: Textboxes when setting up/inserting/changing passwords are now automatically focused
- Change: Laser Blocks now create connections more easily
- Change: The SC Manual now displays pages alphabetically
- Change: You can now use boats in fake water
- Fix: Some disguised blocks do not give off power even when they should
- Fix: Lasers don't get removed correctly (Thanks Redstone_Dubstep!)
- Fix: Motion Activated Light activates when a player is mounted to a camera in its range
- Fix: Being in fake lava creates a hurting sound
- Fix: Entities keep burning when exiting fake lava	
- Fix: Backspace button does not work when setting up/inputting a password
- Fix: Throwing items into a Bouncing Betty crashes the game
- Fix: Some mines drop when exploding (Thanks LorenaGdL!)
- Fix: Camera overlay is incorrect when only night vision is activated
- Fix: Track Mines cannot be defused/armed with Wire Cutters/Flint and Steel
- Fix: Electrified Iron Fence Gates lose their owner when being opened
- Fix: Electrified Iron Fence Gates cannot be opened by reinforced pressure plates
- Fix: Feedback message is missing when changing the owner of a Sentry
- Fix: It's possible to reinforce more items than the universal block reinforcer has durability for
- Fix: Stairs and slabs lose their rotation/type when being reinforced
- Fix: Rare crash on load
- Fix: Codebreaker loses damage even when rightclicking blocks that are not password-protected
- Fix: Reinforced (Stained) Glass sides are visible through the same type of Reinforced (Stained) Glass
- Fix: Reinforced (Stained) Glass throws a shadow
- Fix: Dropped items get deleted in fake water
- Fix: Camera does not render in third person view when being mounted to one
- Fix: Clicking in GUIs doesn't work when viewing a camera
- Fix: It's possible to remove or otherwise interact with a camera when being mounted to it
- Fix: Username Logger can log the same player twice at the same time
- Fix: Block mines don't have the same hardness as their vanilla counterpart
- Fix: Password-protected Chests cannot be opened when a half-slab or similar is placed above them
- Fix: Some sounds don't become quieter when moving away from them
- Fix: Mine-/Sentry Remote Access Tools lose their mines/sentries when rightclicking the tool out of range
- Fix: Various (red) sandstone variants cannot be reliably reinforced
- Fix: Some (universal) tools can identify disguised blocks and block mines
- Fix: Sentry Remote Access Tool GUI pauses the game
- Fix: Keycard Reader does not send an error message when using an incorrect keycard with "equal to or higher than" mode
- Fix: Double Slabs can be created by people who don't own the single slab
- Potential Fix: ConcurrentModificationException when saving a linkable tile entity
- Misc.: The minimum required Forge version is now 28.1.115
- Internal: Now using deferred registers for block/item/fluid registration

--------------------------Changelog for v1.8.16 of SecurityCraft--------------------------

- New: The Retinal Scanner and the Scanner Door now have an option to turn off the "Hello" message. Available by rightclicking them with a Universal Block Modifier
- New: The Alarm now has an option to set the range of blocks that it can be heard in (0-100)
- New: The Cage Trap can now be disguised
- New: The SC Manual now shows available options and modules for customizable blocks
- New: French translation (Thanks marminot!)
- Change: Additionally to sneak-rightclicking, sentries can now also be removed using the Universal Block Remover
- Change: The powered taser now inflicts one heart of damage instead of half a heart
- Fix: JEI error on startup
- Fix: Players other than the one mounted to a camera can take screenshots or otherwhise interact with the camera
- Fix: Players that don't own an Inventory Scanner can still take out its items
- Fix: Break animations
- Fix: Reinforced Doors and Scanner Doors can be broken using pistons
- Fix: Block Pocket Wall's sides are visible through other Block Pocket Walls
- Fix: Activating a Cage Trap can delete blocks that shouldn't be deleted
- Fix: Sentry Remote Access Tool tooltip does not show sentry names
- Fix: Inventory Scanner removes its owner's items
- Fix: Laser harms its owner if the harming module is installed
- Fix: Trophy System does not target arrows shot from dispensers
- Fix: SC Manual does not show tooltips for some blocks/items
- Fix: Copied module lists can only be pasted once
- Fix: Key names are not localized properly in camera UI
- Fix: Reinforced Stairs cannot be placed as expected
- Fix: Reinforced Slabs cannot be placed as expected
- Fix: Various language fixes and updates (Thanks supercat95 and Redstone_Dubstep!)
- Fix: Removing a laser (block) may sometimes break lasers that shouldn't be broken
- Fix: Various mines do not ignore their owner
- Fix: Sentry can be placed in the same block space as other blocks
- Fix: Reinforced Double Slabs don't drop two slabs when being broken
- Fix: Breaking a Laser Block/Inventory Scanner keeps modules in connected Laser Blocks/Inventory Scanners
- Fix: Crash when summoning sentry using commands (Thanks LorenaGdL!)
- Potential Fix: Block Pocket still loses its owner when reloading world/server
- Misc.: The minimum required Forge version is now 28.1.115

--------------------------Changelog for v1.8.15 of SecurityCraft--------------------------

- New: Reinforced Prismarine, Reinforced Prismarine Bricks, Reinforced Dark Prismarine and Reinforced Sea Lanterns can now be used for building a conduit
- New: Reinforced Bookshelf (Can also be used for an enchanting table)
- New: If a Briefcase has been renamed, it will now show that name in the inventory GUI
- New: Reinforced Obsidian can now be used to create a nether portal
- New: Sentry Remote Access Tool to remotely control sentries, analogously to the Mine Remote Access Tool (Thanks LorenaGdL!)
- New: The following blocks can now be disguised with the Disguise Module, similar to the Keypad: Inventory Scanner, Keycard Reader, Laser Block, Retinal Scanner, Username Logger
- New: Sentries can now be named. Names show up in the Sentry Remote Access Tool
- New: The Universal Owner Changer now works for sentries
- New: I.M.S. can now target only hostile mobs
- New: Page indicators for SecurityCraft Manual
- New: Reinforced Redstone Block
- Change: Rightclicking a double chest with a Key Panel will now convert the double chest instead of just a single chest
- Change: Lasers now look more like lasers
- Change: Inventory Scanner Fields now look more like Inventory Scanner Fields (Thanks LorenaGdL!)
- Change: More recipes now use reinforced blocks instead of their vanilla equivalent
- Change: The Sentry no longer needs 4 blocks around it to be placeable
- Change: The Cage Trap now has a ceiling to properly prevent players from escaping (Thanks LorenaGdL!)
- Fix: Laser and Inventory Scanner Field's name is not localized
- Fix: Inventory Scanner does not check for prohibited items in armor and offhand slots
- Fix: Crash when the Trophy System tries to destroy a sentry's bullet
- Fix: Occasional crash when right-clicking the Camera Monitor
- Fix: Keypad blacklist does not work
- Fix: Disguised Keypad does not give off power when activated (Thanks Redstone_Dubstep!)
- Fix: Config option to disable fire from mine explosions does not work
- Fix: Config option to disable mines from exploding when being broken in creative does not work
- Fix: Key Binding names are shown incorrectly in the controls menu
- Fix: Trophy System crash
- Fix: Public Gui Announcement compatibility does not work
- Fix: Sentry head does not show when rejoining world/dimension if it was previously showing (Thanks LorenaGdL!)
- Fix: Cage Trap uses stone instead of metal sounds
- Fix: I.M.S. does not emit a sound when launching one of its mines
- Fix: I.M.S. targets all mobs instead of just hostile mobs
- Fix: SecurityCraft Manual's subpage navigation buttons appear even when there are no subpages
- Fix: Username Logger logs its owner
- Fix: Reinforced Stairs can be destroyed using TNT
- Fix: Gap between Reinforced Iron Bars when activating Cage Trap (Thanks LorenaGdL!)
- Fix: Rotation slider in Security Camera's customization GUI displays its value twice
- Fix: Laser Blocks can connect to other Laser Blocks that don't have the same owner
- Fix: Recipes using tags don't correctly show in the manual
- Fix: Sentry kick/crash
- Fix: Module duplication
- Fix: Trophy System does not drop
- Fix: I.M.S. bomb does not show up
- Fix: Message about cage trap having trapped someone gets sent to the one being trapped and not the cage trap owner (Thanks LorenaGdL!)
- Fix: HWYLA doesn't show information of disguised blocks correctly
- Misc.: The minimum required Forge version is now 28.1.91
- Removed: Unused config options

--------------------------Changelog for v1.8.14.1 of SecurityCraft--------------------------

- Fix: Universal Block Reinforcer adds an unnecessary NBT to the reinforced block
- Fix: Reinforced Slabs aren't being placed properly when placing against the side of a block
- Fix: Crash when opening customizing GUI of blocks that can't take modules
- Fix: In the customizing GUI, JEI moves items out of the way without needing to
- Misc.: Added another safeguard against alarm crash

--------------------------Changelog for v1.8.14 of SecurityCraft--------------------------

- New: Reinforced Glass Panes can now be crafted using six Reinforced Glass
- New: Reinforced Stained Glass Panes can now be crafted by surrounding a dye with eight Reinforced Glass Panes
- New: All Reinforced Terracotta blocks can now be crafted analogous to their vanilla counterparts
- New: Block/Item Tags
- New: Recipes now use block/item tags wherever possible
- New: Reinforced Red Sand
- New: Reinforced versions of all types of wooden pressure plates
- New: The Block Pocket now tells the player when it was successfully de-/activated
- Change: The Reinforced Iron Trapdoor is now created by putting a regular Iron Trapdoor into a Universal Block Reinforcer
- Change: The Frame recipe is slightly different, ingredients stay the same
- Change: The Sentry now uses Reinforced Blocks of Iron in its recipe
- Change: Fake Water/Lava Buckets now use the 1.14 texture
- Fix: Username Logger crash involving armor stands
- Fix: Reinforced Diorite and Reinforced Andesite use vanilla Cobblestone instead of Reinforced Cobblestone
- Fix: Using six Reinforced Stained Glass to craft Reinforced Stained Glass Panes yields 8 glass panes instead of 16
- Fix: Incorrect Taser is showing up in the creative tab (again)
- Fix: Fake Liquid Buckets can't be crafted using level two potions
- Fix: Chests and Furnaces cannot be converted to their password-protected variant when not sneak-rightclicking
- Fix: Reinforced Doors and Scanner Doors do not drop when breaking the top half or the block below them
- Fix: Disguised Keypad collision does not completely respect the collision of the block it's disguised as
- Fix: Disguised Keypad throws a shadow no matter what it's disguised as (ambient occlusion)
- Fix: Block Pocket Description incorrectly mentions Reinforced Quartz instead of Reinforced Crystal Quartz
- Fix: Cannot walk through non-see-through Block Pocket Walls
- Fix: Entities do not get healed when standing in fake lava
- Potential Fix: Block Pocket loses its owner when the player reloads the world

--------------------------Changelog for v1.8.13.1 of SecurityCraft--------------------------

- Fix: Disguising a Keypad does not synchronize to other players
- Fix: Sentry animation is not synchronized between players
- Fix: Disguised Keypad doesn't show reinforced blocks correctly
- Fix: Sentry whitelist does not work correctly

--------------------------Changelog for v1.8.13 of SecurityCraft--------------------------

- New: Italian translation (Thanks Chexet48!)
- New: Trophy System (Inspired from https://callofduty.fandom.com/wiki/Trophy_System)
- New: Block Pocket (Designed by Henzoid)
- New: The Whitelist Module can now be used with the Sentry
- New: Button to clear the Username Logger logged players list
- New: Reinforced Stone Pressure Plate (Only the owner and whitelisted users can press it, can be used to open Reinforced Iron (Trap-)Doors)
- New: The Smart Module now works for Security Cameras, enabling others to view cameras at a fixed angle
- New: Finnish translation (Thanks erland!)
- New: Reinforced blocks
	- Stripped Oak Log, Stripped Spruce Log, Stripped Birch Log, Stripped Jungle Log, Stripped Acacia Log, Stripped Dark Oak Log, Stripped Oak Wood, Stripped Spruce Wood, Stripped Birch Wood, Stripped Jungle Wood, Stripped Acacia Wood, Stripped Dark Oak Wood, Oak Wood, Spruce Wood, Birch Wood, Jungle Wood, Acacia Wood, Dark Oak Wood, Prismarine Slab, Prismarine Brick Slab, Dark Prismarine Slab, Smooth Quartz, Smooth Red Sandstone, Smooth Sandstone, Smooth Stone, Prismarine Stairs, Prismarine Brick Stairs, Dark Prismarine Stairs, Polished Granite Stairs, Smooth Red Sandstone Stairs, Mossy Stone Brick Stairs, Polished Diorite Stairs, Mossy Cobblestone Stairs, End Stone Brick Stairs, Smooth Sandstone Stairs, Smooth Quartz Stairs, Granite Stairs, Andesite Stairs, Red Nether Brick Stairs, Polished Andesite Stairs, Diorite Stairs, Polished Granite Slab, Smooth Red Sandstone Slab, Mossy Stone Brick Slab, Polished Diorite Slab, Mossy Cobblestone Slab, End Stone Brick Slab, Smooth Sandstone Slab, Smooth Quartz Slab, Granite Slab, Andesite Slab, Red Nether Brick Slab, Polished Andesite Slab, Diorite Slab
- New: New Secret Sign types (spruce, birch, jungle, acacia, dark oak)
- New: Secret Sign text can now be dyed
- New: Support for Public Gui Announcement
- Change: The Motion Activated Light is now triggered by mobs as well (This change includes tweaks to the attack logic that may impact other blocks, like the Portable Radar or the I.M.S., as well)
- Change: New Secret Sign textures
- Fix: Inventory Scanner ignores contents of Shulker Boxes
- Fix: Portable Radar does not update redstone signal correctly when Redstone Module is installed
- Fix: Installed modules do not drop when destroying block with Universal Block Remover
- Fix: Misc. crashes
- Fix: Taser can be duplicated using offhand slot (Thanks Boreaus!)
- Fix: Incorrect version of the taser shows up in the creative tab and JEI
- Fix: Admin Tool recipe description does not show in JEI
- Fix: Cannot interact with UI elements when mounted to a camera
- Fix: Pressing the inventory key while editing a whitelist/blacklist module closes the GUI
- Fix: Disguising a keypad does not work with Forge 25.0.193+
- Fix: Password-protected Chest is not resistant to explosions and can be broken instantly
- Fix: The Admin Tool's description in JEI is not working
- Fix: Model of activated alarm is incorrect
- Fix: Electrified Iron Fence loses its owner when placing specific blocks next to it
- Fix: Reinforced Door does not behave correctly
- Fix: Briefcase does not save items correctly
- Fix: Some sounds play server-wide instead of just for the players who should hear them
- Fix: Secret Sign does not drop
- Removed: "/module" command. In order to interact with blacklist/whitelist modules, rightclick them
- Misc.: Backend improvements

--------------------------Changelog for v1.8.12.3 of SecurityCraft--------------------------

- Fix: Mod is not compatible with newer Forge versions (The minimum required Forge version is now 28.1.0)

--------------------------Changelog for v1.8.12.2 of SecurityCraft--------------------------

- Fix: Mod is not compatible with Forge 28.0.62+

--------------------------Changelog for v1.8.12.1 of SecurityCraft--------------------------

- Fix: Mod is not compatible with Forge 28.0.45+

--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- New: The One Probe support (https://minecraft.curseforge.com/projects/the-one-probe)
- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Fix Sentry only attacking its owner (Thanks burtletoy!)
- Fix: Crash when rightclicking the Keycard Reader with a Keycard without having it set up first

--------------------------Changelog for v1.8.12-beta2 of SecurityCraft--------------------------

- Fix: Crash on server load
- Fix: Inserting a module into a linked Laser/Inventory Scanner kicks the player

--------------------------Changelog for v1.8.12-beta1 of SecurityCraft--------------------------

- Change: Fake Water/Lava recipes are back to how they were before 1.13.2
