package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType

@FXMarker
abstract class ShapeBuilder<T : Shape> : NodeBuilder<T>() {

    fun fill(paint: Paint) = settings { fill = paint }

    fun stroke(paint: Paint) = settings { stroke = paint }

    fun strokeWidth(width: Double) = settings { strokeWidth = width }

    fun strokeType(type: StrokeType) = settings { strokeType = type }

    fun strokeLineJoin(join: StrokeLineJoin) = settings { strokeLineJoin = join }

    fun strokeLineCap(cap: StrokeLineCap) = settings { strokeLineCap = cap }

    fun strokeDashArray(vararg dashes: Double) {
        settings { strokeDashArray.setAll(dashes.toList()) }
    }

    fun strokeDashOffset(offset: Double) = settings { strokeDashOffset = offset }

    fun strokeMiterLimit(limit: Double) = settings { strokeMiterLimit = limit }

    fun smooth(smooth: Boolean = true) = settings { isSmooth = smooth }
}
