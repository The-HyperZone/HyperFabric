package net.hypercubemc.hyperfabric.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(FillCommand.class)
public class OPFillCommandSpawnProtectionMixin {
    private static ServerCommandSource source;
    @Inject(
            method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/command/arguments/BlockStateArgument;Lnet/minecraft/server/command/FillCommand$Mode;Ljava/util/function/Predicate;)I",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/server/command/FillCommand;execute(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/command/arguments/BlockStateArgument;Lnet/minecraft/server/command/FillCommand$Mode;Ljava/util/function/Predicate;)I"
            )
    )
    private static void execute(ServerCommandSource commandSource, BlockBox range, BlockStateArgument block, FillCommand.Mode mode, Predicate<CachedBlockPosition> filter, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        source = commandSource;
    }
    @ModifyArg(
            method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/command/arguments/BlockStateArgument;Lnet/minecraft/server/command/FillCommand$Mode;Ljava/util/function/Predicate;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            ),
            index = 0
    )
    private static BlockPos getBlockState(BlockPos blockPos) throws CommandSyntaxException {
        try {
            BlockPos finalBlockPos = null;

            boolean isPlayer;

            try {
                source.getPlayer();
                isPlayer = true;
            }
            catch (Exception error) {
                isPlayer = false;
            }

            if (isPlayer) {
                if (source.getWorld().getServer().isSpawnProtected(source.getWorld(), blockPos, source.getPlayer())) {
                    finalBlockPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
                }
            }

            if (finalBlockPos == null) {
                finalBlockPos = blockPos;
            }
            return finalBlockPos;
        } catch (Exception error) {
            error.printStackTrace();
            throw error;
        }
    }
    @ModifyArg(
            method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/command/arguments/BlockStateArgument;Lnet/minecraft/server/command/FillCommand$Mode;Ljava/util/function/Predicate;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/arguments/BlockStateArgument;setBlockState(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;I)Z"
            ),
            index = 1
    )
    private static BlockPos setBlockState(ServerWorld serverWorld, BlockPos blockPos, int i) throws CommandSyntaxException {
        try {
            BlockPos finalBlockPos = null;

            boolean isPlayer;

            try {
                source.getPlayer();
                isPlayer = true;
            }
            catch (Exception error) {
                isPlayer = false;
            }

            if (isPlayer) {
                if (source.getWorld().getServer().isSpawnProtected(source.getWorld(), blockPos, source.getPlayer())) {
                    finalBlockPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
                }
            }

            if (finalBlockPos == null) {
                finalBlockPos = blockPos;
            }
            return finalBlockPos;
        } catch (Exception error) {
            error.printStackTrace();
            throw error;
        }
    }
}
