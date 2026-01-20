package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;

/**
 * ME Terminal BlockState
 *
 * Access point for viewing and managing network storage. Extends
 * ItemContainerState for inventory support.
 */
public class METerminalBlockState extends ItemContainerState {

    public static final BuilderCodec<METerminalBlockState> CODEC;

    static {
        CODEC = BuilderCodec.builder(
                METerminalBlockState.class,
                METerminalBlockState::new,
                ItemContainerState.BASE_CODEC
        )
                .build();
    }

    public METerminalBlockState() {
        super();
    }
}
