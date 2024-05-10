package tobinio.darksorcery.blocks.altar;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.mixin.AbstractCandleBlockInvoker;

import java.util.List;
import java.util.Objects;

/**
 * Created: 30.04.24
 *
 * @author Tobias Frischmann
 */
public class AltarBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final MapCodec<AltarBlock> CODEC = AltarBlock.createCodec(AltarBlock::new);

    public static final VoxelShape TOP = VoxelShapes.cuboid(1 / 16f, 7 / 16f, 0f, 15 / 16f, 9 / 16f, 1f);
    public static final VoxelShape BOTTOM = VoxelShapes.cuboid(2 / 16f, 0 / 16f, 1 / 16f, 14 / 16f, 6 / 16f, 15 / 16f);
    public static final VoxelShape ALL = VoxelShapes.union(TOP, BOTTOM);

    public static final IntProperty LIT_CANDLES = IntProperty.of("lit_candles", 0, 5);
    public static final List<Vec3d> CANDLE_LOCATIONS = List.of(new Vec3d(13.5 / 16., 15 / 16., 11 / 16.), new Vec3d(1 / 16., 14 / 16., 10.5 / 16.), new Vec3d(11 / 16., 16 / 16., 11.5 / 16.), new Vec3d(3.5 / 16., 15 / 16., 13 / 16.), new Vec3d(7.5 / 16., 17 / 16., 14 / 16.));


    public AltarBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LIT_CANDLES, 0).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ALL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT_CANDLES);
        builder.add(Properties.HORIZONTAL_FACING);
        super.appendProperties(builder);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

        Integer lit_candles = state.get(LIT_CANDLES);
        Direction rotation = state.get(Properties.HORIZONTAL_FACING);

        for (int i = 0; i < lit_candles; i++) {
            AbstractCandleBlockInvoker.invokeSpawnCandleParticles(world, CANDLE_LOCATIONS.get(i)
                    .subtract(0.5, 0., 0.5)
                    .rotateY((float) ((-rotation.asRotation() * (Math.PI) / 180) + Math.PI))
                    .add(0.5, 0., 0.5)
                    .add(pos.getX(), pos.getY(), pos.getZ()), random);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        AltarEntity altarEntity = (AltarEntity) Objects.requireNonNull(world.getBlockEntity(pos));

        if (!world.isClient()) {
            player.sendMessage(Text.of("Level: %d capcity: %d/%d".formatted(altarEntity.getAltarLevel(), altarEntity.fluidStorage.getAmount(), altarEntity.fluidStorage.getCapacity())), true);
        }


        ItemVariant storageItem = altarEntity.itemStorage.getResource();
        ItemStack playerItem = player.getStackInHand(Hand.MAIN_HAND);

        if (!storageItem.isBlank()) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extract = altarEntity.itemStorage.extract(storageItem, 1, transaction);

                if (extract == 1) {

                    if (!world.isClient) {
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, storageItem.toStack());
                        itemEntity.setToDefaultPickupDelay();
                        world.spawnEntity(itemEntity);
                    }

                    transaction.commit();
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (!playerItem.isEmpty() && storageItem.isBlank()) {
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = altarEntity.itemStorage.insert(ItemVariant.of(playerItem), 1, transaction);

                if (!player.getAbilities().creativeMode) {
                    playerItem.decrement(1);
                }

                if (inserted == 1) {
                    transaction.commit();
                    return ActionResult.SUCCESS;
                } else {
                    if (!player.getAbilities().creativeMode) {
                        playerItem.increment(1);
                    }
                }
            }

        }

        return ActionResult.PASS;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
                .with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AltarEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {

        if (world.isClient || type != ModBlocks.ALTAR_ENTITY_TYPE) return null;

        return (world1, pos, state1, entity) -> AltarEntity.tick(world1, pos, state1, (AltarEntity) entity);
    }
}
