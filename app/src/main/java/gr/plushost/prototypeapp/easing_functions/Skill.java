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

package gr.plushost.prototypeapp.easing_functions;

import gr.plushost.prototypeapp.easing_functions.BaseEasingMethod;
import gr.plushost.prototypeapp.easing_functions.back.BackEaseIn;
import gr.plushost.prototypeapp.easing_functions.back.BackEaseInOut;
import gr.plushost.prototypeapp.easing_functions.back.BackEaseOut;
import gr.plushost.prototypeapp.easing_functions.bounce.BounceEaseIn;
import gr.plushost.prototypeapp.easing_functions.bounce.BounceEaseInOut;
import gr.plushost.prototypeapp.easing_functions.bounce.BounceEaseOut;
import gr.plushost.prototypeapp.easing_functions.circ.CircEaseIn;
import gr.plushost.prototypeapp.easing_functions.circ.CircEaseInOut;
import gr.plushost.prototypeapp.easing_functions.circ.CircEaseOut;
import gr.plushost.prototypeapp.easing_functions.cubic.CubicEaseIn;
import gr.plushost.prototypeapp.easing_functions.cubic.CubicEaseInOut;
import gr.plushost.prototypeapp.easing_functions.cubic.CubicEaseOut;
import gr.plushost.prototypeapp.easing_functions.elastic.ElasticEaseIn;
import gr.plushost.prototypeapp.easing_functions.elastic.ElasticEaseOut;
import gr.plushost.prototypeapp.easing_functions.expo.ExpoEaseIn;
import gr.plushost.prototypeapp.easing_functions.expo.ExpoEaseInOut;
import gr.plushost.prototypeapp.easing_functions.expo.ExpoEaseOut;
import gr.plushost.prototypeapp.easing_functions.quad.QuadEaseIn;
import gr.plushost.prototypeapp.easing_functions.quad.QuadEaseInOut;
import gr.plushost.prototypeapp.easing_functions.quad.QuadEaseOut;
import gr.plushost.prototypeapp.easing_functions.quint.QuintEaseIn;
import gr.plushost.prototypeapp.easing_functions.quint.QuintEaseInOut;
import gr.plushost.prototypeapp.easing_functions.quint.QuintEaseOut;
import gr.plushost.prototypeapp.easing_functions.sine.SineEaseIn;
import gr.plushost.prototypeapp.easing_functions.sine.SineEaseInOut;
import gr.plushost.prototypeapp.easing_functions.sine.SineEaseOut;
import gr.plushost.prototypeapp.easing_functions.linear.Linear;


public enum  Skill {

    BackEaseIn(BackEaseIn.class),
    BackEaseOut(BackEaseOut.class),
    BackEaseInOut(BackEaseInOut.class),

    BounceEaseIn(BounceEaseIn.class),
    BounceEaseOut(BounceEaseOut.class),
    BounceEaseInOut(BounceEaseInOut.class),

    CircEaseIn(CircEaseIn.class),
    CircEaseOut(CircEaseOut.class),
    CircEaseInOut(CircEaseInOut.class),

    CubicEaseIn(CubicEaseIn.class),
    CubicEaseOut(CubicEaseOut.class),
    CubicEaseInOut(CubicEaseInOut.class),

    ElasticEaseIn(ElasticEaseIn.class),
    ElasticEaseOut(ElasticEaseOut.class),

    ExpoEaseIn(ExpoEaseIn.class),
    ExpoEaseOut(ExpoEaseOut.class),
    ExpoEaseInOut(ExpoEaseInOut.class),

    QuadEaseIn(QuadEaseIn.class),
    QuadEaseOut(QuadEaseOut.class),
    QuadEaseInOut(QuadEaseInOut.class),

    QuintEaseIn(QuintEaseIn.class),
    QuintEaseOut(QuintEaseOut.class),
    QuintEaseInOut(QuintEaseInOut.class),

    SineEaseIn(SineEaseIn.class),
    SineEaseOut(SineEaseOut.class),
    SineEaseInOut(SineEaseInOut.class),

    Linear(Linear.class);


    private Class easingMethod;

    private Skill(Class clazz) {
        easingMethod = clazz;
    }

    public BaseEasingMethod getMethod(float duration) {
        try {
            return (BaseEasingMethod)easingMethod.getConstructor(float.class).newInstance(duration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Can not init easingMethod instance");
        }
    }
}
