package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.block.IncBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod("incorporeal")
public class IncForge {
	public IncForge() {
		//blocks
		bind(ForgeRegistries.BLOCKS, IncBlocks::register);
		
		//items
		bind(ForgeRegistries.ITEMS, IncItems::register);
		
		//block entity types
		bind(ForgeRegistries.BLOCK_ENTITIES, IncBlockEntityTypes::register);
		
		//sound events
		bind(ForgeRegistries.SOUND_EVENTS, IncSounds::register);
		
		//I gotta admit. Fabric's "client entrypoint" scheme is a lot nicer than forge's "proxy" pattern
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> IncForgeClient::init);
		
		//some other stuff (not different between loaders)
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> Inc.registerExtraThings());
	}
	
	//botania copypasterino !
	private static <T extends IForgeRegistryEntry<T>> void bind(IForgeRegistry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(registry.getRegistrySuperType(),
			(RegistryEvent.Register<T> event) -> {
				IForgeRegistry<T> forgeRegistry = event.getRegistry();
				source.accept((t, rl) -> {
					t.setRegistryName(rl);
					forgeRegistry.register(t);
				});
			});
	}
}