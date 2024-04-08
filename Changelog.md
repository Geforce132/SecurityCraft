--------------------------Changelog for v1.9.10 of SecurityCraft--------------------------

- Change: The cameraSpeed client side config setting has been moved to be a per-block option, accessible with the Universal Block Modifier
- API: Changed constructors for IntOption and DoubleOption, they are now always sliders by default
- API: Removed FloatOption. Use DoubleOption instead
- Fix: Trying to place a Panic Button on top of powdered snow crashes the game
- Fix: Occasional crash when opening the inventory in creative mode in certain situations

--------------------------Changelog for v1.9.9-beta2 of SecurityCraft--------------------------

- Fix: The block display in disguise modules and projectors is rendered too dark

--------------------------Changelog for v1.9.9-beta1 of SecurityCraft--------------------------

- Change: The "taser_effects" and "powered_taser_effects" config settings have been moved to the item component "minecraft:potion_contents" on the taser
- Fix: The Mine/Sentry Remote Access Tool and Sonic Security System neither save nor respect the level of the bound objects
- Misc.: The minimum required NeoForge version is 20.6.14-beta