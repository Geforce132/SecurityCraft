--------------------------Changelog for v1.9.4 of SecurityCraft--------------------------

- New: Japanese translation (Thanks momo-i!)
- New: Taser effects and damage are now configurable
- New: The Block Change Detector can now show highlights in the world, marking where it detected block changes
- New: Ability to change a block pocket's outline color
- New: The I.M.S. is now waterloggable
- New: Scrolling in the SecurityCraft Manual while holding the control key will now scroll through subpages (if any exist)
- New: Rift Stabilizer, a block that detects and prevents teleportation attempts in its vicinity
- New: Display Case. Securely display your items
- New: Glow Display Case
- Change: The Laser Block now needs a Redstone Module in order to emit a redstone signal when someone walks through the laser
- Change: Most of SecurityCraft's screens are now no longer pausing the game when open, and can now be closed with the "Open/Close Inventory" key
- API: Changed LinkedAction to an interface and the enum values to records for easier usability
- API: Several methods in LinkableBlockEntity were changed to account for the LinkedAction change
- API: LinkedAction has been renamed to ILinkedAction
- Fix: Nether portals can replace reinforced blocks when generating
- Fix: Crash when trying to disguise a block as/project some blocks that don't always have a block entity associated with them
- Fix: Can't place blocks on blocks locked by a Sonic Security System
- Fix: Interacting with blocks while holding a Sonic Security System may sometimes place it for a short while
- Fix: A Sentry's name does not show in the Sentry Remote Access Tool
- Fix: Mobs can spawn on reinforced ice and reinforced iron trapdoors
- Fix: Sentry bullets can remove item frames
- Fix: Block Pocket Manager's size button and offset slider are available to non-owners
- Fix: Password-protected furnaces close even if some players still have it open
- Fix: Tooltips in some screens can overlap when tabbing through buttons while hovering over other areas that show a tooltip
- Fix: The Harming Module does not work when a Laser Block is powered
- Fix: The Sonic Security System has no placing sound
- Fix: Several issues in various language files
- Fix: Some reinforced blocks can be destroyed by unintended vanilla means

--------------------------Changelog for v1.9.3.1 of SecurityCraft--------------------------

- Change: The default range of the IMS has been increased from 12 to 15 blocks
- Fix: Newly placed Sentries do not work. To fix non-working sentries, right-click them with redstone
- Misc.: The French translation has been updated to address the new content from v1.9.3

--------------------------Changelog for v1.9.3 of SecurityCraft--------------------------

- New: Some of SecurityCraft's blocks, items, and entities can now trigger sculk sensors
- New: Items can now be dragged from JEI into the Inventory Scanner's ghost slots
- New: Item and block tag "securitycraft:reinforced/terracotta"
- New: Reinforced sand has been added to the "minecraft:sand" tag
- New: Several blocks now have a new Universal Block Modifier option to disable them
- New: Blocks that have been linked to a Sonic Security System can now be seen and removed in the item's and block's screen
- Change: Reduced the Rail Mine's explosion size by 25% to make it comparable to other mines' explosions
- Change: Blocks that entities can intersect with, such as Laser and Inventory scanner fields, mines and the Cage Trap, now only trigger when the entity touches their bounding box
- API: Added DisabledOption for having a default option to disable blocks
- API: Added Option#getKey and Option#getDescriptionKey to easily access the language keys associated with an option
- API: IEMPAffected has been added. It can be used to shut down select SecurityCraft blocks and entities when they're within range of an EMP blast
- API: IEMPAffectedTE has been added as a default implementation for tile entities which want to implement IEMPAffected
- Fix: Water doesn't render correctly while a Sonic Security System is recording within the player's view
- Fix: The reinforced stone pressure plate is not in SecurityCraft's "securitycraft:reinforced/pressure_plates" tag
- Fix: Blocks disguised as a translucent block, like stained glass, don't show up properly
- Fix: The SecurityCraft Manual shows the info for secret signs on the keycard page (Thanks cdgamedev!)

--------------------------Changelog for v1.9.2 of SecurityCraft--------------------------

- New: Block Change Detector. Detects and logs players breaking and/or placing blocks in its vicinity
- New: Codebreakers can now hack Keycard Readers
- New: Item tag "securitycraft:can_interact_with_doors" to control which items can interact with reinforced doors, scanner doors, keypad doors, and reinforced iron trapdoors
- New: Item tag "securitycraft:modules", containing all of SecurityCraft's modules
- New: Config option "codebreaker_chance" to set the chance of the codebreaker successfully hacking a block
- New: The Allowlist and Denylist Module can now contain teams, meaning every player on a team that is on the list will be allowed/denied
- New: Modules can now be toggled off or on without removing them from the block, by clicking the respective button in the Universal Block Modifier screen
- New: Reinforced End Rod
- New: Reinforced Glazed Terracotta
- New: The Projector can now be placed on the ceiling, allowing for downwards projection
- New: All disguisable blocks (those that accept the Disguise Module) are now waterloggable, allowing for waterlogged disguises
- Change: Codebreakers will now always open briefcases when in Creative mode without a chance of failing
- Change: The Sentry's body is now solid if the sentry is not disguised
- Change: The Fake Lava/Water Buckets are now created in a brewing stand, by putting the potion in the top slot, and the bucket(s) in the bottom slot(s)
- Change: Sentries can now be placed in water
- Change: Massively increased explosion resistance of all blocks to hopefully prevent them from being broken by modded explosions
- Change: Placing a Laser Block and Inventory Scanner now also places the fields if replaceable blocks (like grass or snow) are in the way
- API: Split IPasswordProtected into two interfaces (IPasswordProtected and ICodebreakable), meaning blocks can now be hacked by the codebreaker without needing to be password protected
- API: Removed IPasswordProtected#isCodebreakable
- API: New linked action: OWNER_CHANGED
- Fix: The recipe for reinforced glass panes is incorrect
- Fix: Breaking the block another block with modules is attached to does not drop the modules
- Fix: Compatibility issues with other mods that add overlays (e.g. Stylish Effects)
- Fix: Some logs have an incorrect side texture
- Fix: Reinforced Iron Trapdoor textures do not rotate and aren't oriented the same way open as closed
- Fix: Reinforced Ice blocks melt unintentionally
- Fix: Disguised blocks from sentries can be retrieved by using pistons or other means
- Fix: The Admin Tool and Universal Key Changer do not work on doors
- Fix: The Keypad Door can only be accessed with an empty hand
- Fix: Several blocks do not respect team ownership
- Fix: Server crash involving automatically building a Block Pocket
- Fix: Tricking scanners with player heads does not work if the player head owner is on the allowlist of the block
- Fix: The Password-protected Furnace/Smoker/Blast Furnace don't have a closing sound
- Fix: When placing a Password-protected Chest next to another one creating a double chest, modules and options are not synchronized to the newly placed one
- Fix: The Reinforced Cobweb can be removed by flowing fluids
- Fix: The Reinforced Cobweb can be destroyed by hand as well as explosions
- Fix: Disguisable blocks that are disguised as blocks that emit light (e.g. Glowstone) don't emit light
- Fix: The Motion Activated Light is constantly turned on when placed near a Sentry or armor stand
- Fix: Prohibited items get removed from an Inventory Scanner if a Storage Module is added/removed
- Fix: Changing the owner of an Inventory Scanner doesn't change the owner of the corresponding fields
- Fix: Taking out modules from some blocks voids the module
- Fix: Changing the owner of a double Password-protected Chest does not change the owner of the other half
- Fix: Changing the owner of a Laser Block does not change the owner of connected laser blocks
- Fix: Adding a module to a Laser Block by rightclicking the block does not add the module to connected laser blocks
- Fix: Module dupe involving doors
- Fix: It's possible to connect one's own Password-protected Chest to that of another owner
- Fix: Players hear a constant fire extinguishing sound when being in fake lava while it's raining
- Fix: Alarm sound plays from the player's position instead of the alarm's position
- Removed: Some unnecessary menu types. This may result in a "missing registry entries" message showing up, which can be accepted
- Removed: "allowCodebreakerItem" config option. Disabling the Codebreaker is now achieveable by setting "codebreaker_chance" to a negative value
- Misc.: More texture and model changes

--------------------------Changelog for v1.9.1 of SecurityCraft--------------------------

- New: Password-protected Smoker and Password-protected Blast Furnace
- New: Smoker and Blast Furnace mines
- New: The Disguise Module and Projector can now be set to display a specific state of a block (e.g. corner stairs), instead of a fixed one
- Change: The Password-protected Furnace now has a new look
- Change: The Cage Trap is now reusable
- Change: The Admin Tool is now enabled by default. This does not affect existing config files
- Fix: Cannot access the recipe book for the Password-protected Furnace
- Fix: The Password-protected Furnace is not shown as a recipe catalyst for furnace recipes in JEI
- Fix: Changing the owner of inventory scanners and doors does not change the owner of the other block/door half
- Fix: Projecting disguisable blocks (those that support the disguise module) does not work
- Fix: Crash when trying to place a door one block below the build height
- Fix: Door item gets removed when trying to place a door outside the build height
- Fix: Some blocks' settings/inventory don't properly save
- Fix: Fake Water/Lava bucket fill sounds don't play
- Fix: Disguising blocks using certain blocks with block entity renderers (like signs and banners) doesn't work
- Fix: It's possible to jump over disguisable blocks that are disguised as fences or walls
- Fix: Attempting to put more than one item into the Projector's slot swallows extra items
- Fix: Sentries can target entities they cannot see
- Fix: The Sentry's bounding box (F3+B) is not displayed correctly
- Fix: The patron list in the SecurityCraft Manual is only capable of showing 33 patrons
- Fix: Patron name tooltips in the SecurityCraft Manual can extend over the border of the Minecraft window
- Fix: The Codebreaker does not work on keypad doors
- Fix: The Mine Remote Access Tool does not work for mines placed at x=0, y=0, z=0
- Fix: The Sentry Remote Access Tool does not work for sentries placed at y=0
- Fix: The Portable Tune Player plays the saved tune slightly slower than the Sonic Security System
- Fix: Navigating the SecurityCraft Manual via the tab key does not visibly select any button
- Fix: Block mines do not drop themselves when mined
- Fix: Block mines cannot be mined quickly with the appropriate tool of their vanilla counterpart
- Fix: The mine overlay on the Ancient Debris Mine item can be seen by other players when holding the item
- Fix: Removing a laser field with the Universal Block Remover does not destroy adjacent laser fields
- Fix: Crash when placing a Sonic Security System inside of any replaceable block
- Fix: Sentries ignore the Allowlist Module's "Affect every player" setting
- Misc.: More texture changes and file renames, resource packs may need to be updated

--------------------------Changelog for v1.9.0.2 of SecurityCraft--------------------------

- Fix: Item duplication bug involving reinforced hoppers
- Fix: Crash when opening the Password-protected Furnace
- Fix: Log error when changing "solidifyField" option in an Inventory Scanner that's not linked to another one
- Fix: Sound for converting chests/furnaces to their password-protected variants does not play
- Fix: Frame has stone sounds instead of metal sounds

--------------------------Changelog for v1.9.0.1 of SecurityCraft--------------------------

- Fix: Copper -> Cut Copper stonecutting recipes yield incorrect amounts
- Fix: Possible incompatibilities with other mods that use mixins to modify the same code as SecurityCraft (known: Magnesium, Immersive Portals)

--------------------------Changelog for v1.9 of SecurityCraft--------------------------

- New: Reinforced Blocks: Deepslate, Cobbled Deepslate, Polished Deepslate, Calcite, Tuff, Dripstone Block, Rooted Dirt, Block of Raw Iron, Block of Raw Copper, Block of Raw Gold, Block of Amethyst, Block of Copper, Exposed Copper, Weathered Copper, Oxidized Copper, Cut Copper, Exposed Cut Copper, Weathered Cut Copper, Oxidized Cut Copper, Cut Copper Stairs, Exposed Cut Copper Stairs, Weathered Cut Copper Stairs, Oxidized Cut Copper Stairs, Cut Copper Slab, Exposed Cut Copper Slab, Weathered Cut Copper Slab, Oxidized Cut Copper Slab, Tinted Glass, Smooth Basalt, Deepslate Bricks, Cracked Deepslate Bricks, Deepslate Tiles, Cracked Deepslate Tiles, Chiseled Deepslate, Cobbled Deepslate Stairs, Polished Deepslate Stairs, Deepslate Brick Stairs, Deepslate Tile Stairs, Cobbled Deepslate Slab, Polished Deepslate Slab, Deepslate Brick Slab, Deepslate Tile Slab, Moss Carpet, Moss Block, Cobbled Deepslate Wall, Polished Deepslate Wall, Deepslate Brick Wall, Deepslate Tile Wall
- New: Completely overhauled the overwhelming majority of SecurityCraft's textures. Direct feedback @ChainmailPickaxe in #sc-talk on our Discord server
- New: Reinforced Pistons
- New: The Reinforced Redstone Block and the Reinforced Observer can now be used to open doors, activate pistons, etc.
- New: Block Mines for Deepslate, Cobbled Deepslate, Copper Ore, and all deepslate ores
- New: The debug menu now hides block mines, disguised blocks, and fake fluids
- New: The Protecto, Trophy System, and Password-protected Furnace now support the Disguise Module
- New: Key Panel Block. A waterloggable, smaller version of the keypad. Place it by rightclicking a Key Panel
- New: Config option "enableTeamOwnership". Setting this to true will allow all players in the same scoreboard team to own each other's blocks
- New: The Allowlist and Denylist Module can now be set to affect every player instead of just those added to the list
- New: Security Cameras and Portable Radars can now open Reinforced Doors when a Redstone Module is installed and the signal is activated
- New: The Reinforced Hopper can now extract from the Inventory Scanner
- New: The message SecurityCraft sends when joining a world can now be turned off by server owners
- New: Players can now see themselves when viewing a camera, which also means they're now vulnerable to attacks while doing so
- New: Indicator on security cameras that turns on when someone is viewing the camera
- New: Config option "trickScannersWithPlayerHeads" to allow players to wear a player's head to trick that player's retinal scanner or scanner door into activating
- New: Sonic Security System. Lock your blocks behind a note block tune of your choice
- New: Portable Tune Player. Can play back tunes saved to sonic security systems
- Change: SecurityCraft's config values for Jade/HWYLA are now synchronized from server to client, meaning server owners can now control what Jade shows to players
- Change: Renamed "Track Mine" to "Rail Mine" to be in line with vanilla
- Change: Cameras can now be viewed from anywhere in the same dimension, it is no longer necessary to be close to the camera to be able to view it
- Change: The Smart Module no longer works in the camera, the Allowlist Module is now used for better control
- Change: Retinal Scanners and Scanner Doors now only get activated by looking at one of the sides that contain the actual scanner
- API: Removed INameable in favor of vanilla's Nameable
- API: Added INameSettable to be able to easily set a block's custom name
- API: Added IViewActivated (used by the Retinal Scanner and Scanner Door)
- API: Renamed SecurityCraftBlockEntity to NamedBlockEntity and remove most of its code
- API: Removed IIntersectable
- API: Added LinkableBlockEntity and removed relevant code from CustomizableBlockEntity
- API: Added ILockable which enables a block to be locked with the Sonic Security System
- Fix: Reinforced Doors cannot be opened in some cases (for example when a reinforced pressure plate is present on both sides)
- Fix: Alarm sound does not play
- Fix: Claymore does not explode
- Fix: Some blocks/items are not in the correct block/item tags
- Fix: The top half of the Keypad Door does not get affected by resource packs
- Fix: Jade display does not properly hide block mines
- Fix: Panic Button loses its owner when pressed
- Fix: Wrenches or similar of other mods are able to open doors
- Fix: Reinforcing blocks in the world may not work if some specific mods are installed
- Fix: Reinforced Smooth Quartz has the wrong texture
- Fix: The Keypad and Keypad Door cannot be opened while holding the key panel
- Fix: Converting chests whose loot has not been generated yet creates additional items
- Fix: Redstone does not connect to the Reinforced Observer
- Fix: Crash when trying to power a taser while holding redstone in the offhand
- Fix: Anyone can insert modules into any block
- Fix: Entity shadows do not render on block mines
- Fix: The security camera overlay still shows when the debug menu is open
- Fix: Block Mine explosions can create fake blocks
- Fix: Block Pockets get disabled when they get unloaded
- Fix: SecurityCraft Manual entries for doors are not properly showing all information
- Fix: The Universal Block Reinforcer can be thrown out of the player's inventory while its reinforcing menu is open
- Fix: Rail Mine loses its owner when its state changes
- Fix: Claymores, Motion Activated Lights, and Username Loggers don't ignore players in spectator mode
- Fix: Electrified Iron Fence Gate has wooden open/close sounds
- Fix: Offhand hotbar indicator for Camera Monitor/SRAT/MRAT shows incorrectly when the main hand is set to left
- Fix: Hotbar indicator for Camera Monitor/MRAT does not respect the player's reach distance
- Fix: Reinforced doors/trapdoors/etc. can be activated by anyone in some circumstances
- Fix: Some SecurityCraft blocks do not update their surrounding blocks correctly when powering/unpowering
- Removed: Unused textures
- Misc.: Renamed a few textures
- Misc.: Performance improvements due to fewer ticking block entities
- Misc.: The minimum Forge version is now 37.1.1

--------------------------Changelog for v1.8.23-beta5 of SecurityCraft--------------------------

- Fix: Crash on Forge 37.0.49 and newer
- Fix: Scrollable lists do not respond (affects Allow-/Denylist Modules, SecurityCraft Manual, Trophy System, Username Logger)
- Misc.: The minimum Forge version is now 37.0.50

--------------------------Changelog for v1.8.23-beta4 of SecurityCraft--------------------------

- Fix: Reinforced Water/Lava/Powder Snow Cauldrons do not show option and module descriptions correctly
- Fix: Reinforced Water/Lava/Powder Snow Cauldrons do not drop anything when broken with the Universal Block Remover

--------------------------Changelog for v1.8.23-beta3 of SecurityCraft--------------------------

- Misc.: Readd Just Enough Items integration

--------------------------Changelog for v1.8.23-beta2 of SecurityCraft--------------------------

- Fix: Keycard Reader crash
- Fix: The Inventory Scanner is invisible when placed/loaded
- Fix: SRAT/MRAT/Camera Monitor hotbar overlay is shown behind items

--------------------------Changelog for v1.8.23-beta1 of SecurityCraft--------------------------

- Misc.: The minimum Forge version is 37.0.33