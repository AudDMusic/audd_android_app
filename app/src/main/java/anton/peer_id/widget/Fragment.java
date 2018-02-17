package anton.peer_id.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import anton.peer_id.audd.Application;
import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;


public abstract class Fragment extends android.app.Fragment {

    public interface FragmentListener {
        void onAddFragment(Fragment fragment);
        void onRemoveFragment(Fragment fragment);
        void preparationFragment(Fragment fragment, boolean isFinish);
        void init();
    }

    private Context context;
    private FragmentListener listener = null;
    private boolean isFinish = false;

    public void addFragment(Fragment fragment) {
        listener.onAddFragment(fragment);
    }

    public Context context() {
        if (context == null) {
            return getActivity();
        } else {
            return context;
        }
    }

    public void setContext(Context context) {
        if (listener == null && context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        }
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentListener) context;
    }

    public void init() {
        listener.init();
    }

    public void finish() {
        if (isFinish) {
            finishInternal();
            return;
        }
        isFinish = true;
        listener.preparationFragment(this, true);
        if (getView() == null || !isBackSwipe()) {
            finishInternal();
            return;
        }
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofFloat(getView(), "alpha", 1.0f, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(getView(), "translationY", 0, Screen.getHeight() + Screen.statusBarHeight));
                    final AnimatorSet currentAnimation = new AnimatorSet();
                    currentAnimation.playTogether(animators);
                    currentAnimation.setDuration(320);
                    currentAnimation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (getView() != null) {
                                getView().setLayerType(View.LAYER_TYPE_NONE, null);
                            }
                            finishInternal();
                        }
                    });
                    currentAnimation.start();
                } catch (Throwable ignored) { }
            }
        });
    }

    public void finishInternal() {
        listener.onRemoveFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listener.preparationFragment(this, false);
        View view = onViewPage();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        view.setLayerType(View.LAYER_TYPE_NONE, null);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        view.setLayoutParams(layoutParams);
        if (view.getBackground() == null) {
            view.setBackgroundResource(R.drawable.fragment_background);
        }
        if (!isBackSwipe()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            view.post(new Runnable() {
                @Override
                public void run() {
                    onConnectApp();
                }
            });
            return view;
        }
        SwipeBackView swipeBackView = new SwipeBackView(context());
        swipeBackView.setContent(view, new SwipeBackView.OnSwipeListener() {
            @Override
            public void onCloseScreen() {
                finishInternal();
            }

            @Override
            public void onDragging() {
                onSwipeDragging();
            }
        });
        view = swipeBackView;
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.setAlpha(0.0f);
        view.setTranslationY(Screen.getHeight() + Screen.statusBarHeight);
        view.post(new Runnable() {
            @Override
            public void run() {
                onConnectApp();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setRetainInstance(true);
        if (!isBackSwipe()) {
            return;
        }
        if (getView() == null) {
            finishInternal();
            return;
        }
        getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofFloat(getView(), "alpha", 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(getView(), "translationY", Screen.getHeight() + Screen.statusBarHeight, 0));
                    final AnimatorSet currentAnimation = new AnimatorSet();
                    currentAnimation.playTogether(animators);
                    currentAnimation.setDuration(320);
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
                } catch (Throwable e) {
                    if (getView() != null) {
                        getView().setAlpha(1.0f);
                        getView().setScaleX(1.0f);
                        getView().setScaleY(1.0f);
                        getView().setTranslationX(0);
                    }
                }
            }
        }, 40);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDisconnectApp();
        finish();
    }

    public abstract View onViewPage();
    public abstract void onConnectApp();
    public abstract void onDisconnectApp();
    public abstract boolean onBackPressed();

    public boolean isBackSwipe() {
        return true;
    }

    public void onSwipeDragging() {

    }

    public void onAnimationFinish() {

    }

}