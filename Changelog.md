--------------------------Changelog for the next version of SecurityCraft--------------------------

- New: The Keycard Reader, Keypad, and Retinal Scanner can now be placed facing up or down (Thanks CYB3RCA4T!)
- New: The /sc owner subcommand now has an optional "retainData" mode to prevent affected blocks from losing their data when their owner is changed
- Change: Several entries within the SecurityCraft manual have been updated to remove outdated descriptions
- API: New method Owner#isTreatedTheSameAs to check if everything owned by one owner is also owned by the other owner
- Fix: Some Briefcase and Keycard interactions do not respect whether the player is wearing an Incognito Mask
- Fix: The Block Change Detector cannot open reinforced doors/trapdoors/etc. or interact with secure redstone interfaces (Thanks CYB3RCA4T!)
- Fix: Server crash involving display cases
- Fix: Redstone Wire cannot be placed on top of reinforced hoppers
- Fix: Lecterns and Chiseled Bookshelves display their content incorrectly when they are reinforced using /sc convert
- Fix: There are two identical SecurityCraft Manual pages of the Projector
- Fix: Reinforced Lanterns and Reinforced Chains cannot be waterlogged by placing water into them using a bucket
- Fix: Some messages that are sent from a Keycard Lock mention a Keycard Reader as the source block

--------------------------Changelog for v1.10.1-beta3 of SecurityCraft--------------------------

- Fix: The Frame feed is sometimes rendered with partly transparent pixels

--------------------------Changelog for v1.10.1-beta2 of SecurityCraft--------------------------

- Fix: The Frame feed doesn't display correctly