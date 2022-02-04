Incorporeal 3
=============

Magic mod addon in which an old dragon yells at a corporea spark. 1.18, Forge and Fabric.

Incorporeal 2 (1.16/Forge) is [here](https://github.com/quat1024/incorporeal-2-forge), Incorporeal 1 (1.12/Forge) is [here](https://github.com/quat1024/incorporeal).

## Things that have been ported

* corporea solidifier
* corporea ticket
* corporea retainer evaporator
* red string liar
* item frame tinkerer
* unstable cubes
* Sanvocalia
* The funny
* natural redstone circuitry

## Things left to port

* soul cores (ender/~~corporea~~/potion soul core)
* ticket conjurer
* rod of the fractured space
* the datapack (for now i will probably cut and paste it from 1.16)
* the lexicon (same)
* plantable redstone roots

## Things left to write

* Computer mod :)
* i want more soul cores lmao

## Things that will not be ported

* frame screw
  * I don't like it and didn't port it for 1.16 either
  * n.b., computermod can probably read/write its rotation
    * will need a mixin to make it properly serialize rotation when the frame is empty
* Rhododendrite
  * I want to go in a different direction
  * will be integrated into the main addon, and not its own thing
* Corporea Soul Core
  * I still like this idea a lot, but want to make it much cheaper to access
  * (tentatively) just put a corporea spark on a player head

# Build information

Ok so, this is by far the most cursed buildscript i've had the pleasure of messing with.

See:
* `buildSrc/src/main/kotlin/incorporeal.java-conventions.gradle.kts` for settings common to all artifacts
* The other files in that folder, for settings common to Fabricness, Forgeness, or commonness
* `(Common|Fabric|Forge)/build.gradle.kts` for settings relevant to "being a Botania addon"
* `gradle.properties` for build variables like name and version number

Keep in mind:
* It's currently open-season for Botania API breaks, and the `1.18.1-428-SNAPSHOT` version I'm depending on right now is literally just `HEAD`.
  * Builds are not reproducible and may break at any time.
  * Life on the edge! ;)
* I prefer to develop on Fabric first and fix Forge later.

Having weird build issues:
* If `/build` cropped up again (not `/Fabric/build`, just `/build`), yeet it
* Try `./gradlew --refreshDependencies`
* ensure you have the same version of botania-fabric/botania-forge/botania-xplat i.e. check that the jenkins didnt get clogged again

If you're using Eclipse:
* Good luck!

(I heard turning off the Kotlin integration and letting buildship treat it as a black-box might help?)

## things i need to add

* mixin jsons, naming mixin jsons in fabric.mod.json and mixingradle block
* run configurations
  * figure out where the fabric run configs went lol
* a place for generated resources to go, if i end up using a data gen 
* TEST IT MORE, test remapping, test refmaps, test test
* non-`Common` `-sources` jars don't contain `Common` sources - hubry said this happens to patchy and botania too
* What's the difference between depending on :Common and adding its source set to the compilation classpath. do you need to do both
* Data generators would be nice to have, rn i merged everything into the regular assets folder

### Why are you using kotlin buildscripts

Just to feel something.

I also wanted to learn a little more about Gradle, and the additional type-safety and go-to-definition support sometimes helps to understand what is actually happening.

# License

LGPL-3.0-or-later