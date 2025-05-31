package net.cluis.cozy_clothing_mod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.UUID;

public class WelliesItem extends ArmorItem {
    // Unique UUID for the speed modifier - using a more descriptive name
    private static final UUID WELLIES_SPEED_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private static final String WELLIES_SPEED_NAME = "Wellies Shallow Water Speed";
    private static final double SPEED_BOOST_MULTIPLIER = 0.3; // 30% speed increase

    // Cache the last water state to avoid unnecessary attribute modifications
    private boolean wasInShallowWater = false;

    public WelliesItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "cozy_clothing_mod:textures/models/armor/wellies_layer_1.png";
    }

    /**
     * Called every tick for items in inventory - optimized to only check when boots are equipped
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        // Only process if this item is actually equipped as boots
        ItemStack equippedBoots = player.getItemBySlot(EquipmentSlot.FEET);
        if (equippedBoots != stack) {
            // If this stack is not the equipped boots, ensure no speed boost is applied
            if (wasInShallowWater) {
                removeSpeedBoost(player);
                wasInShallowWater = false;
            }
            return;
        }

        // Now we know these wellies are equipped
        boolean currentlyInShallowWater = isInShallowWater(player, level);

        // Only modify attributes when the state changes to avoid unnecessary operations
        if (currentlyInShallowWater && !wasInShallowWater) {
            applySpeedBoost(player);
            wasInShallowWater = true;
        } else if (!currentlyInShallowWater && wasInShallowWater) {
            removeSpeedBoost(player);
            wasInShallowWater = false;
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    /**
     * Apply speed boost using attribute modifiers
     */
    private void applySpeedBoost(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.getModifier(WELLIES_SPEED_UUID) == null) {
            AttributeModifier speedModifier = new AttributeModifier(
                    WELLIES_SPEED_UUID,
                    WELLIES_SPEED_NAME,
                    SPEED_BOOST_MULTIPLIER,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            speedAttribute.addPermanentModifier(speedModifier);
        }
    }

    /**
     * Remove speed boost
     */
    private void removeSpeedBoost(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(WELLIES_SPEED_UUID);
        }
    }

    /**
     * Enhanced shallow water detection
     * Checks if player is in water that's roughly 1 block deep
     */
    private boolean isInShallowWater(Player player, Level level) {
        BlockPos playerFeet = player.blockPosition();
        BlockPos playerHead = playerFeet.above();

        // Get fluid states at different positions
        FluidState fluidAtFeet = level.getFluidState(playerFeet);
        FluidState fluidAtHead = level.getFluidState(playerHead);

        // Check if player's feet are in water (using fluid tags for better compatibility)
        boolean feetInWater = fluidAtFeet.is(FluidTags.WATER) && fluidAtFeet.getAmount() >= 4; // At least half a block high

        // Check if player's head is NOT in deep water
        boolean headNotInDeepWater = !fluidAtHead.is(FluidTags.WATER) || fluidAtHead.getAmount() < 8; // Less than full block

        // Additional safety check: ensure player is actually touching water (not floating above)
        boolean actuallyTouchingWater = player.isInWater() || player.isUnderWater();

        // Player is in shallow water if:
        // 1. Their feet are in water (at least half a block)
        // 2. Their head is not in deep water (allowing for shallow splashing)
        // 3. They are actually touching water according to the game's detection
        return feetInWater && headNotInDeepWater && actuallyTouchingWater;
    }

    /**
     * Alternative method for even more precise shallow water detection
     * Uncomment and use this if you want stricter "exactly 1 block deep" behavior
     */
    /*
    private boolean isInExactlyOneBlockDeepWater(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();

        // Check water at player position
        FluidState playerFluid = level.getFluidState(playerPos);
        boolean playerInWater = playerFluid.is(FluidTags.WATER) && playerFluid.getAmount() >= 6;

        // Check no water above player
        FluidState aboveFluid = level.getFluidState(playerPos.above());
        boolean noWaterAbove = !aboveFluid.is(FluidTags.WATER) || aboveFluid.getAmount() < 2;

        // Check solid ground below (within 2 blocks)
        boolean solidGroundNearby = false;
        for (int i = 0; i <= 2; i++) {
            BlockPos checkPos = playerPos.below(i);
            if (level.getBlockState(checkPos).isSolidRender(level, checkPos)) {
                solidGroundNearby = true;
                break;
            }
        }

        return playerInWater && noWaterAbove && solidGroundNearby;
    }
    */
}