package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;

/**
 * ME Controller BlockState
 *
 * Main hub for ME networks - expands channels from 8 to 32. Has active/inactive
 * states based on network power.
 */
public class MEControllerBlockState extends BlockState {

    public static final BuilderCodec<MEControllerBlockState> CODEC;

    static {
        CODEC = BuilderCodec.builder(
                MEControllerBlockState.class,
                MEControllerBlockState::new,
                BlockState.BASE_CODEC
        )
                .append(
                        new KeyedCodec<>("Active", Codec.BOOLEAN),
                        (state, active) -> state.active = active,
                        state -> state.active
                )
                .add()
                .build();
    }

    private boolean active;

    public MEControllerBlockState() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
