--------------------------Changelog for v1.9 of SecurityCraft--------------------------

- New: Reinforced Blocks: Deepslate, Cobbled Deepslate, Polished Deepslate, Calcite, Tuff, Dripstone Block, Rooted Dirt, Block of Raw Iron, Block of Raw Copper, Block of Raw Gold, Block of Amethyst, Block of Copper, Exposed Copper, Weathered Copper, Oxidized Copper, Cut Copper, Exposed Cut Copper, Weathered Cut Copper, Oxidized Cut Copper, Cut Copper Stairs, Exposed Cut Copper Stairs, Weathered Cut Copper Stairs, Oxidized Cut Copper Stairs, Cut Copper Slab, Exposed Cut Copper Slab, Weathered Cut Copper Slab, Oxidized Cut Copper Slab, Tinted Glass, Smooth Basalt, Deepslate Bricks, Cracked Deepslate Bricks, Deepslate Tiles, Cracked Deepslate Tiles, Chiseled Deepslate, Cobbled Deepslate Stairs, Polished Deepslate Stairs, Deepslate Brick Stairs, Deepslate Tile Stairs, Cobbled Deepslate Slab, Polished Deepslate Slab, Deepslate Brick Slab, Deepslate Tile Slab, Moss Carpet, Moss Block, Cobbled Deepslate Wall, Polished Deepslate Wall, Deepslate Brick Wall, Deepslate Tile Wall
- New: Completely overhauled the overwhelming majority of SecurityCraft's textures. Direct feedback @ChainmailPickaxe in #sc-talk on our Discord server
- New: Reinforced Pistons
- New: The Reinforced Redstone Block and the Reinforced Observer can now be used to open doors, activate pistons, etc.
- New: Block Mines for Deepslate, Cobbled Deepslate, Copper Ore, and all deepslate ores
- New: The debug menu now hides block mines, disguised blocks, and fake fluids
- New: The Protecto and Trophy System now support the Disguise Module
- New: Key Panel Block. A waterloggable, smaller version of the keypad. Place it by rightclicking a Key Panel
- New: Config option "enableTeamOwnership". Setting this to true will allow all players in the same scoreboard team to own each other's blocks
- New: The Allowlist and Denylist Module can now be set to affect every player instead of just those added to the list
- New: Security Cameras can now open Reinforced Doors when a Redstone Module is installed and the signal is activated
- New: The Reinforced Hopper can now extract from the Inventory Scanner
- New: The message SecurityCraft sends when joining a world can now be turned off by server owners
- New: Players can now see themselves when viewing a camera, which also means they're now vulnerable to attacks while doing so
- New: Indicator on security cameras that turns on when someone is viewing the camera
- New: Config option "trickScannersWithPlayerHeads" to allow players to wear a player's head to trick that player's retinal scanner or scanner door into activating
- Change: SecurityCraft's config values for Jade/HWYLA are now synchronized from server to client, meaning server owners can now control what Jade shows to players
- Change: Renamed "Track Mine" to "Rail Mine" to be in line with vanilla
- Change: Cameras can now be viewed from anywhere in the same dimension, it is no longer necessary to be close to the camera to be able to view it
- Change: The Smart Module no longer works in the camera, the Allowlist Module is now used for better control
- API: Removed INameable in favor of vanilla's Nameable
- API: Added INameSettable to be able to easily set a block's custom name
- API: Added IViewActivated (used by the Retinal Scanner and Scanner Door)
- API: Renamed SecurityCraftBlockEntity to NamedBlockEntity and remove most of its code
- API: Removed IIntersectable
- API: Added LinkableBlockEntity and removed relevant code from CustomizableBlockEntity
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
- Removed: Unused textures
- Misc.: Renamed a few textures
- Misc.: Performance improvements due to fewer ticking block entities
- Misc.: The minimum Forge version is now 37.1.0

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