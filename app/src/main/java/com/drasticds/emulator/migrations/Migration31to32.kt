package com.drasticds.emulator.migrations

import android.content.Context
import com.drasticds.emulator.domain.model.Point
import com.drasticds.emulator.domain.model.ui.Orientation
import com.drasticds.emulator.impl.dtos.layout.PointDto
import com.drasticds.emulator.migrations.helper.GenericJsonArrayMigrationHelper
import com.drasticds.emulator.migrations.legacy.layout.LayoutConfigurationDto31
import com.drasticds.emulator.migrations.legacy.layout.LayoutConfigurationDto35
import com.drasticds.emulator.migrations.legacy.layout.UILayoutVariantDto35
import com.drasticds.emulator.utils.WindowManagerCompat

class Migration31to32(
    private val context: Context,
    private val layoutMigrationHelper: GenericJsonArrayMigrationHelper,
) : Migration {

    companion object {
        private const val LAYOUTS_DATA_FILE = "layouts.json"
    }

    override val from = 31
    override val to = 32

    override fun migrate() {
        val windowSize = WindowManagerCompat.getWindowSize(context)
        val (portraitSize, landscapeSize) = if (windowSize.x > windowSize.y) {
            Point(windowSize.y, windowSize.x) to Point(windowSize.x, windowSize.y)
        } else {
            Point(windowSize.x, windowSize.y) to Point(windowSize.y, windowSize.x)
        }

        layoutMigrationHelper.migrateJsonArrayData<LayoutConfigurationDto31, LayoutConfigurationDto35>(LAYOUTS_DATA_FILE) {
            try {
                LayoutConfigurationDto35(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    orientation = it.orientation,
                    useCustomOpacity = it.useCustomOpacity,
                    opacity = it.opacity,
                    layoutVariants = listOf(
                        LayoutConfigurationDto35.LayoutEntryDto35(
                            variant = UILayoutVariantDto35(
                                uiSize = PointDto.fromModel(portraitSize),
                                orientation = Orientation.PORTRAIT.name,
                                folds = emptyList(),
                            ),
                            layout = it.portraitLayout,
                        ),
                        LayoutConfigurationDto35.LayoutEntryDto35(
                            variant = UILayoutVariantDto35(
                                uiSize = PointDto.fromModel(landscapeSize),
                                orientation = Orientation.LANDSCAPE.name,
                                folds = emptyList(),
                            ),
                            layout = it.landscapeLayout,
                        ),
                    )
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}