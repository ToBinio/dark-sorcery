package tobinio.darksorcery.blocks.bloodfunnel;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.items.ModItems;

/**
 * Created: 19.04.24
 *
 * @author Tobias Frischmann
 */
public class BloodFunnelBlock extends Block implements BlockEntityProvider {

    public static final VoxelShape TOP_CUT_OUT = VoxelShapes.cuboid(2 / 16f, 5 / 16f, 2 / 16f, 14 / 16f, 8 / 16f, 14 / 16f);
    public static final VoxelShape TOP = VoxelShapes.combine(VoxelShapes.cuboid(0, 5 / 16f, 0, 1, 8 / 16f, 1),
            TOP_CUT_OUT, BooleanBiFunction.ONLY_FIRST);

    public static final VoxelShape BOTTOM_CUT_OUT = VoxelShapes.union(VoxelShapes.cuboid(3 / 16f, 0, 0, 13 / 16f, 3 / 16f, 1), VoxelShapes.cuboid(0, 0, 3 / 16f, 1, 3 / 16f, 13 / 16f));
    public static final VoxelShape BOTTOM = VoxelShapes.combine(VoxelShapes.cuboid(1 / 16f, 0, 1 / 16f, 15 / 16f, 5 / 16f, 15 / 16f),
            BOTTOM_CUT_OUT, BooleanBiFunction.ONLY_FIRST);
    public static final VoxelShape VOXEL_SHAPE = VoxelShapes.union(BOTTOM, TOP);

    public static final BooleanProperty FILLED = BooleanProperty.of("filled");

    public BloodFunnelBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FILLED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        ItemStack item = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, hit.getSide());

        if (storage == null) {
            return super.onUse(state, world, pos, player, hand, hit);
        }

        if (item.isOf(ModItems.TINTED_GLASS_BOTTLE)) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extract = storage.extract(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BOTTLE, transaction);

                if (extract == FluidConstants.BOTTLE) {
                    transaction.commit();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(item, player, new ItemStack(ModItems.BLOODY_TINTED_GLASS_BOTTLE)));
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (item.isOf(ModItems.BLOODY_TINTED_GLASS_BOTTLE)) {
            try (Transaction transaction = Transaction.openOuter()) {
                long insert = storage.insert(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BOTTLE, transaction);

                if (insert == FluidConstants.BOTTLE) {
                    transaction.commit();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(item, player, new ItemStack(ModItems.TINTED_GLASS_BOTTLE)));
                    world.playSound(player, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (item.isOf(Items.BUCKET)) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extract = storage.extract(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BUCKET, transaction);

                if (extract == FluidConstants.BUCKET) {
                    transaction.commit();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(item, player, new ItemStack(ModFluids.BLOOD_BUCKET)));
                    player.playSound(ModFluids.BLOOD.getBucketFillSound().get(), 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (item.isOf(ModFluids.BLOOD_BUCKET)) {
            try (Transaction transaction = Transaction.openOuter()) {
                long insert = storage.insert(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BUCKET, transaction);

                if (insert == FluidConstants.BUCKET) {
                    transaction.commit();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(item, player, new ItemStack(Items.BUCKET)));
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
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
}
