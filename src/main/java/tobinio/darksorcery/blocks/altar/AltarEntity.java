package tobinio.darksorcery.blocks.altar;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import tobinio.darksorcery.DarkSorcery;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.tags.ModTags;

import java.util.List;

import static tobinio.darksorcery.blocks.altar.AltarBlock.LIT_CANDLES;

/**
 * Created: 09.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarEntity extends BlockEntity {

    public static final List<Vec3i> TOWER_LOCATIONS = List.of(new Vec3i(5, -1, -1), new Vec3i(4, -1, 1), new Vec3i(2, -1, 2), new Vec3i(0, -1, 3), new Vec3i(-2, -1, 2), new Vec3i(-4, -1, 1), new Vec3i(-5, -1, -1));

    public final SingleVariantStorage<FluidVariant> storage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return Math.max(getAltarLevel() * FluidConstants.BUCKET, FluidConstants.BOTTLE);
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    int altarLevel = 0;

    public AltarEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ALTAR_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AltarEntity entity) {
        entity.updateAltar();

        int level = entity.getAltarLevel();
        Integer lit_candles = state.get(LIT_CANDLES);

        if (lit_candles != level) {
            world.setBlockState(pos, state.with(LIT_CANDLES, level));
        }
    }

    private void updateAltar() {
        altarLevel = calculateAltarLevel();
    }

    private int calculateAltarLevel() {
        if (world == null) {
            DarkSorcery.LOGGER.error("trying to get AltarLevel but world is null");
            return 0;
        }

        Direction rotation = this.getCachedState().get(Properties.HORIZONTAL_FACING);

        //get ground
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!this.world.getBlockState(pos.add(x, -1, z)).isIn(ModTags.ALTAR_GROUND)) {
                    return 0;
                }
            }
        }

        //check towers
        int tier1 = 0;
        int tier2 = 0;
        int tier3 = 0;
        int tier4 = 0;
        int tier5 = 0;

        for (Vec3i towerLocation : TOWER_LOCATIONS) {

            Vec3d vec3d = new Vec3d(towerLocation.getX(), towerLocation.getY(), towerLocation.getZ()).rotateY((float) (-rotation.asRotation() * (Math.PI) / 180 + Math.PI));
            var towerPos = pos.add(new Vec3i((int) Math.round(vec3d.x), (int) Math.round(vec3d.y), (int) Math.round(vec3d.z)));

            if (!world.getBlockState(towerPos).isIn(ModTags.ALTAR_TOWER_BLOCKS)) {
                return 0;
            }

            while (true) {
                BlockState block = world.getBlockState(towerPos);

                if (!block.isIn(ModTags.ALTAR_TOWER_BLOCKS)) {
                    break;
                }

                //todo also count higher level as lower level
                if (block.isIn(ModTags.ALTAR_TIER1_BLOCKS)) tier1++;
                if (block.isIn(ModTags.ALTAR_TIER2_BLOCKS)) tier2++;
                if (block.isIn(ModTags.ALTAR_TIER3_BLOCKS)) tier3++;
                if (block.isIn(ModTags.ALTAR_TIER4_BLOCKS)) tier4++;
                if (block.isIn(ModTags.ALTAR_TIER5_BLOCKS)) tier5++;

                towerPos = towerPos.up();
            }
        }

        if (tier5 > 10) return 5;
        if (tier4 > 10) return 4;
        if (tier3 > 10) return 3;
        if (tier2 > 10) return 2;
        if (tier1 > 10) return 1;

        return 0;
    }

    public int getAltarLevel() {
        return this.altarLevel;
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
