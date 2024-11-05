# NoEmotecraft  
a.k.a. Emotecraft (EmoteX)

## Download
When downloading the mod, please **only use** official downloads as others may be infected.  
Official project (only download the mod from here):
- [GitHub/TheMek0/NoEmotecraft](https://github.com/TheMek0/NoEmotecraft) ([GitHub/KosmX/emotes](https://github.com/KosmX/emotes) for older versions)
- [Modrinth/NoEmotecraft](https://modrinth.com/plugin/noemotecraft/) ([Modrinth/Emotecraft](https://modrinth.com/mod/emotecraft) for older versions)
- [maven.kosmx.dev](https://maven.kosmx.dev/io/github/kosmx/emotes/) this is for developers.
**Don't download it from any other source!**

## Development
---
_WARN: building from repo is not available because it uses player-anim-core that was build on my pc_<br>
How to build
```bash
git clone https://github.com/TheMek0/NoEmotecraft.git (https://github.com/KosmX/emotes.git for older versions)
cd NoEmotecraft (emotes for older versions)
./gradlew build
```
You can use `collectArtifacts` task to copy the mod files into an artifacts directory.  
```bash
./gradlew collectArtifact
cd artifacts
```
  
### Using in your mod/modpack  

`Fabric` depends on [**bendy-lib**](https://github.com/KosmX/bendy-lib), optionally [**Mod Menu**](https://github.com/TerraformersMC/ModMenu) and FabricMC mods: **Fabric-loader**, **Fabric-API**, **Minecraft**.

Modules:
--------
`emotesAPI`: common library used by NoEmotecraft, has no dependencies, published as **emotesAPI** you can find it in my private maven server: [`https://maven.kosmx.dev`](https://maven.kosmx.dev)  
`executor`: the interface to be implemented to the modloader+MC version  
`emotesMain`: main client-side logic    
`emotesServer`: server-side logic    
<br>
`archCommon`: common Fabric Minecraft dependent stuff. Using [architectury](https://github.com/architectury/forgified-fabric-loom) loom  
`fabric`: latest fabric implementation  

### If you have any questions about the mod, you can find me on Discord
[![](https://img.shields.io/discord/737216980095991838?label=Discord)](https://discord.gg/6NfdRuE)
[![](https://img.shields.io/discord/1146015043335491624?label=Discord)](https://discord.gg/37XeetHexP)

