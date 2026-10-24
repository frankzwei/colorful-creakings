package me.ultrusmods.colorfulcreakings.block;

import me.ultrusmods.colorfulcreakings.block.entity.ColoredCreakingHeartBlockEntity;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;

public class ColoredCreakingHeartBlock extends CreakingHeartBlock {

    private final CreakingColor color;

    protected ColoredCreakingHeartBlock(Properties properties, CreakingColor color) {
        super(properties);
        this.color = color;
    }

    public CreakingColor getColor() {
        return color;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }

        return blockState.getValue(STATE) != CreakingHeartState.UPROOTED
                ? createTickerHelper(type,
                        ColorfulCreakingsBlockEntities.COLORED_CREAKING_HEART_BLOCK_ENTITY,
                        CreakingHeartBlockEntity::serverTick)
                : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ColoredCreakingHeartBlockEntity(blockPos, blockState);
    }
}