package anton.peer_id.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import anton.peer_id.audd.Application;
import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;

public abstract class BottomFragment extends Fragment {

    private View content;
    private CloseBottomView container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup _container, Bundle savedInstanceState) {
        super.onCreateView(inflater, _container, savedInstanceState);
        container = new CloseBottomView(context());
        content = onViewPage();
        ViewGroup parent = (ViewGroup) content.getParent();
        if (parent != null) {
            parent.removeView(content);
        }
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        content.setLayoutParams(layoutParams);
        if (content.getBackground() == null) {
            content.setBackgroundResource(R.drawable.fragment_background);
        }
        content.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        content.setTranslationY(Screen.getHeight() + Screen.statusBarHeight);
        content.post(new Runnable() {
            @Override
            public void run() {
                onConnectApp();
            }
        });
        container.addView(content, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        return container;
    }

    public void setPie(boolean pie) {
        container.setPie(pie);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setRetainInstance(true);
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(content, "translationY", 0));
                final AnimatorSet currentAnimation = new AnimatorSet();
                currentAnimation.playTogether(animators);
                currentAnimation.setDuration(220);
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Application.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                onAnimationFinish();
                            }
                        });
                    }
                });
                currentAnimation.start();
            }
        }, 100);
    }

    @Override
    public void finish() {
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(content, "translationY", Screen.getHeight() + Screen.statusBarHeight));
                final AnimatorSet currentAnimation = new AnimatorSet();
                currentAnimation.playTogether(animators);
                currentAnimation.setDuration(220);
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Application.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                BottomFragment.super.finishInternal();
                            }
                        });
                    }
                });
                currentAnimation.start();
            }
        }, 100);
    }

    @Override
    public boolean isBackSwipe() {
        return false;
    }
}
