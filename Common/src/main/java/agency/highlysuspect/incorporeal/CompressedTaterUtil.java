package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import vazkii.botania.common.block.ModBlocks;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompressedTaterUtil {
	public static final Map<Integer, String> prefixes = new LinkedHashMap<>();
	public static final int SMALLEST = -4;
	public static final int LARGEST = 8;
	static {
		prefixes.put(-4, "tiny_tiny_tiny_tiny_");
		prefixes.put(-3, "tiny_tiny_tiny_");
		prefixes.put(-2, "tiny_tiny_");
		prefixes.put(-1, "tiny_");
		
		prefixes.put(1, "single_compressed_");
		prefixes.put(2, "double_compressed_");
		prefixes.put(3, "triple_compressed_");
		prefixes.put(4, "quadruple_compressed_");
		prefixes.put(5, "quintuple_compressed_");
		prefixes.put(6, "sextuple_compressed_");
		prefixes.put(7, "septuple_compressed_");
		prefixes.put(8, "octuple_compressed_");
	}
	
	public static String prefix(int compressionLevel, String s) {
		return prefixes.get(compressionLevel) + s;
	}
	
	public static TranslatableComponent formatCount(int compressionLevel, int root) {
		int num = (int) Math.pow(root, Math.abs(compressionLevel));
		String niceFormatted = String.format("%,d", num);
		
		if(compressionLevel < 0)
			return new TranslatableComponent("block.incorporeal.compressed_tiny_potatoes.tooltip.fraction", niceFormatted);
		else return new TranslatableComponent("block.incorporeal.compressed_tiny_potatoes.tooltip", niceFormatted);
	}
	
	//radius of each tater. default tater is 2/16 blocks wide
	public static float taterRadius(int compressionLevel) {
		if(compressionLevel > 0) {
			return Inc.rangeRemap(compressionLevel, 0, LARGEST, 2/16f, 1/2f);
		} else {
			//tater nuggets need different handling because they become negative sized lol
			return Inc.rangeRemap(compressionLevel, 0, SMALLEST, 2/16f, 0.01f);
		}
	}
	
	//how much the tiny potato block entity renderer should be scaled, before drawing it
	public static float taterScaleFactor(int compressionLevel) {
		return taterRadius(compressionLevel) * 8; //deeply magical number
	}
	
	public static Block getPotato(int compressionLevel) {
		if(compressionLevel == 0) return ModBlocks.tinyPotato;
		else return IncBlocks.COMPRESSED_TATERS.get(compressionLevel);
	}
}
