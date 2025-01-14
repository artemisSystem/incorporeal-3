package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.computer.DataPrismBlockEntity;
import agency.highlysuspect.incorporeal.computer.DataStorageBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.Map;
import java.util.function.BiConsumer;

public class IncBlockEntityTypes {
	//corporetics
	public static final BlockEntityType<RedStringLiarBlockEntity> RED_STRING_LIAR =
		IXplatAbstractions.INSTANCE.createBlockEntityType(RedStringLiarBlockEntity::new, IncBlocks.RED_STRING_LIAR);
	
	//soul cores
	public static final BlockEntityType<EnderSoulCoreBlockEntity> ENDER_SOUL_CORE = 
		IXplatAbstractions.INSTANCE.createBlockEntityType(EnderSoulCoreBlockEntity::new, IncBlocks.ENDER_SOUL_CORE);
	public static final BlockEntityType<PotionSoulCoreBlockEntity> POTION_SOUL_CORE =
		IXplatAbstractions.INSTANCE.createBlockEntityType(PotionSoulCoreBlockEntity::new, IncBlocks.POTION_SOUL_CORE);
	
	//flowers
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_BIG =
		IXplatAbstractions.INSTANCE.createBlockEntityType(SanvocaliaBlockEntity::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA);
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_SMALL =
		IXplatAbstractions.INSTANCE.createBlockEntityType(SanvocaliaBlockEntity::small, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL);
	
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_BIG =
		IXplatAbstractions.INSTANCE.createBlockEntityType(FunnyBlockEntity::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY);
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_SMALL =
		IXplatAbstractions.INSTANCE.createBlockEntityType(FunnyBlockEntity::small, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL);
	
	//unstable cubes
	//Note - Only one BlockEntityRenderer can be assigned per block entity type.
	public static final Map<DyeColor, BlockEntityType<UnstableCubeBlockEntity>> UNSTABLE_CUBES = Inc.sixteenColors(color ->
		IXplatAbstractions.INSTANCE.createBlockEntityType(
			(pos, state) -> new UnstableCubeBlockEntity(color, pos, state), 
			IncBlocks.UNSTABLE_CUBES.get(color)));
	
	//computer
	public static final BlockEntityType<DataPrismBlockEntity> DATA_PRISM =
		IXplatAbstractions.INSTANCE.createBlockEntityType(DataPrismBlockEntity::new, IncBlocks.DATA_PRISM);
	public static final BlockEntityType<DataStorageBlockEntity> DATA_STORAGE =
		IXplatAbstractions.INSTANCE.createBlockEntityType(DataStorageBlockEntity::new, IncBlocks.DATA_STORAGE);
	
	public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		
		r.accept(ENDER_SOUL_CORE, Inc.id("ender_soul_core"));
		r.accept(POTION_SOUL_CORE, Inc.id("potion_soul_core"));
		
		r.accept(SANVOCALIA_BIG, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_small"));
		r.accept(FUNNY_BIG, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_small"));
		
		UNSTABLE_CUBES.forEach((color, type) -> r.accept(type, Inc.id(color.getName() + "_unstable_cube")));
		
		r.accept(DATA_PRISM, Inc.id("data_prism"));
		r.accept(DATA_STORAGE, Inc.id("data_storage"));
	}
}
