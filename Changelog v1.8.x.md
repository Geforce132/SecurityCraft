--------------------------Changelog for v1.8.22 of SecurityCraft--------------------------

- New: SecurityCraft's blocks and items can now properly be used by the offhand
- New: The Codebreaker will now send a message when it failed to break a code
- New: The Admin Tool will now send a message when used while it has been disabled in the config
- Change: The information on how to look around inside a camera is now displayed in the camera's overlay instead of being sent to the chat
- Change: Messages sent due to changing a Sentry's mode will now show up above the hotbar to avoid spamming the chat
- API: Added hook for mods to define a block that can open Reinforced Doors/Reinforced Trapdoors/Reinforced Fence Gates. For more info, see IDoorActivator
- Fix: An item stack cannot be put into the Universal Block Reinforcer's slots if the stack's count is larger than the Reinforcer's durability
- Fix: I.M.S. entity gets stuck in mid-air when leaving and rejoining the world
- Fix: Placing blocks using tools of other mods (like Better Builder's Wands) does not set the owner correctly
- Fix: Portable Radar does not respect the "respect_invisibility" config option
- Fix: I.M.S. attacks players in spectator mode
- Fix: The wooden Reinforced Button's sound is incorrect
- Fix: The Briefcase's NBT contains empty Briefcase inventory slots
- Fix: Some of SecurityCraft's tools don't lose durability when used on a block while held in the offhand
- Fix: Reinforced Buttons and the Reinforced Lever can get washed away by water and destroyed by pistons
- Fix: Mines can be placed on some blocks that don't have a solid top side
- Fix: Claymore explosions ignore the "shouldSpawnFire" configuration option
- Fix: Placing a Scanner-/Keypad Door in a block space that is powered will place the door in an open state
- Fix: Universal Block Remover does not take damage when breaking a deactivated Cage Trap
- Fix: The message that the Codebreaker has been disabled in the configuration file doesn't get sent
- Fix: Placing a Sentry in replaceable blocks, such as grass, does not work
- Fix: Some mines ignore the "smallerMinesExplosion" configuration option

--------------------------Changelog for v1.8.21 of SecurityCraft--------------------------

**READ BEFORE UPDATING TO THIS VERSION: Due to the new sentry modes, your sentries will not behave as in previous mod versions. Make sure to set them to the correct mode again, otherwise they will attack something you don't want to be attacked! Do note, that due to an additional fix regarding the Sentry, Sentries placed above water, lava, or other liquids will be removed and dropped as an item!**

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
- Change: Some customizable options have been adapted to allow for finer control
- Change: Reinforced Iron Bars spawned by a Cage Trap no longer drop
- Change: If not in creative mode, auto-assembling the Block Pocket now only works if the proper building materials are provided in the Block Pocket Manager's inventory (accessible by adding a Storage Module)
- Change: Removing a Storage Module from an Inventory Scanner or Block Pocket Manager will now drop the contents of the block
- API: Added hook for mods to alter if the Sentry can attack their entities. For more info, see IAttackTargetCheck
- API: Added hook for mods to add a way to have their own blocks be convertible to password-protected variants. For more info, see IPasswordConvertible
- API: Inter mod communications related code has been moved to the SecurityCraftAPI class
- Fix: Trophy Systems shoot bullets of their owner's sentries
- Fix: I.M.S. GUI sometimes has faulty text 
- Fix: The Disguise Module does not accept some modded blocks
- Fix: Modules sometimes do not get synched to a linked block (e.g. Laser Block, Inventory Scanner)
- Fix: Removing the block under a Sentry does not remove the Sentry in numerous cases
- Fix: Sentries target invulnerable entities
- Fix: Block of a disguised Sentry sometimes does not properly reappear when broken
- Fix: Sentries can be placed above liquids
- Fix: Players in spectator mode can activate some blocks (e.g. Portable Radar)
- Fix: Some recipes don't get displayed correctly in the SecurityCraft Manual
- Fix: Rightclicking a block while attempting to remove a Briefcase's code does not remove the code
- Fix: Slider tooltips overlap sliders when changing their value (affects Projector, block customization, ...)
- Fix: The names of nameable SecurityCraft blocks cannot contain braces
- Fix: Inventory Scanner does not drop stored items when broken
- Fix: Projector does not drop its contained block when broken
- Fix: Sentry does not attack hostile mobs from Lycanites Mobs
- Fix: Some messages don't get translated when playing on a server
- Fix: Some text in the Inventory Scanner's GUI is not translated
- Fix: Some text in the Inventory Scanner's GUI may not display correctly when using certain languages
- Fix: Prohibited Items slots in the Inventory Scanner's GUI are overlapping
- Fix: Prohibited Items in Shulker Boxes get destroyed by the Inventory Scanner when a redstone module is equipped
- Fix: The custom name of Password-protected Chests doesn't show up at the top of the chest's screen
- Fix: The default name of the Password-protected Chest's screen doesn't get translated
- Fix: Large Password-protected Chests have the wrong label at the top of their screen
- Fix: Password-protected Chests can connect to vanilla chests, causing visual glitches
- Fix: Password-protected Chests sometimes visually disappear despite them still being on screen
- Fix: Defusing/arming some mines does not decrease the durability of the wire cutters/flint and steel
- Fix: Defusing some mines decreases the durability of the wire cutters when in creative mode
- Fix: Attempting to defuse some mines despite them already being defused, decreases the durability of the wire cutters
- Fix: Size and show outline settings of a Block Pocket Manager don't get synchronized to other players
- Fix: Crash involving capabilities of Password-protected Chests/Furnaces
- Fix: SecurityCraft's Doors and the Reinforced Trapdoor are open when placed while powered by redstone
- Fix: Players mounted to cameras are able to break blocks and hit/interact with entities
- Fix: The camera's zoom can't be decreased when increased for too long (and vice versa)
- Fix: The Block Pocket Manager can sometimes allow to activate block pockets that aren't built correctly
- Fix: Block Pocket Manager outline color is incorrect
- Fix: Bouncing Betty is invisible after being tripped
- Fix: The Intelligent Munition System cannot target players
- Removed: Configuration option "alarmSoundVolume"
- Removed: Configuration option "alarmTickDelay" This is now a per-block option
- Removed: Configuration option "claymoreRange" This is now a per-block option
- Removed: Configuration option "debug". It was unused
- Removed: Configuration option "imsRange". This is now a per-block option
- Removed: Configuration option "motionActivatedLightSearchRadius". This is already a per-block option
- Removed: Configuration option "portableRadarDelay". This is already a per-block option
- Removed: Configuration option "portableRadarSearchRadius". This is already a per-block option
- Removed: Configuration option "usernameLoggerSearchRadius". This is now a per-block option
- Misc.: The messages sent on joining a world have been updated to remove outdated messages, improve existing ones, and add one notifying the user of an outdated mod version

--------------------------Changelog for v1.8.20.2 of SecurityCraft--------------------------

- Fix: Potential crash while starting the game

--------------------------Changelog for v1.8.20.1 of SecurityCraft--------------------------

- Fix: Crash when reinforcing blocks in the world

--------------------------Changelog for v1.8.20 of SecurityCraft--------------------------

- New: The name of a camera is now displayed in the top right when the player is mounted to it
- New: Inventory Scanner modifying option to have inventory scanner fields be horizontal
- Change: Inventory Scanner Fields now cannot be destroyed when between two Inventory Scanners (Thanks Redstone_Dubstep!)
- New: A briefcase's owner can now be changed if its owner rightclicks while holding the briefcase in their off hand and a named Universal Owner Changer in their main hand (Thanks Redstone_Dubstep!)
- New: The codebreaker can now be used on a briefcase by holding the briefcase in the off hand and the codebreaker in the main hand and rightclicking (Thanks Redstone_Dubstep!)
- New: Customization option to change how long the Keycard Reader emits a redstone signal when it has been activated (Thanks Redstone_Dubstep!)
- New: Secret Signs can now have a whitelist (Thanks Redstone_Dubstep!)
- New: New customization option to make the text of Secret Signs visible to everyone (Thanks Redstone_Dubstep!)
- New: Modules can now also be added to a block by (sneak-)rightclicking them onto the block instead of using the Universal Block Modifier
- New: Security Cameras, Reinforced Iron Fences, and Reinforced Iron Fence Gates now get affected by ICBM Classic's EMP blast. Blocks that have been hit can be reactivated by rightclicking them with redstone
- New: In inventories, block mines can now be distinguished from their vanilla counterparts
- Change: Laser and Taser damage no longer bypasses armor
- Change: The Admin Tool now only works in creative mode
- Change: Instead of using a crafting table, a briefcase's code can now be reset if its owner rightclicks while holding the briefcase in their off hand and a Universal Key Changer in their main hand. (Thanks Redstone_Dubstep!)
- Change: Balanced Fake Liquids: Reduced damage of Fake Water by 70% and added regeneration effect to Fake Lava instead of healing instantly
- Change: The Panic Button now emits a light level of 4 when turned on
- Change: Just Enough Items now shows proper recipes for un-/reinforcing blocks - the old info screen has been removed in favor of this
- Change: The level 1 Universal Block Reinforcer can no longer unreinforce blocks. At least level 2 is required for this now
- Change: The codebreaker is now 100% effective when using in creative mode
- API: Added hook for mods to define blocks that can extract from Password-protected Chests/Furnaces. For more info, see IExtractionBlock
- Fix: WAILA exploit
- Fix: Taser does not reliably hit entities (Thanks Redstone_Dubstep!)
- Fix: Camera overlay's time and hotkey texts sometimes get obscured or render partly offscreen
- Fix: Inventory Scanner and Inventory Scanner Field metadata is calculated incorrectly (This means that the orientation of your Inventory Scanner (Fields) might reset!)
- Fix: Some customizable blocks don't drop their items correctly when broken
- Fix: Anyone can reset a briefcase's passcode (Thanks Redstone_Dubstep!)
- Fix: Confirm button in the Universal Key Changer's GUI doesn't properly react to changes in the textboxes (Thanks Redstone_Dubstep!)
- Fix: Defused Claymore model is incorrect (Thanks Redstone_Dubstep!)
- Fix: Keycard Reader sometimes sends incorrect messages (Thanks Redstone_Dubstep!)
- Fix: Fire doesn't get removed after exiting Fake Lava
- Fix: Rare crash
- Fix: Transparent pixels in Codebreaker and Sentry Remote Access Tool textures
- Fix: Panic button has no sound
- Fix: Rotating/mirroring some blocks (e.g. using mods like Recurrent Complex) does not work correctly
- Fix: Pipe interaction with Password-protected Chest/Furnace
- Fix: Incorrect Reinforced Hopper sounds
- Fix: Admin Tool has to be sneak-rightclicked in order to show information
- Fix: Portable Radar sometimes doesn't turn off the redstone signal when it's supposed to
- Fix: Incorrect Reinforced Lever sounds
- Fix: Vanilla redstone power sources can still activate Reinforced Doors and similar in specific cases
- Fix: Reinforcing a hopper in the world drops its contents
- Fix: Changes to installed modules don't sync to clients on a dedicated server
- Fix: The first rightlick of a Whitelist/Blacklist Module does not open the GUI on a dedicated server
- Fix: Cannot exit GUIs using escape key when a textfield is focused
- Removed: Taser Bullet entity
- Potential Fix: First Alarm sound sometimes does not play
- Misc.: Various French language fixes (Thanks supercat95!)

--------------------------Changelog for v1.8.19.3 of SecurityCraft--------------------------

- Fix: Packet exploit

--------------------------Changelog for v1.8.19.2 of SecurityCraft--------------------------

- Fix: SecurityCraft is incompatible with OpenCubicChunks
- Fix: Crash on SC Manual pages with subpages, when using a language that does not use spaces to seperate words (Thanks jihuayu!)

--------------------------Changelog for v1.8.19.1 of SecurityCraft--------------------------

- Fix: Tooltips of modules show a header even if no blocks/items/players have been added (Thanks Redstone_Dubstep!)
- Fix: Projector doesn't sync on servers

--------------------------Changelog for v1.8.19 of SecurityCraft--------------------------

**READ BEFORE UPDATING TO THIS VERSION: The backend of the module system has been rewritten. Despite thoroughly testing it, there might still be issues we missed. Always backup your world before updating! If you find issues, please report them to us.**

- New: The Reinforced Hopper is now the only block that can pull out of Password-protected Chests and Furnaces. The owner of both blocks needs to be the same, or the hopper needs to be owned by a whitelisted player
- New: White-/Blacklist Modules now work for the Password-protected Chest and Furnace (including customization options to turn off the messages)
- New: Players in creative can now remove any sentry and not just their own
- New: The Block Pocket can now be built automatically from within the Block Pocket Manager's GUI (Thanks Redstone_Dubstep!)
- New: Projector (Projects a fake wall of a block of your choice into the world)
- Change: Disguising a sentry will now respect previously placed blocks at the sentry's position (Thanks Redstone_Dubstep!)
- Change: Improved the Block Pocker Manager's GUI (Thanks Redstone_Dubstep!)
- API: New IModuleInventory interface that adds the ability to have modules in a TileEntity
- API: New ICustomizable interface that adds the ability to have customization options in a TileEntity
- Fix: Universal Block Remover sends two messages when the right-clicked block cannot be removed
- Fix: Mine explodes when trying to change its owner
- Fix: Removing a Redstone Module from an active Portable Radar does not update the redstone output
- Fix: Universal Block Remover creates two breaking sounds
- Fix: Whitelist Module does not work in the Keycard Reader
- Fix: Placing a block on an active Keypad plays no sound
- Fix: Trophy System beam is not always visible
- Fix: The customization GUI for Block Pocket blocks can be opened despite there being no functions
- Fix: Modules drop twice when breaking a block containing modules in creative mode
- Fix: Modules can duplicate when loading chunks
- Fix: Retinal Scanner crash
- Fix: Hoppers etc. can extract from the fake slots of an Inventory Scanner
- Fix: Capability related crash
- Fix: Backslashes visible in German language file
- Fix: Security Camera still powers blocks when removing the Redstone Module
- Fix: Modules don't get synched correctly on servers

--------------------------Changelog for v1.8.18.2 of SecurityCraft--------------------------

- Fix: Username Logger crash

--------------------------Changelog for v1.8.18.1 of SecurityCraft--------------------------

- API: Option is now abstract and some methods have been changed around for cleaner code
- Fix: Reinforced Levers etc. don't reliably open Reinforced Iron Doors etc. when powering a block next to them
- Fix: Portable Radar cannot be placed
- Fix: New customization options don't correctly load for existing blocks
- Fix: Retinal Scanner crash
- Fix: Camera dis-/mounting doesn't work correctly

--------------------------Changelog for v1.8.18 of SecurityCraft--------------------------

- New: More reinforced block recipes for parity with vanilla (Thanks Redstone_Dubstep!)
- New: Fake Water/Lava Buckets can now be properly used in dispensers
- New: Trophy System now destroys shulker bullets (Thanks Redstone_Dubstep!)
- New: Reinforced Redstone Lamp
- New: Reinforced Cobblestone Wall, Reinforced Mossy Cobblestone Wall
- New: Reinforced Blocks: Hopper, Observer, Buttons, Lever, Grass Block, Grass Path, Coarse Dirt, Podzol, Ice, Snow Block, Clay, Mycelium, Packed Ice, Nether Wart Block, Cobweb (Thanks Redstone_Dubstep!)
- New: Block mines for Coal Ore, Emerald Ore, Gold Ore, Iron Ore, Lapis Lazuli Ore, Nether Quartz Ore, and Redstone Ore
- New: Configuration option to turn off the darker textures of reinforced blocks
- New: Pressing "Add", "Remove" or "Clear" in the Whitelist/Blacklist module screen clears the textbox (Thanks Redstone_Dubstep!)
- New: The buttons in the Whitelist/Blacklist module screen activate and deactivate depending on the text field input and the amount of stored players (Thanks Redstone_Dubstep!)
- New: HWYLA and TOP now show a Sentry's owner, its current mode, and the equipped modules (Thanks Redstone_Dubstep!)
- New: Config option to turn off the recipes of all SecurityCraft explosives
- New: Option in the Universal Block Modifier GUI of the Keypad and Keycard Reader to turn off messages for whitelisted/blacklisted players
- New: Configuration option to turn off showing the owner's face on a retinal scanner
- New: Customization option to change how long the Keypad emits a redstone signal when the correct code has been inserted
- Change: Zooming when viewing a Security Camera is no longer restriced to only three zoom levels
- Change: Creepers, Ocelots, and Endermen can now trigger Mines
- Change: White-/Blacklist Modules can now hold up to 50 players
- Change: Reinforced Doors, Fence gates and trapdoors have a greater range to detect nearby active reinforced blocks (Thanks Redstone_Dubstep!)
- Change: Sentries now attack flying mobs (except bats), Slimes, Magma Slimes, Shulkers and the Ender Dragon (Thanks Redstone_Dubstep!)
- Change: The Protecto can now attack charged creepers and zombie pigmen
- Change: Overhauled the inventory scanner. You can now choose what it does by inserting a storage module, redstone module, or both
- Change: The Smart Module now allows anyone to fully access a Security Camera
- Change: /sc help no longer gives a free SecurityCraft Manual
- Fix: Password-protected chest looses content and owner when turning it by placing another chest next to it
- Fix: Localization for camera keybindings is incorrect
- Fix: Incorrect laser removal in creative mode
- Fix: Crash when trying to view the Codebreaker help page in Spanish
- Fix: Cage Trap owner collision is incorrect
- Fix: Inventory Scanners can connect to Inventory Scanners by different owners
- Fix: Password setup screen displays long block names incorrectly
- Fix: Bouncing Betty doesn't play a sound when triggered (Thanks Redstone_Dubstep!)
- Fix: Block Mines let items and the owner go through (Thanks Redstone_Dubstep!)
- Fix: Trophy System destroys projectiles even if they have already been removed (Thanks Redstone_Dubstep!)
- Fix: Textures of Universal Block Modifier, Universal Owner Changer and the Mine Remote Access Tool are transparent at the sides (Thanks Redstone_Dubstep!)
- Fix: Step sounds of disguised blocks are wrong
- Fix: Camera Monitor tooltip shows amount of first X bound cameras instead of amount of all bound cameras
- Fix: Mines and Portable Radar can exist without a support block (Thanks Redstone_Dubstep!)
- Fix: Claymore spawns too many particles when being broken (Thanks Redstone_Dubstep!)
- Fix: Claymore has no collision (Thanks Redstone_Dubstep!)
- Fix: Crash when breaking the floor/ceiling of an activated Block Pocket
- Fix: Keycard Readers can be set up by players other than the owner
- Fix: Fake Water can be placed in the nether
- Fix: Occasional Retinal Scanner crash
- Fix: Config option "mineExplodesWhenInCreative" does not work for block mines
- Fix: When inserting a password, adding more characters after the limit has been reached results in having to press backspace more times than needed
- Fix: Pick Block on Sentry doesn't work (Thanks Redstone_Dubstep!)
- Fix: Destroying an active SecurityCraft block while a reinforced door is open keeps the door open (Thanks Redstone_Dubstep!)
- Fix: Sentries shoot at targets that are in their death animation (Thanks Redstone_Dubstep!)
- Fix: The wooden reinforced pressure plate doesn't have a whitelist module description (Thanks Redstone_Dubstep!)
- Fix: Hoppers/pipes/etc. can extract items out of a password-protected chest/furnace
- Fix: The Protecto can attack Sentries
- Fix: Shift-clicking in customization gui does not work properly
- Fix: WAILA/TOP/Admin Tool show english module names instead of the translated counterpart
- Fix: Auth server related issue due to Retinal Scanner requests
- Fix: The Taser can be enchanted
- Fix: Message when changing a sentry's owner gets sent twice
- Fix: Changing the owner of a Sentry does not correctly set the owner/uuid
- Fix: White-/Blacklist message of Keycard Reader getting sent twice
- Fix: The Scanner Door and Reinforced Iron Door have no place sound
- Fix: Can use other items than a Camera Monitor when mounted to a Security Camera
- Fix: Placing a Scanner Door or Reinforced Iron Door plays no hand animation
- Fix: Rightclicking an unowned block mine with a Universal Block Remover plays a hand animation
- Potential Fix: Sentry doesn't shoot when using Sponge Forge

--------------------------Changelog for v1.8.17 of SecurityCraft--------------------------

- New: The Universal Block Reinforcer can now "unreinforce" blocks. Place items in the top slot to reinforce them, use the bottom slot instead for "unreinforcing" them
- New: The Retinal Scanner now renders its owner's face (Thanks LorenaGdL!)
- New: The Username Logger now displays the UUID of logged players and the time they were logged at to its owner. Click an entry to copy the UUID
- New: Fire on Reinforced Netherrack now doesn't get removed
- New: The Mine-/Sentry Remote Access Tool GUI now shows explanatory tooltips when hovering over buttons
- New: The Mine-/Sentry Remote Access Tool now show whether a mine/sentry the player is looking at is bound to them on the hotbar (just like the Camera Monitor)
- New: Spanish translation (Thanks Ryo567!)
- New: Configuration option to account for invisibility (Sentries, Inventory Scanners, Lasers, etc. won't detect entities if they're invisible, config option is off by default)
- Change: The Username Logger now displays logged users in a scrollable list
- Change: The floor of Block Pockets is now solid (reactivate Block Pockets to fix this for already existing ones)
- Change: Textboxes when setting up/inserting/changing passwords are now automatically focused
- Change: Laser Blocks now create connections more easily
- Change: The SC Manual now displays pages alphabetically
- Change: You can now use boats in fake water
- Fix: Lasers don't get removed correctly (Thanks Redstone_Dubstep!)
- Fix: Motion Activated Light activates when a player is mounted to a camera in its range
- Fix: Being in fake lava creates a hurting sound
- Fix: Entities keep burning when exiting fake lava
- Fix: Some mines drop when exploding
- Fix: Camera overlay is incorrect when only night vision is activated
- Fix: Track Mines cannot be defused/armed with Wire Cutters/Flint and Steel
- Fix: Electrified Iron Fence Gates cannot be opened by reinforced pressure plates
- Fix: Feedback message is missing when changing the owner of a Sentry
- Fix: It's possible to reinforce more items than the universal block reinforcer has durability for
- Fix: Codebreaker loses damage even when rightclicking blocks that are not password-protected
- Fix: Reinforced (Stained) Glass sides are visible through the same type of Reinforced (Stained) Glass
- Fix: Reinforced (Stained) Glass throws a shadow
- Fix: Dropped items get destroyed in fake water
- Fix: Camera does not render in third person view when being mounted to one
- Fix: It's possible to remove or otherwise interact with a camera when being mounted to it
- Fix: Username Logger can log the same player twice at the same time
- Fix: Block mines don't have the same hardness as their vanilla counterpart
- Fix: HWYLA doesn't show information of disguised blocks correctly
- Fix: Password-protected Chests cannot be opened when a half-slab or similar is placed above them
- Fix: Mine-/Sentry Remote Access Tools lose their mines/sentries when rightclicking the tool out of range
- Fix: The One Probe doesn't show fake liquids correctly
- Fix: Some (universal) tools can identify disguised blocks and block mines
- Fix: Fake water doesn't change its tint depending on the biome
- Fix: Cyclic's Sack of Holding can pick up blocks that are owned by different players
- Fix: Keycard Reader does not send an error message when using an incorrect keycard with "equal to or higher than" mode
- Potential Fix: ConcurrentModificationException when saving a linkable tile entity

--------------------------Changelog for v1.8.16 of SecurityCraft--------------------------

- New: The Retinal Scanner and the Scanner Door now have an option to turn off the "Hello" message. Available by rightclicking them with a Universal Block Modifier
- New: The Alarm now has an option to set the range of blocks that it can be heard in (0-100)
- New: The Cage Trap can now be disguised
- New: The SC Manual now shows available options and modules for customizable blocks
- New: French translation (Thanks marminot!)
- Change: Additionally to sneak-rightclicking, sentries can now also be removed using the Universal Block Remover
- Change: The powered taser now inflicts one heart of damage instead of half a heart
- Fix: Players other than the one mounted to a camera can take screenshots or otherwhise interact with the camera
- Fix: Players that don't own an Inventory Scanner can still take out its items
- Fix: Inventory Scanner Field's selection box is slightly off
- Fix: Reinforced Doors and Scanner Doors can be broken using pistons
- Fix: Block Pocket Wall's sides are visible through other Block Pocket Walls
- Fix: Activating a Cage Trap can delete blocks that shouldn't be deleted
- Fix: Sentry Remote Access Tool tooltip does not show sentry names
- Fix: Portable Radar can duplicate modules
- Fix: Inventory Scanner removes its owner's items
- Fix: Laser harms its owner if the harming module is installed
- Fix: Trophy System does not target arrows shot from dispensers
- Fix: Copied module lists can only be pasted once
- Fix: Various language fixes and updates (Thanks supercat95 and Redstone_Dubstep!)
- Fix: Removing a laser (block) may sometimes break lasers that shouldn't be broken
- Fix: Various mines do not ignore their owner
- Fix: Sentry can be placed in the same block space as other blocks
- Fix: Breaking a Password-protected Chest does not drop its contents when using SpongeForge (Thanks gununakuna!)
- Fix: Breaking a Laser Block/Inventory Scanner keeps modules in connected Laser Blocks/Inventory Scanners
- Fix: Crash when summoning sentry using commands (Thanks LorenaGdL!)
- Potential Fix: Block Pocket still loses its owner when reloading world/server

--------------------------Changelog for v1.8.15 of SecurityCraft--------------------------

- New: Reinforced Bookshelf (Can also be used for an enchanting table)
- New: If a Briefcase has been renamed, it will now show that name in the inventory GUI
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
- Fix: Inventory Scanner does not check for prohibited items in armor and offhand slots
- Fix: Crash when the Trophy System tries to destroy a sentry's bullet
- Fix: Alarm does not turn off when it no longer receives a redstone signal
- Fix: Keypad blacklist does not work
- Fix: Trophy System crash
- Fix: Sentry head does not show when rejoining world/dimension if it was previously showing (Thanks LorenaGdL!)
- Fix: Cage Trap uses stone instead of metal sounds
- Fix: Disguised Keypad doesn't show up as disguised when switching worlds/dimensions
- Fix: Laser Block can duplicate modules
- Fix: I.M.S. does not emit a sound when launching one of its mines
- Fix: Username Logger logs its owner
- Fix: Laser Blocks can connect to other Laser Blocks that don't have the same owner
- Fix: I.M.S. particles don't work
- Fix: Sentry kick/crash
- Fix: Module duplication
- Fix: Message about cage trap having trapped someone gets sent to the one being trapped and not the cage trap owner (Thanks LorenaGdL!)
- Misc.: The minimum required Forge version is now 14.23.5.2826

--------------------------Changelog for v1.8.14.1 of SecurityCraft--------------------------

- Fix: Crash when opening customizing GUI of blocks that can't take modules
- Fix: In the customizing GUI, JEI moves items out of the way without needing to
- Fix: Keycard Reader GUI displays for a short amount of time when opening the customizing GUI
- Fix: Inventory Scanner does not check for prohibited items in armor and offhand slots
- Misc.: Added another safeguard against alarm crash

--------------------------Changelog for v1.8.14 of SecurityCraft--------------------------

- New: Reinforced Glass Panes can now be crafted using six Reinforced Glass
- New: Reinforced Stained Glass Panes can now be crafted by surrounding a dye with eight Reinforced Glass Panes
- New: All Reinforced Terracotta blocks can now be crafted analogous to their vanilla counterparts
- New: Reinforced Red Sand
- New: Reinforced Wooden Pressure Plate
- New: The Block Pocket now tells the player when it was successfully de-/activated
- Change: The Reinforced Iron Trapdoor is now created by putting a regular Iron Trapdoor into a Universal Block Reinforcer
- Change: The Frame recipe is slightly different, ingredients stay the same
- Change: The Sentry now uses Reinforced Blocks of Iron in its recipe
- Fix: Username Logger crash involving armor stands
- Fix: Reinforced Diorite and Reinforced Andesite use vanilla Cobblestone instead of Reinforced Cobblestone
- Fix: Using six Reinforced Stained Glass to craft Reinforced Stained Glass Panes yields 8 glass panes instead of 16
- Fix: Chests and Furnaces cannot be converted to their password-protected variant when not sneak-rightclicking
- Fix: Disguised Keypad collision and selection boxes do not respect the boxes of the block it's disguised as
- Fix: Block Pocket Description incorrectly mentions Reinforced Quartz instead of Reinforced Crystal Quartz
- Fix: Entities do not get healed when standing in fake lava
- Potential Fix: Block Pocket loses its owner when the player reloads the world

--------------------------Changelog for v1.8.13.1 of SecurityCraft--------------------------

- Fix: Disguising a Keypad does not synchronize to other players
- Fix: Sentry animation is not synchronized between players

--------------------------Changelog for v1.8.13 of SecurityCraft--------------------------

- New: Italian translation (Thanks Chexet48!)
- New: Trophy System (Inspired from https://callofduty.fandom.com/wiki/Trophy_System)
- New: Block Pocket (Designed by Henzoid)
- New: The Whitelist Module can now be used with the Sentry
- New: Button to clear the Username Logger logged players list
- New: Reinforced Stone Pressure Plate (Only the owner and whitelisted users can press it, can be used to open Reinforced Iron (Trap-)Doors)
- New: The Smart Module now works for Security Cameras, enabling others to view cameras at a fixed angle
- Change: The Motion Activated Light is now triggered by mobs as well (This change includes tweaks to the attack logic that may impact other blocks, like the Portable Radar or the I.M.S., as well)
- Change: New Secret Sign textures
- Fix: Inventory Scanner ignores contents of Shulker Boxes
- Fix: Portable Radar does not update redstone signal correctly when Redstone Module is installed
- Fix: Installed modules do not drop when destroying block with Universal Block Remover
- Fix: SecurityCraft Manual does not correctly display items with metadata
- Fix: Misc. crashes
- Fix: Taser can be duplicated using offhand slot (Thanks Boreaus!)
- Fix: Incorrect version of the taser shows up in the creative tab and JEI
- Removed: "/module" command. In order to interact with blacklist/whitelist modules, rightclick them
- Misc.: Backend improvements

--------------------------Changelog for v1.8.12.1 of SecurityCraft--------------------------

- Fix: Crash involving the Security Camera

--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- New: The One Probe support (https://minecraft.curseforge.com/projects/the-one-probe)
- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Crash when rightclicking the Keycard Reader with a Keycard without having it set up first
- Fix: French translation does not work
- Fix: Password-protected Chest doesn't show in the inventory under certain circumstances

--------------------------Changelog for v1.8.11 of SecurityCraft--------------------------

- New: Reinforced Gravel
- New: Reinforced Sand
- New: Sentry
	- This feature was designed by Henzoid, who won our New Year's Eve giveaway! If you want to get notified of	future giveaways, join our Discord server: https://discord.gg/U8DvBAW
	- If you want to completely design a feature of your choice, consider becoming a patron! https://www.patreon.com/Geforce
- New: The SC Manual now shows text, that is too long to properly display, on multiple subpages
- Change: Using the left and right arrow keys in the SC Manual will now change subpages
- Change: The viewing range of cameras placed on the ceiling has been increased drastically
- Fix for real: Reinforced blocks can be destroyed by the Wither
- Fix: The Mine Remote Access Tool's GUI displays incorrectly for mines that cannot be defused
- Fix: Two messages appear instead of one when using the Keycard Reader with an insufficient security level
- Fix: Disguise Module tooltip shows unlocalized block name
- Fix: Miscellaneous crashes
- Misc.: Small adjustments in english translation

--------------------------Changelog for v1.8.10 of SecurityCraft--------------------------

- New: Config option to allow players to claim blocks that do not have an owner. Only blocks with the owner "owner" and uuid "ownerUUID" will work (check with the Admin Tool)
~~- Fix: SecurityCraft blocks can be destroyed by the Wither~~
- Fix: Typo in German localization
- Fix: Keybind localization does not work
- Fix: Rightclicking another player's chest with a Universal Block Remover/Modifier does not show a message

--------------------------Changelog for v1.8.9 of SecurityCraft--------------------------

**This version no longer supports 1.12 and 1.12.1**

- New: Whitelist and Blacklist Modules can now be rightclicked to manage the players
- New: The SC Manual now has a recipe (Book + Iron Bars)
- Fix: Crash when using a Forge version that is too old. Instead, the game will now notify the user to update their Forge version
- Fix: Blocks do not show up correctly in the SC Manual (Thanks supercat95!)
- Misc.: The minimum required Forge build is now 14.23.3.2694

--------------------------Changelog for v1.8.8.1 of SecurityCraft--------------------------

- Fix: Players can teleport to a Security Camera's position when logging out while viewing one
- Fix: Crash when reinforcing reinforced blocks added in v1.8.8 (Thanks AlexM-Dev!)

--------------------------Changelog for v1.8.8 of SecurityCraft--------------------------

- New: Reinforced Blocks
		- Carpet
		- Glowstone
		- Stained Glass Panes are back!
- New: Reinforced Stained Glass and Reinforced Stained Glass Panes can now be created using the Universal Block Reinforcer (alongside the already existing recipes)
- New: The Track Mine can now be controlled with the Mine Remote Access Tool
- New: The Briefcase's code can now be reset by crafting it together with a Universal Key Changer
- New: The Admin Tool now shows the text on Secret Signs
- New: The I.M.S. can now be reloaded by rightclicking it with Bouncing Betties
- New: Reinforced Blocks of Iron, Gold, Diamond, and Emerald can now be used as a Beacon base
- New: Gravel Mine
- New: Sand and Gravel Mines now fall like normal sand/gravel
- New: Reinforced Moss Stone can now be crafted with a Reinforced Cobblestone and a Vine
- New: The several Reinforced Stone variants (Reinforced Granite, Reinforced Diorite, Reinforced Andesite) can now be crafted analogous to their vanilla counterparts
- New: Security Cameras can now be switched when already viewing a camera
- Change: Item tooltips in the SecurityCraft Manual now show the complete tooltip instead of just the name
- Change: The Alarm recipe now needs Reinforced Glass instead of vanilla glass
- Change: The Universal Block Reinforcer will now reinforce placed down blocks instantly
- Change: When broken, the I.M.S. will now drop any charges left
- API: Added a data serializer to the Owner class
- Fix: Torch/door/etc. placement on several SecurityCraft blocks does not work as expected
- Fix: Mines that don't exist in the world anymore do not disappear from a Mine Remote Access Tool, if bound
- Fix: Item duplication with Inventory Scanner and Storage Module
- Fix: Inventory Scanner's redstone mode doesn't respect Smart Module
- Fix: Items thrown through an Inventory Scanner Field don't respect the Smart and Storage Module
- Fix: Inventory Scanner does not trigger on item stacks that have a size greater than one
- Fix: When breaking an Inventory Scanner, items placed in the blacklist slots will drop
- Fix: Iron/Reinforced Trapdoor can be opened by redstone
- Fix: Placing/breaking Inventory Scanners can break other Inventory Scanner's scanner fields
- Fix: Name inconsistencies with vanilla
- Fix: Admin Tool doesn't get properly disabled when it is disabled in the config
- Fix: Existing translations do not work
- Fix: Taser reequips all the time when loading after a shot (Note that this cannot be implemented in 1.7.10 due to limitations in Forge)
- Fix: Password-protected Furnace does not give off light when open and burning
- Fix: The Inventory Scanner Field is missing its top and bottom texture
- Fix: Storage Module does not work
- Fix: Model loading errors
- Fix: Reinforced Iron Bars model is not the same as the vanilla Iron Bars model
- Fix: Password-protected Chest does not synchronize owner correctly when loading world
- Fix: Item Stack tooltips don't get rendered in SecurityCraft inventories
- Fix: Beacon color with Reinforced Stained Glass is slightly incorrect
- Fix: TileEntity IDs are not registered to SecurityCraft's domain
- Misc.: Possibly improved render performance a bit
- Internal: Removed unused code
- Internal: Refactored to make code more readable
- Internal: Use recommended way of registering content

--------------------------Changelog for v1.8.7 of SecurityCraft--------------------------

**!!WARNING!! Upgrading to this version of SecurityCraft will deny access to any items that are placed in the blacklist slots of any Inventory Scanner (they will *not* disappear, but you won't be able to get them back)**

- New: The SC Manual can now be navigated using the mouse's scroll wheel
- New: The SC Manual now remembers the last page that was viewed and opens back up at that page
- New: Reinforced Obsidian can now be used for nether portals (Note that this cannot be implemented in 1.7.10 due to limitations in Forge)
- Change: The Inventory Scanner's blacklist slots are now "ghost slots", which means that items can be put into and pulled out of them, but no physical item will actually be used
- Fix: Taser doesn't respect PVP settings
- Fix: Blocks in the Manual aren't shaded correctly
- Fix: Portable Radar does not work
- Fix: Portable Radar doesn't respect the whitelist module
- Fix: Players mounted on cameras trigger motion activated lights
- Fix: Various strings aren't translated
- Fix: Storage Module slots on the Inventory Scanner's GUI texture are flipped
- Fix: Reinforced Doors cannot be opened from the top or bottom
- Fix: Grass can grow under Fake Water (Note that this fix cannot be applied to 1.8.9 and below due to limitations in Forge)
- Fix: Fake liquids turn back into their vanilla forms (Note that this fix cannot be applied to 1.8.9 and below due to limitations in Forge)
- Fix: Dupe bug involving Reinforced (Scanner) Doors (Thanks InsertCheerios!)

--------------------------Changelog for v1.8.6 of SecurityCraft--------------------------

- New: Rightclicking an activated Cage Trap with a Universal Block Remover will remove the complete cage, provided the trap and bars are owned by the same person
- Fix: Possible conflicting language strings with other mods
- Fix: Crashes involving the Username Logger or Inventory Scanner Field (Thanks Kreezxil!)
- Fix: Fix incorrect lighting in some GUIs
- Fix: Fix GUI tooltips rendering under JEI interface

--------------------------Changelog for v1.8.5 of SecurityCraft--------------------------

- New: Two Reinforced Doors placed next to each other will open together when one of them is opened
- New: Completely overhauled the GUI of the Mine Remote Access Tool
- New: The Security Camera can now be placed on the ceiling
- New: Sneak-rightclicking the Taser with redstone in the inventory will double the Taser's power for one shot
- New: Secret Sign which can only be read by its owner
- New: Reinforced Blocks
	- Obsidian
	- End Stone
	- Netherrack
	- Sea Lantern
	- Bone Block
- New: Motion Activated Light
- Change: When viewing a camera, the view now more closely represents what the camera would see
- Change: The Briefcase recipe now requires a Password-protected Chest instead of a regular one
- Change: The Taser now only applies level 2 potion effects for 10 seconds
- Fix: Language key for Codebreaker doesn't inform the user about its limited usability
- Fix: Welcome message shows when connecting to a server, even when disabling it on clientside
- Fix: Name Tag gets deleted when renaming cameras in creative mode
- Fix: Incorrect model for double Password-protected Chest
- Fix: Rare crash involving Inventory Scanner Fields
- Fix: Fake Liquids sometimes don't work correctly
- Fix: Furnace Mine doesn't explode when an item is held
- Fix: Password-protected blocks can be set-up by players other than the owner
- Fix: Inconsistent Alarm model
- Fix: Protecto does not work
- Fix: Some trigger ranges for SecurityCraft blocks were not calculated correctly
- Fix: Under certain circumstances, the Alarm doesn't break when the block it's placed on is removed
- Potential Fix: StackOverflowException involving block mines
- Sponge Fix: Keycard Reader cannot be configured
- Sponge Fix: Server crashes when placing Reinforced Stained Glass over an active Beacon

--------------------------Changelog for v1.8.4.1 of SecurityCraft--------------------------

- Fix:Crash when inserting a module into a Laser Block (Thanks Shrimplet596!)

--------------------------Changelog for v1.8.4 of SecurityCraft--------------------------

- New: Inventory Scanners can now be configured to have a higher range, similar to Laser Blocks
- Change: Password-protected blocks no longer have a crafting recipe. Instead, rightclick a Frame/Chest/Furnace with a Key Panel to create them (any contents are safe!)
- Fix: Sounds don't respect their correct categories
- Fix: Laser fields break when breaking other Laser Blocks placed orthogonal to the fields
- Fix: Items can be duplicated within the Module GUI
- Fix: Reinforced Iron Trapdoor can be broken by any player (Thanks shaiapouf!)
- Fix: Portable Radar can be broken by any player
- Fix: Username Logger can be broken by any player
- Fix: Alarm sound pitch is incorrect
- Fix: Password-protected Furnace doesn't drop items when being broken
- Fix: Security Camera doesn't update Redstone correctly when the Redstone Module is removed/added/turned on/turned off
- Fix: SecurityCraft Tile Entity data doesn't get synchronized correctly with clients on world load [1]
- Fix: Module GUI cannot be accessed
- Fix: Crashes
- Fix: Alarm sound volume config option does not affect the ingame sound
- Fix: Incorrect string in Password-protected Furnace GUI
- Fix: Some SecurityCraft sounds don't play at all (Taser/Camera)
- Removed: Tip for /sc connect
- Internal: Refactoring to make code a little more readable and cleaner
- Internal: Removed legacy code

[1] This fixes:
  1. The camera not rotating when reloading the world
  2. The Redstone Module not working when trying to change the camera's redstone output right after reloading the world
  3. The Keypad not being disguised after reloading the world
  4. Probably some other things
  
--------------------------Changelog for v1.8.3 of SecurityCraft--------------------------

- New: Reinforced Blocks
  - Logs
  - Lapis Lazuli Block
  - Block of Coal
  - Block of Gold
  - Block of Iron
  - Block of Diamond
  - Block of Emerald
  - Wool
  - Quartz incl. slabs and stairs
  - Prismarine/Prismarine Bricks/Dark Prismarine
  - Red Sandstone incl. slabs and stairs
  - (Smooth) Granite/Andesite/Diorite
  - End Stone Bricks
  - Red Nether Brick
  - Purpur incl. slabs and stairs
  - Concrete
- New: Keypad Gurnace
- New: Information about how to exit the Security Camera
- New: Cameras can now be unbound from within the Camera Monitors' GUI, even if they're no longer present in the world (press the X at the top right of the respective button)
- New: JEI (JustEnoughItems) now shows information about blocks and items without a recipe
- Change: The SecurityCraft Manual now only displays one general page about reinforced blocks as not to clutter the book
- Fix: Reinforced Stained Hardened Clay (1.12.2: Terracotta) isn't craftable
- Fix: Several incorrect language strings
- Fix: Mines can be activated when viewing a camera (thanks LeKoopa!)
- Fix: Incorrect string in Password-protected Furnace GUI
- Fix: Crash involving the new Camera Monitor indicator (overlay in inventory when looking at a camera)
- Fix: The Camera Monitor's inventory overlay sometimes shows incorrect information
- Fix: Players get kicked sometimes when using the Codebreaker on a server
- Fix: Issue when rightclicking a block with a module
- Fix: The '/sc' command doesn't always show help when executing it incorrectly
- Fix: Crash when trying to open a blocked Password-protected Chest
- Fix: Unlocalized string in I.M.S. settings menu
- Fix: Incorrect rendering of the Camera Monitor's inventory overlay
- Fix: Buttons overlapping with the JEI interface are not accessible
- Fix: Sponge incompatibilities (~~untested on 1.10.2 and 1.11.2, please report any issues you find to our Discord's #bugreport channel.~~ As of 1. January 2018 Sponge no longer supports 1.10/1.11 versions, SecurityCraft will do the same)
- Fix: I.M.S. settings menu is not accessible
- Removed: IRC support chat. Please refer to the #help channel on SecurityCraft's Discord! https://discord.gg/U8DvBAW
- Internal: Rewrote handling of reinforced blocks (Now much easier to add them)

--------------------------Changelog for v1.8.2.4 of SecurityCraft--------------------------

- New: Camera can be set to a fixed angle using the Universal Block Modifier
- New: When looking at a camera while holding a monitor, an overlay will be shown based on if the camera is added to the monitor or not
- New: Disabled recipes now show an empty grid in the SecurityCraft Manual
- Fix: Unintended behavior when using '/sc contact' on a multiplayer server
- Fix: Invalid Discord invite link
- Fix: IRC security issue
- Fix: Incorrect German language strings
- Fix: Retinal Scanner doesn't respect the whitelist module
- Fix: Adding/Removing an active Redstone Module does not update the state of connected redstone
- Fix: Installed modules disappear in certain situations
- Fix: Keycard recipes cannot be disabled via config
- Fix: WAILA can now be used again (Use Hwyla https://minecraft.curseforge.com/projects/hwyla)

--------------------------Changelog for v1.8.2.3 of SecurityCraft (v1.8.2.3-hotfix below)--------------------------

- New: Reinforced Blocks
  - Stone Bricks (normal, mossy, cracked, chiseled) incl. stairs and slabs
  - Mossy Cobblestone
  - Bricks incl. stairs and slabs
  - Nether Bricks incl. stairs and slabs
  - Hardened Clay
  - Terracotta
- New: Official SecurityCraft server tip
- Change: Heavily nerfed Codebreaker. It now has 5 uses and a 1 in 3 chance of failing
- Fix: Recipe for Reinforced Glass does not show up in the SecurityCraft Manual
- Fix: WAILA does not update the new owner of a door when changed with a Universal Owner Changer
- Fix: Both halves of a Scanner Door can have different owners
- Fix: Cage Trap can be escaped
- Fix: Taser can tase the player who shot
- Fix: Reinforced Doors can be opened by any SC block, not only the ones with the same owner as the door
- Fix: Descriptions do not translate to different languages in the SecurityCraft Manual
- Fix: Reinforced Iron Bars placed by Cage Traps have no owner
- Fix: Reinforced Iron Fence does not damage players
- Fix: Reinforcing Andesite/Granite/Diorite gives back a glitched block
- Fix: Players get kicked sometimes when using the Password-protected Furnace
- Fix: Username Logger cannot be opened
- Fix: Reinforced Stairs are turned incorrectly when placed upside down
- Fix: Upside down Reinforced Slabs show up incorrectly in WAILA
- Fix: Wrong Fake Liquids recipes showing up in the SecurityCraft Manual
- Fix: Glass Panes can be put into Universal Block Reinforcers
- Fix: Crash when breaking planks/sandstone with a Universal Block Reinforcer
- Fix: Universal Block Reinforcer does not show up when being held
- Fix: Reinforced Doors don't have a placing sound
- Fix: Server error when using a Redstone Module
- Fix: Blocks scanning for players/mobs do not work correctly
- Fix: Recipes are not grouped in Recipe Book
- Removed: Config option to enable the old Keypad recipe

**Hotfixes:**
- Fix: Codebreaker can be enchanted with books at the anvil
- Fix: Issue with the recent Security Camera animation fix
- Fix: Reinforced stairs placed a certain way are still being displayed incorrectly
- Fix: New top slabs are displayed incorrectly

--------------------------Changelog for v1.8.2.2 of SecurityCraft--------------------------

- Ported to 1.12
- New: Reinforced textures now adapt to the resourcepack being used
- New: The Cage Trap can now be set to capture hostile mobs via the Universal Block Modifier
- New: Information about needing the Redstone Module for the Security Camera
- New: Discord tip
- New: Stained Reinforced Glass now colors beacon beams
- Change: Recipes with Reinforced Glass Panes now use normal Reinforced Glass
- Fix: Crash when adding an empty Disguise Module to a Keypad
- Fix: Missing German language strings
- Fix: Some GUIs cannot be closed
- Fix: The Universal Key Changer allows non digit characters
- Fix: Wrong texture for Reinforced Stone Slabs
- Fix: Crash when shift-clicking an item out of the Disguise Module's slot
- Fix: Security Camera can be broken without a Universal Block Remover
- Fix: Alarm bounding box is too large for top/bottom alarm
- Fix: Missing language strings for SecurityCraft Manual and Reinforced Planks within the SecurityCraft Manual
- Fix: Pick block does not work on Keypads
- Removed: Reinforced Glass Pane (updating this would have required a complete rewrite and a big chunk of time. Our priorities are sadly not positioned here)
- Removed: Reinforced Dirt Slab (The slab was not working at all and despite tons of debugging and checking, we could not iron out the issue)
