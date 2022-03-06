package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.mixin.AccessorBlockModelGenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IncCommonModelsAndBlockstates {
	//Like botania BlockstateProvider
	private static final List<BlockStateGenerator> stateGenerators = new ArrayList<>();
	private static final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
	private static final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = models::put;
	
	public static void doIt(DataGenerator generator, Consumer<JsonFile> files) {
		//Corporetics
		itemBlockModelParent(IncBlocks.CORPOREA_SOLIDIFIER);
		itemBlockModelParent(IncBlocks.FRAME_TINKERER);
		itemBlockModelParent(IncBlocks.RED_STRING_LIAR);
		
		itemGenerated(IncItems.FRACTURED_SPACE_ROD, Inc.id("item/fractured_space_rod/tex"));
		itemGenerated(IncItems.TICKET_CONJURER, Inc.id("item/ticket_conjurer/tex"));
		
		//Soul cores
		singleVariantParticleOnly(IncBlocks.ENDER_SOUL_CORE, Inc.id("entity/ender_soul_core"));
		itemBlockRotationsBuiltinEntity(IncBlocks.ENDER_SOUL_CORE);
		singleVariantParticleOnly(IncBlocks.POTION_SOUL_CORE, Inc.id("entity/potion_soul_core"));
		itemBlockRotationsBuiltinEntity(IncBlocks.POTION_SOUL_CORE);
		
		itemBlockRotationsBuiltinEntity(IncItems.SOUL_CORE_FRAME);
		
		//Natural devices
		//(these are done manually at the moment)
		
		//Flowers
		flower(IncBlocks.SANVOCALIA, Inc.id("block/sanvocalia/big"));
		flower(IncBlocks.SANVOCALIA_SMALL, Inc.id("block/sanvocalia/small"));
		flower(IncBlocks.FUNNY, Inc.id("block/funny/thisissosad"));
		flower(IncBlocks.FUNNY_SMALL, Inc.id("block/funny/alexaplaydespacito"));
		floatingFlower(IncBlocks.FLOATING_SANVOCALIA, IncBlocks.SANVOCALIA);
		floatingFlower(IncBlocks.FLOATING_SANVOCALIA_SMALL, IncBlocks.SANVOCALIA_SMALL);
		floatingFlower(IncBlocks.FLOATING_FUNNY, IncBlocks.FUNNY);
		floatingFlower(IncBlocks.FLOATING_FUNNY_SMALL, IncBlocks.FUNNY_SMALL);
		
		//Unstable cubes
		for(Block cube : IncBlocks.UNSTABLE_CUBES.values()) {
			singleVariantParticleOnly(cube, Inc.id("entity/unstable_cube"));
			itemBlockRotationsBuiltinEntity(cube);
		}
		
		//Clearly
		singleVariantCubeColumn(IncBlocks.CLEARLY, Inc.id("block/clearly"), Inc.id("black"));
		itemBlockModelParent(IncBlocks.CLEARLY);
		
		//Taters (Temporary for now maybe)
		for(Block tater : IncBlocks.COMPRESSED_TATERS.values()) {
			stateGenerators.add(MultiVariantGenerator.multiVariant(tater, Variant.variant()
				.with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.tinyPotato))
			).with(AccessorBlockModelGenerators.horizontalDispatch()));
			itemBlockModelParent(tater, ModBlocks.tinyPotato);
		}
		
		//Computer
		
		//Aaaand write out all the files now
		stateGenerators.forEach(stateGenerator -> {
			ResourceLocation id = DataDsl.notAir(Registry.BLOCK.getKey(stateGenerator.getBlock()));
			files.accept(JsonFile.create(stateGenerator.get(), "assets", id.getNamespace(), "blockstates", id.getPath()));
		});
		
		models.forEach((id, elementSupplier) ->
			files.accept(JsonFile.create(elementSupplier.get(), "assets", id.getNamespace(), "models", id.getPath())));
	}
	
	public static void singleVariantBlockState(Block b, ResourceLocation model) {
		stateGenerators.add(MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, model)));
	}
	
	public static void singleVariantParticleOnly(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, ModelTemplates.PARTICLE_ONLY.create(b, TextureMapping.particle(texture), modelOutput));
	}
	
	public static void singleVariantCubeAll(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, ModelTemplates.CUBE_ALL.create(b, TextureMapping.cube(texture), modelOutput));
	}
	
	public static void singleVariantCubeColumn(Block b, ResourceLocation side, ResourceLocation end) {
		singleVariantBlockState(b, ModelTemplates.CUBE_COLUMN.create(b, TextureMapping.column(side, end), modelOutput));
	}
	
	public static final ModelTemplate crossTemplate = new ModelTemplate(Optional.of(Inc.botaniaId("block/shapes/cross")), Optional.empty(), TextureSlot.CROSS);
	public static void flower(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, crossTemplate.create(b, TextureMapping.cross(texture), modelOutput));
		itemGenerated(b, texture);
	}
	
	public static void floatingFlower(Block floating, Block nonFloating) {
		//brittle but might work
		ResourceLocation floatingModelId = DataDsl.prefixPath(Registry.BLOCK.getKey(floating), "block");
		ResourceLocation nonFloatingModelId = DataDsl.prefixPath(Registry.BLOCK.getKey(nonFloating), "block");
		
		//construct the model json manually (minecraft utils don't have any support for "loader")
		JsonObject json = new JsonObject();
		json.addProperty("parent", "minecraft:block/block");
		json.addProperty("loader", "botania:floating_flower");
		
		JsonObject flower = new JsonObject();
		flower.addProperty("parent", nonFloatingModelId.toString());
		json.add("flower", flower);
		
		//output it with the blockmodels
		modelOutput.accept(floatingModelId, () -> json);
		//add a blockstate pointing at it
		singleVariantBlockState(floating, floatingModelId);
		//and add an item model pointing at the block model
		itemBlockModelParent(floating);
	}
	
	public static void itemBlockModelParent(Block block) {
		itemBlockModelParent(block, block);
	}
	
	public static void itemBlockModelParent(ItemLike item, Block blockParent) {
		itemDelegatedTo(item, ModelLocationUtils.getModelLocation(blockParent));
	}
	
	public static void itemBlockRotationsBuiltinEntity(ItemLike item) {
		//hand-written json that contains the rotations from minecraft:block/block, but inherits from block:builtin/entity
		itemDelegatedTo(item, Inc.id("item/block_rotations_builtin_entity"));
	}
	
	public static void itemDelegatedTo(ItemLike item, ResourceLocation delegated) {
		modelOutput.accept(ModelLocationUtils.getModelLocation(item.asItem()), new DelegatedModel(delegated));
	}
	
	public static void itemGenerated(ItemLike b, ResourceLocation texture) {
		ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(b.asItem()), TextureMapping.layer0(texture), modelOutput);
	}
}
