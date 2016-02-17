package gr.plushost.prototypeapp.viewanimations.specials.out;

import android.view.View;
import android.animation.ObjectAnimator;

import gr.plushost.prototypeapp.easing_functions.Glider;
import gr.plushost.prototypeapp.easing_functions.Skill;
import gr.plushost.prototypeapp.viewanimations.BaseViewAnimator;

public class TakingOffAnimator extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "scaleX", 1f, 1.5f)),
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "scaleY", 1f, 1.5f)),
                Glider.glide(Skill.QuintEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "alpha", 1, 0))
        );
    }
}
