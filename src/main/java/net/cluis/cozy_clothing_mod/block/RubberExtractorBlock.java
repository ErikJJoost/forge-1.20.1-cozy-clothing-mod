package net.cluis.cozy_clothing_mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RubberExtractorBlock extends Block {

    public RubberExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            // For now, just print a message when clicked
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Rubber Extractor clicked!"));
        }
        return InteractionResult.SUCCESS;
    }
}