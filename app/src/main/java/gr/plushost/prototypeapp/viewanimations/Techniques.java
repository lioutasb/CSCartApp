
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 daimajia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gr.plushost.prototypeapp.viewanimations;

import gr.plushost.prototypeapp.viewanimations.attention.BounceAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.FlashAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.PulseAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.RubberBandAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.ShakeAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.StandUpAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.SwingAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.TadaAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.WaveAnimator;
import gr.plushost.prototypeapp.viewanimations.attention.WobbleAnimator;
import gr.plushost.prototypeapp.viewanimations.bouncing_entrances.BounceInAnimator;
import gr.plushost.prototypeapp.viewanimations.bouncing_entrances.BounceInDownAnimator;
import gr.plushost.prototypeapp.viewanimations.bouncing_entrances.BounceInLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.bouncing_entrances.BounceInRightAnimator;
import gr.plushost.prototypeapp.viewanimations.bouncing_entrances.BounceInUpAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_entrances.FadeInAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_entrances.FadeInDownAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_entrances.FadeInLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_entrances.FadeInRightAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_entrances.FadeInUpAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_exits.FadeOutAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_exits.FadeOutDownAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_exits.FadeOutLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_exits.FadeOutRightAnimator;
import gr.plushost.prototypeapp.viewanimations.fading_exits.FadeOutUpAnimator;
import gr.plushost.prototypeapp.viewanimations.flippers.FlipInXAnimator;
import gr.plushost.prototypeapp.viewanimations.flippers.FlipOutXAnimator;
import gr.plushost.prototypeapp.viewanimations.flippers.FlipOutYAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_entrances.RotateInAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_entrances.RotateInDownLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_entrances.RotateInDownRightAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_entrances.RotateInUpLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_entrances.RotateInUpRightAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_exits.RotateOutAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_exits.RotateOutDownLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_exits.RotateOutDownRightAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_exits.RotateOutUpLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.rotating_exits.RotateOutUpRightAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideInDownAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideInLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideInRightAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideInUpAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideOutDownAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideOutLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideOutRightAnimator;
import gr.plushost.prototypeapp.viewanimations.sliders.SlideOutUpAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.HingeAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.RollInAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.RollOutAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.in.DropOutAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.in.LandingAnimator;
import gr.plushost.prototypeapp.viewanimations.specials.out.TakingOffAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_entrances.ZoomInAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_entrances.ZoomInDownAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_entrances.ZoomInLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_entrances.ZoomInRightAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_entrances.ZoomInUpAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_exits.ZoomOutAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_exits.ZoomOutDownAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_exits.ZoomOutLeftAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_exits.ZoomOutRightAnimator;
import gr.plushost.prototypeapp.viewanimations.zooming_exits.ZoomOutUpAnimator;

public enum Techniques {

    DropOut(DropOutAnimator.class),
    Landing(LandingAnimator.class),
    TakingOff(TakingOffAnimator.class),

    Flash(FlashAnimator.class),
    Pulse(PulseAnimator.class),
    RubberBand(RubberBandAnimator.class),
    Shake(ShakeAnimator.class),
    Swing(SwingAnimator.class),
    Wobble(WobbleAnimator.class),
    Bounce(BounceAnimator.class),
    Tada(TadaAnimator.class),
    StandUp(StandUpAnimator.class),
    Wave(WaveAnimator.class),

    Hinge(HingeAnimator.class),
    RollIn(RollInAnimator.class),
    RollOut(RollOutAnimator.class),

    BounceIn(BounceInAnimator.class),
    BounceInDown(BounceInDownAnimator.class),
    BounceInLeft(BounceInLeftAnimator.class),
    BounceInRight(BounceInRightAnimator.class),
    BounceInUp(BounceInUpAnimator.class),

    FadeIn(FadeInAnimator.class),
    FadeInUp(FadeInUpAnimator.class),
    FadeInDown(FadeInDownAnimator.class),
    FadeInLeft(FadeInLeftAnimator.class),
    FadeInRight(FadeInRightAnimator.class),

    FadeOut(FadeOutAnimator.class),
    FadeOutDown(FadeOutDownAnimator.class),
    FadeOutLeft(FadeOutLeftAnimator.class),
    FadeOutRight(FadeOutRightAnimator.class),
    FadeOutUp(FadeOutUpAnimator.class),

    FlipInX(FlipInXAnimator.class),
    FlipOutX(FlipOutXAnimator.class),

    FlipOutY(FlipOutYAnimator.class),
    RotateIn(RotateInAnimator.class),
    RotateInDownLeft(RotateInDownLeftAnimator.class),
    RotateInDownRight(RotateInDownRightAnimator.class),
    RotateInUpLeft(RotateInUpLeftAnimator.class),
    RotateInUpRight(RotateInUpRightAnimator.class),

    RotateOut(RotateOutAnimator.class),
    RotateOutDownLeft(RotateOutDownLeftAnimator.class),
    RotateOutDownRight(RotateOutDownRightAnimator.class),
    RotateOutUpLeft(RotateOutUpLeftAnimator.class),
    RotateOutUpRight(RotateOutUpRightAnimator.class),

    SlideInLeft(SlideInLeftAnimator.class),
    SlideInRight(SlideInRightAnimator.class),
    SlideInUp(SlideInUpAnimator.class),
    SlideInDown(SlideInDownAnimator.class),

    SlideOutLeft(SlideOutLeftAnimator.class),
    SlideOutRight(SlideOutRightAnimator.class),
    SlideOutUp(SlideOutUpAnimator.class),
    SlideOutDown(SlideOutDownAnimator.class),

    ZoomIn(ZoomInAnimator.class),
    ZoomInDown(ZoomInDownAnimator.class),
    ZoomInLeft(ZoomInLeftAnimator.class),
    ZoomInRight(ZoomInRightAnimator.class),
    ZoomInUp(ZoomInUpAnimator.class),

    ZoomOut(ZoomOutAnimator.class),
    ZoomOutDown(ZoomOutDownAnimator.class),
    ZoomOutLeft(ZoomOutLeftAnimator.class),
    ZoomOutRight(ZoomOutRightAnimator.class),
    ZoomOutUp(ZoomOutUpAnimator.class);



    private Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
