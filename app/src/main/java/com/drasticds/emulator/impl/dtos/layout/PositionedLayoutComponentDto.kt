package com.drasticds.emulator.impl.dtos.layout

import com.google.gson.annotations.SerializedName
import com.drasticds.emulator.domain.model.layout.PositionedLayoutComponent
import com.drasticds.emulator.utils.enumValueOfIgnoreCase

data class PositionedLayoutComponentDto(
    @SerializedName("rect")
    val rect: RectDto,
    @SerializedName("component")
    val component: String,
    @SerializedName("alpha")
    val alpha: Float? = null,
    @SerializedName("onTop")
    val onTop: Boolean? = null,
) {

    companion object {
        fun fromModel(positionedLayoutComponent: PositionedLayoutComponent): PositionedLayoutComponentDto {
            return PositionedLayoutComponentDto(
                RectDto.fromModel(positionedLayoutComponent.rect),
                positionedLayoutComponent.component.name,
                positionedLayoutComponent.alpha,
                positionedLayoutComponent.onTop,
            )
        }
    }

    fun toModel(): PositionedLayoutComponent {
        return PositionedLayoutComponent(
            rect.toModel(),
            enumValueOfIgnoreCase(component),
            alpha ?: 1f,
            onTop ?: false,
        )
    }
}