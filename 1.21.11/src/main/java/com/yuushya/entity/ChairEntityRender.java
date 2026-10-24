package com.yuushya.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * 椅子实体本身不可见，仅用于承载乘客，因此不需要绘制内容。
 * 1.21.9+ 的实体渲染改为「渲染状态」模式：EntityRenderer&lt;T, S extends EntityRenderState&gt;。
 */
public class ChairEntityRender extends EntityRenderer<ChairEntity, EntityRenderState> {
    public ChairEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
