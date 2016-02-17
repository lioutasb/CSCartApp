package gr.plushost.prototypeapp.viewanimations.specials.in;

import android.view.View;

import gr.plushost.prototypeapp.easing_functions.Glider;
import gr.plushost.prototypeapp.easing_functions.Skill;
import android.animation.ObjectAnimator;
import gr.plushost.prototypeapp.viewanimations.BaseViewAnimator;

public class LandingAnimator extends BaseViewAnimator{
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "scaleX", 1.5f, 1f)),
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "scaleY", 1.5f, 1f)),
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "alpha", 0, 1f))
        );
    }
}
