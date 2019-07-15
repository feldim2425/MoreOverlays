package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class ItemUtils {

	public static boolean ingredientMatches(Object ingredient, ItemStack stack){
		if (ingredient instanceof ItemStack) {
			ItemStack stack1 = (ItemStack) ingredient;
			return stack1.isItemEqualIgnoreDurability(stack) && JeiModule.areItemsEqualInterpreter(stack1, stack);
		}
		else if(ingredient instanceof EnchantmentData){
			NBTTagList tags;
			if (stack.getItem() instanceof ItemEnchantedBook) {
				tags = ItemEnchantedBook.getEnchantments(stack);
			}
			else {
				tags = stack.getEnchantmentTagList();
			}
			return getEnchantmentData(tags).stream().anyMatch((ench)-> ench.enchantment.equals(((EnchantmentData) ingredient).enchantment) &&
					ench.enchantmentLevel == ((EnchantmentData) ingredient).enchantmentLevel);
		}

		return false;
	}

	public static Collection<EnchantmentData> getEnchantmentData(@Nullable NBTTagList nbtList) {
		if(nbtList == null){
			return Collections.emptySet();
		}

		Collection<EnchantmentData> enchantments = new HashSet<>();
		for (NBTBase nbt : nbtList) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound nbttagcompound = (NBTTagCompound) nbt;
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

	public boolean matchNBT(ItemStack a, ItemStack b) {
		if (!Config.itemsearch_matchNbt.contains(a.getItem().getRegistryName() == null ? "" : a.getItem().getRegistryName().toString())) {
			return true;
		}
		return a.hasTagCompound() == b.hasTagCompound() && (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound()));
	}

	private ItemUtils(){
		//EMPTY
	}
}
