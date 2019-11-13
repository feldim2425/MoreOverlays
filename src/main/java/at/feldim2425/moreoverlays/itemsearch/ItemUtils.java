package at.feldim2425.moreoverlays.itemsearch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public final class ItemUtils {

	public static boolean ingredientMatches(Object ingredient, ItemStack stack){
		if (ingredient instanceof ItemStack) {
			ItemStack stack1 = (ItemStack) ingredient;
			return stack1.isItemEqualIgnoreDurability(stack) && JeiModule.areItemsEqualInterpreter(stack1, stack);
		}
		else if(ingredient instanceof EnchantmentData){
			ListNBT tags;
			if (stack.getItem() instanceof EnchantedBookItem) {
				tags = EnchantedBookItem.getEnchantments(stack);
			}
			else {
				tags = stack.getEnchantmentTagList();
			}
			return getEnchantmentData(tags).stream().anyMatch((ench)-> ench.enchantment.equals(((EnchantmentData) ingredient).enchantment) &&
					ench.enchantmentLevel == ((EnchantmentData) ingredient).enchantmentLevel);
		}

		return false;
	}

	public static Collection<EnchantmentData> getEnchantmentData(@Nullable ListNBT nbtList) {
		if(nbtList == null){
			return Collections.emptySet();
		}

		Collection<EnchantmentData> enchantments = new HashSet<>();
		for (INBT nbt : nbtList) {
			if (nbt instanceof CompoundNBT) {
				CompoundNBT nbttagcompound = (CompoundNBT) nbt;
				int id = nbttagcompound.getShort("id");
				int level = nbttagcompound.getShort("lvl");
				Enchantment enchantment = Enchantment.getEnchantmentByID(id);
				if (enchantment != null && level > 0) {
					enchantments.add(new EnchantmentData(enchantment, level));
				}
			}
		}
		return enchantments;
	}

	public static boolean matchNBT(ItemStack a, ItemStack b) {
		return a.hasTag() == b.hasTag() && (!a.hasTag() || a.getTag().equals(b.getTag()));
	}

	private ItemUtils(){
		//EMPTY
	}
}
