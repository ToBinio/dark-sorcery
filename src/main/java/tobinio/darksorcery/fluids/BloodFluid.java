package tobinio.darksorcery.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.items.ModItems;

import java.util.Optional;

/**
 * Created: 18.05.24
 *
 * @author Tobias Frischmann
 */
public abstract class BloodFluid extends BaseFluid {
    @Override
    public Fluid getStill() {
        return ModFluids.BLOOD;
    }


    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_BLOOD;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.BLOOD_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.BLOOD.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        //todo - custom sound?
        return Fluids.WATER.getBucketFillSound();
    }

    public static class Flowing extends BloodFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends BloodFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
