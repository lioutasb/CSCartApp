package gr.plushost.prototypeapp.viewanimations.specials.in;

import android.view.View;


import android.animation.ObjectAnimator;

import gr.plushost.prototypeapp.easing_functions.Glider;
import gr.plushost.prototypeapp.easing_functions.Skill;
import gr.plushost.prototypeapp.viewanimations.BaseViewAnimator;

public class DropOutAnimator extends BaseViewAnimator{
    @Override
    protected void prepare(View target) {
        int distance = target.getTop() + target.getHeight();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                Glider.glide(Skill.BounceEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "translationY", -distance, 0))
        );
    }
}
