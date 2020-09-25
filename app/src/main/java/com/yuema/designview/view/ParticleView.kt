package com.yuema.designview.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.BitmapFactory.decodeResource
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.core.graphics.rotationMatrix
import com.yuema.designview.R
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


/**
 * @author liyeyu
 * @date 2020/9/17
 * description
 */
@SuppressLint("CustomViewStyleable")
class ParticleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    //粒子集合
    private var particleList = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var centerX = 0f
    private var centerY = 0f
    private var cSpace = 0f //内圆间距
    private var cRadius = 0f //粒子半径
    private var radius = 0f // 内圆半径
    private var outRadius = 0f // 内圆半径
    private var cMaxOffset = 0f
    private var cSpeed = 0
    private var cNum = 0
    private var cColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var animator = ValueAnimator.ofFloat(0f, 1f)


    private val pathMeasure = PathMeasure()//路径，用于测量扩散圆某一处的X,Y值
    private var path: Path = Path()
    private var pos = FloatArray(2) //扩散圆上某一点的x,y
    private val tan = FloatArray(2)//扩散圆上某一点切线
    private var colorArray:Array<String>? = null
    private var colorRandom:Boolean = true

    companion object {
        const val DEFAULT_RADIUS = 3f
        const val DEFAULT_SPACE = 20f
        const val DEFAULT_RADIUS_INNER = 200f
        const val DEFAULT_NUM = 2000
        const val DEFAULT_SPEED = 1
        const val DEFAULT_MAX_OFFSET = 300f
    }

    init {
        val t: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.dimple)
        cColor = t.getColor(R.styleable.dimple_particle_color, cColor)
        cNum = t.getInt(R.styleable.dimple_particle_num, DEFAULT_NUM)
        cRadius = t.getFloat(R.styleable.dimple_particle_radius, DEFAULT_RADIUS)
        cSpace = t.getFloat(R.styleable.dimple_particle_space, DEFAULT_SPACE)
        radius = t.getDimension(R.styleable.dimple_dimple_radius_inner, DEFAULT_RADIUS_INNER)
        cMaxOffset = t.getFloat(R.styleable.dimple_particle_max_offset, DEFAULT_MAX_OFFSET)
        cSpeed = t.getInt(R.styleable.dimple_particle_speed, DEFAULT_SPEED)
        colorRandom = t.getBoolean(R.styleable.dimple_particle_color_random, colorRandom)

        outRadius = radius + cSpace + 3f
        t.recycle()

        animator.duration = 5000
        animator.repeatCount = -1
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            updateParticle(it.animatedValue as Float)
            invalidate()
        }
        colorArray = resources.getStringArray(R.array.color_choices)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL

        particleList.forEach {
            paint.color = if(colorRandom) {Color.parseColor(colorArray!!.random())} else cColor
            paint.alpha = it.alpha
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()
        path.reset()
        path.addCircle(centerX, centerY, outRadius, Path.Direction.CCW)
        pathMeasure.setPath(path, false) //添加path

        var nextX: Float
        var nextY: Float
        particleList.clear()
        for (i in 0..cNum) {

            pathMeasure.getPosTan(i * 1f / cNum * pathMeasure.length, pos, tan)

            cSpeed = Random.nextInt(cSpeed) + 2
            nextX = pos[0] + Random.nextInt(12) - 6f
            nextY = pos[1] + Random.nextInt(12) - 6f

            val angle = acos((pos[0] - centerX) / outRadius)
            val offset = Random.nextInt((DEFAULT_MAX_OFFSET * 2 / 3).toInt()).toFloat()
            val max = Random.nextInt(cMaxOffset.toInt()) + 20f

            val particle =
                Particle(
                    nextX,
                    nextY,
                    cRadius,
                    cSpeed.toFloat(),
                    100,
                    max,
                    angle.toDouble(),
                    offset
                )
            particleList.add(particle)
        }
        animator.start()
    }

    private fun updateParticle(value: Float) {
        particleList.forEach {

            if (it.offset > it.maxOffset) {
                it.offset = 0f
                it.speed = Random.nextInt(cSpeed) + 2f

            }
            it.alpha = ((1f - it.offset / it.maxOffset) * 255f).toInt()
            it.x = (centerX + (outRadius + it.offset) * cos(it.angle)).toFloat()

            if (it.y > centerY) {
                it.y = (centerY + (outRadius + it.offset) * sin(it.angle)).toFloat()
            } else {
                it.y = (centerY - (outRadius + it.offset) * sin(it.angle)).toFloat()
            }

            it.offset += it.speed
        }
    }
}