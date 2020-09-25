package com.yuema.designview.view

/**
 * @author liyeyu
 * @date 2020/9/17
 * description
 */
class Particle(
    var x: Float,//X坐标
    var y: Float,//Y坐标
    var radius: Float,//半径
    var speed: Float,//速度
    var alpha: Int,//透明度
    var maxOffset: Float,//最大移动距离
    var angle: Double,//每个粒子的弧度
    var offset: Float//粒子散发的位移
)