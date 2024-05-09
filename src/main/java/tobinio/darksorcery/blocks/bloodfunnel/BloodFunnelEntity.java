package tobinio.darksorcery.blocks.bloodfunnel;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import tobinio.darksorcery.blocks.ModBlocks;

/**
 * Created: 19.04.24
 *
 * @author Tobias Frischmann
 */
public class BloodFunnelEntity extends BlockEntity {

    public final SingleVariantStorage<FluidVariant> storage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BOTTLE;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    public BloodFunnelEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BLOOD_FUNNEL_ENTITY_TYPE, pos, state);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (this.world != null) {
            if (storage.getAmount() == FluidConstants.BOTTLE) {
                this.world.setBlockState(this.pos, this.getCachedState().with(BloodFunnelBlock.FILLED, true));
            } else {
                this.world.setBlockState(this.pos, this.getCachedState().with(BloodFunnelBlock.FILLED, false));
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.put("fluidVariant", storage.variant.toNbt());
        tag.putLong("fluidAmount", storage.amount);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        storage.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        storage.amount = tag.getLong("fluidAmount");
    }
}
