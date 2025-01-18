--------------------------Changelog for the upcoming version of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- Change: Camera model animations are now synchronized between players
- Fix: The display of items in the SC Manual can change too fast in certain situations
- Fix: Potential startup crash

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