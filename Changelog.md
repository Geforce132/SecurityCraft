--------------------------Changelog for v1.9.10 of SecurityCraft--------------------------

- Change: The cameraSpeed client side config setting has been moved to be a per-block option, accessible with the Universal Block Modifier
- Change: Some SecurityCraft tip messages have been reworded for clarity
- Change: Increased suffocation damage inside reinforced blocks no longer affects non-player entities and players owning the reinforced blocks
- Change: The "preventReinforcedFloorGlitching" configuration option no longer affects players trying to glitch through reinforced blocks that they are the owner of
- API: Changed constructors for IntOption and DoubleOption, they are now always sliders by default
- API: Removed FloatOption. Use DoubleOption instead
- Fix: Trying to place a Panic Button on top of powdered snow crashes the game
- Fix: Occasional crash when opening the inventory in creative mode in certain situations
- Fix: Reinforced fence gates don't properly retain their owner when reloading the world
- Fix: The debug world does not work with SecurityCraft installed
- Fix: The block pocket can be assembled without the necessary items

--------------------------Changelog for v1.9.9-beta2 of SecurityCraft--------------------------

- Fix: The block display in disguise modules and projectors is rendered too dark

--------------------------Changelog for v1.9.9-beta1 of SecurityCraft--------------------------

- Change: The "taser_effects" and "powered_taser_effects" config settings have been moved to the item component "minecraft:potion_contents" on the taser
- Fix: The Mine/Sentry Remote Access Tool and Sonic Security System neither save nor respect the level of the bound objects
- Misc.: The minimum required NeoForge version is 20.6.14-beta