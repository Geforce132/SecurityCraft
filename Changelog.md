--------------------------Changelog for the upcoming version of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- New: All blocks can now be broken only by the owner with normal tools (axe, shovel, hoe, ...) and the Universal Block Remover has been disabled by default
- New: Config to re-enable the Universal Block Remover and disable normal block breaking
- New: Config to define tool requirement behavior (e.g. does Reinforced Stone always drop, or just when breaking it using a pickaxe?)
- New: Config to allow other players to break anyone's blocks
- New: Config for defining how much longer it should take to break another player's block compared to breaking one's own
- New: Reinforced Blocks: Pale Oak Log, Stripped Pale Oak Log, Pale Oak Wood, Stripped Pale Oak Wood, Pale Oak Planks, Pale Oak Stairs, Pale Oak Slabs, Pale Oak Button, Pale Oake Pressure Plate, Pale Oak Fence, Pale Oak Fence Gate, Pale Moss Block, Pale Moss Carpet
- New: Reinforced Blocks: Resin Block, Resin Bricks, Chiseled Resin Bricks, Resin Brick Slab, Resin Brick Stairs, Resin Brick Wall
- New: Secret Pale Oak (Hanging) Sign
- New: Pale Oak Security Sea Boat
- New: Creaking Heart Mine
- Change: Camera model animations are now synchronized between players
- API: New IBlockMine interface for blocks that are block mines
- API: New method IExplosive#explodesWhenInteractedWith as well as two utility methods
- Fix: The display of items in the SC Manual can change too fast in certain situations
- Fix: Potential startup crash
- Fix: The map color, instrument, and more properties of many reinforced blocks don't match their vanilla counterparts
- Fix: Several reinforced blocks can be broken by pistons
- Fix: Wall hanging signs have an incorrect translation key
- Fix: Security sea boats cannot be broken by players like normal boats
- Removed: Configuration "ableToBreakMines"

[1] Note: Frame blocks that already existed in the world prior to this update will lose their owner. These frames can be broken by anyone so they can be placed down with the proper owner again.

--------------------------Changelog for v1.9.12-beta3 of SecurityCraft--------------------------

- API: Added IModuleInventoryWithContainer to discern between module inventory and normal inventory of a block, without relying on vanilla's Container
- Fix: Compatibility with NeoForge 21.4.49-beta
- Fix: Text in the Inventory Scanner and edit module screen is not rendered correctly
- Misc.: The minimum required NeoForge version is now 21.4.49-beta

--------------------------Changelog for v1.9.12-beta2 of SecurityCraft--------------------------

- Fix: Text in the SC Manual is not rendered correctly
- Fix: Reinforced Mud Bricks and Blackstone Slab/Stairs don't look 100% like their vanilla counterparts
- Fix: Compatibility with NeoForge 21.4.30-beta
- Misc.: The minimum required NeoForge version is now 21.4.30-beta

--------------------------Changelog for v1.9.12-beta1 of SecurityCraft--------------------------

- Change: The block mine overlay now shows in the first person hand models as well
- Fix: Module descriptions of security sea boats do not show up correctly
- Fix: Holding a camera monitor makes it possible to identify disguised cameras
- Fix: Items that can have linked positions (e.g. Camera Monitor) show the idle animation even when they have positions and are either dropped, or held by a non-player entity
- Misc.: Moved display case textures to the `textures/entity/display_case/` subfolder and renamed them
- Misc.: Renamed track mine textures