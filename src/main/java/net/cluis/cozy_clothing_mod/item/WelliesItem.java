package net.cluis.cozy_clothing_mod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class WelliesItem extends ArmorItem {
    // Cache the last water state
    private boolean wasInShallowWater = false;

    public WelliesItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "cozy_clothing_mod:textures/models/armor/wellies_layer_1.png";
    }

    /**
     * Called every tick for items in inventory - hijacks Depth Strider enchantment
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        // Only process if this item is actually equipped as boots
        ItemStack equippedBoots = player.getItemBySlot(EquipmentSlot.FEET);
        if (equippedBoots != stack) {
            // If this stack is not the equipped boots, remove any fake enchantments
            if (wasInShallowWater) {
                removeFakeDepthStrider(stack);
                wasInShallowWater = false;
            }
            return;
        }

        // Now we know these wellies are equipped
        boolean currentlyInShallowWater = isInShallowWater(player, level);

        // Debug output every 20 ticks (once per second)
        if (level.getGameTime() % 20 == 0) {
            System.out.println("Wellies Debug - InWater: " + currentlyInShallowWater +
                    ", WasInWater: " + wasInShallowWater +
                    ", PlayerInWater: " + player.isInWater());
        }

        // Apply/remove fake Depth Strider enchantment
        if (currentlyInShallowWater && !wasInShallowWater) {
            applyFakeDepthStrider(stack);
            wasInShallowWater = true;
            System.out.println("Applied fake Depth Strider III to wellies!");
        } else if (!currentlyInShallowWater && wasInShallowWater) {
            removeFakeDepthStrider(stack);
            wasInShallowWater = false;
            System.out.println("Removed fake Depth Strider from wellies!");
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    /**
     * Adds a fake Depth Strider III enchantment to the boots
     */
    private void applyFakeDepthStrider(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag enchantments = tag.getList("Enchantments", 10);

        // Check if Depth Strider is already present
        boolean hasDepthStrider = false;
        for (int i = 0; i < enchantments.size(); i++) {
            CompoundTag enchant = enchantments.getCompound(i);
            String id = enchant.getString("id");
            if ("minecraft:depth_strider".equals(id) || "depth_strider".equals(id)) {
                // Update existing Depth Strider to level III
                enchant.putShort("lvl", (short) 3);
                hasDepthStrider = true;
                break;
            }
        }

        // If no Depth Strider exists, add it
        if (!hasDepthStrider) {
            CompoundTag depthStriderTag = new CompoundTag();
            depthStriderTag.putString("id", "minecraft:depth_strider");
            depthStriderTag.putShort("lvl", (short) 3);
            enchantments.add(depthStriderTag);
        }

        tag.put("Enchantments", enchantments);
        stack.setTag(tag);
    }

    /**
     * Removes the fake Depth Strider enchantment from the boots
     */
    private void removeFakeDepthStrider(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        ListTag enchantments = tag.getList("Enchantments", 10);

        // Remove Depth Strider enchantment
        for (int i = enchantments.size() - 1; i >= 0; i--) {
            CompoundTag enchant = enchantments.getCompound(i);
            String id = enchant.getString("id");
            if ("minecraft:depth_strider".equals(id) || "depth_strider".equals(id)) {
                enchantments.remove(i);
                break;
            }
        }

        tag.put("Enchantments", enchantments);
        stack.setTag(tag);
    }

    /**
     * Simplified water detection - any water contact
     */
    private boolean isInShallowWater(Player player, Level level) {
        // Just check if player is in any water
        boolean inWater = player.isInWater();

        // Debug output
        if (inWater) {
            System.out.println("Player in water - applying Depth Strider effect");
        }

        return inWater;
    }
}