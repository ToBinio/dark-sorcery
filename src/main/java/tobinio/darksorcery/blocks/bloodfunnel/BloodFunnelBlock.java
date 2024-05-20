package tobinio.darksorcery.blocks.bloodfunnel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tobinio.darksorcery.blockHighlight.BlockHighlightManager;

/**
 * Created: 19.04.24
 *
 * @author Tobias Frischmann
 */
public class BloodFunnelBlock extends Block implements BlockEntityProvider {

    public static final VoxelShape TOP_CUT_OUT = VoxelShapes.cuboid(2 / 16f, 5 / 16f, 2 / 16f, 14 / 16f, 8 / 16f, 14 / 16f);
    public static final VoxelShape TOP = VoxelShapes.combine(VoxelShapes.cuboid(0, 5 / 16f, 0, 1, 8 / 16f, 1), TOP_CUT_OUT, BooleanBiFunction.ONLY_FIRST);

    public static final VoxelShape BOTTOM_CUT_OUT = VoxelShapes.union(VoxelShapes.cuboid(3 / 16f, 0, 0, 13 / 16f, 3 / 16f, 1), VoxelShapes.cuboid(0, 0, 3 / 16f, 1, 3 / 16f, 13 / 16f));
    public static final VoxelShape BOTTOM = VoxelShapes.combine(VoxelShapes.cuboid(1 / 16f, 0, 1 / 16f, 15 / 16f, 5 / 16f, 15 / 16f), BOTTOM_CUT_OUT, BooleanBiFunction.ONLY_FIRST);
    public static final VoxelShape VOXEL_SHAPE = VoxelShapes.union(BOTTOM, TOP);

    public static final BooleanProperty FILLED = BooleanProperty.of("filled");

    public BloodFunnelBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FILLED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
        super.appendProperties(builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VOXEL_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BloodFunnelEntity(pos, state);
    }

    @Environment (EnvType.CLIENT)
    public static void highlightBLock(BlockPos pos, int time) {
        BlockHighlightManager.INSTANCE.highlightBlock(pos, time);
    }

    public static void disableEffect(World world, BlockPos pos) {

        Random random = world.getRandom();

        for (int i = 10; i > 0; i--) {
            world.addParticle(ParticleTypes.DAMAGE_INDICATOR, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat() / 3 + 0.5, pos.getZ() + random.nextFloat(), 0.0, -0.3, 0.0);
        }

        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
    }
}
