package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.IncBlockEntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.ModSubtiles;
import vazkii.botania.common.block.tile.ModTiles;

@Mixin(ModSubtiles.class)
public class ModSubtilesMixin {
	/**
	 * This is a really terrible mixin that I should probably get rid of, it's hooking into Botania's cross-loader
	 * capability system because I'm too lazy to do the AttachCapabilitiesEvent myself.
	 */
	@Inject(
		method = "registerWandHudCaps",
		at = @At("TAIL"),
		remap = false
	)
	private static void onRegisteringWandHudCaps(ModTiles.BECapConsumer<IWandHUD> consumer, CallbackInfo ci) {
		consumer.accept((be) -> new TileEntityFunctionalFlower.FunctionalWandHud<>((TileEntityFunctionalFlower) be),
			IncBlockEntityTypes.SANVOCALIA_BIG,
			IncBlockEntityTypes.SANVOCALIA_SMALL,
			IncBlockEntityTypes.FUNNY_BIG,
			IncBlockEntityTypes.FUNNY_SMALL
		);
	}
}
