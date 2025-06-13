package net.cluis.cozy_clothing_mod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "cozy_clothing_mod")
public class WelliesItem extends ArmorItem {

    // Unique UUID for our swim speed modifier
    private static final UUID WELLIES_SWIM_SPEED_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final String WELLIES_SWIM_SPEED_NAME = "Wellies Swim Speed";

    public WelliesItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "cozy_clothing_mod:textures/models/armor/wellies_layer_1.png";
    }

    /**
     * Event handler that manages swim speed when wearing wellies
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Only check every 10 ticks to reduce performance impact
        if (player.level().getGameTime() % 10 != 0) {
            return;
        }

        // Check if player is wearing our wellies
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        boolean wearingWellies = !boots.isEmpty() && boots.getItem() instanceof WelliesItem;

        // Check if player is in water
        boolean inWater = player.isInWater();

        // Check if we currently have the swim speed modifier
        boolean hasModifier = player.getAttribute(ForgeMod.SWIM_SPEED.get())
                .getModifier(WELLIES_SWIM_SPEED_UUID) != null;

        // Apply or remove modifier based on conditions
        if (wearingWellies && inWater && !hasModifier) {
            // Add swim speed boost - much more conservative
            AttributeModifier swimSpeedModifier = new AttributeModifier(
                    WELLIES_SWIM_SPEED_UUID,
                    WELLIES_SWIM_SPEED_NAME,
                    0.8, // 30% increase in swim speed (much more reasonable)
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).addPermanentModifier(swimSpeedModifier);
        } else if ((!wearingWellies || !inWater) && hasModifier) {
            // Remove swim speed boost
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).removeModifier(WELLIES_SWIM_SPEED_UUID);
        }
    }
}