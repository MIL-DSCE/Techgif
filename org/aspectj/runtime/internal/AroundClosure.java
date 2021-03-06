package org.aspectj.runtime.internal;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AroundClosure {
    protected int bitflags;
    protected Object[] preInitializationState;
    protected Object[] state;

    public abstract Object run(Object[] objArr) throws Throwable;

    public AroundClosure() {
        this.bitflags = AccessibilityNodeInfoCompat.ACTION_DISMISS;
    }

    public AroundClosure(Object[] state) {
        this.bitflags = AccessibilityNodeInfoCompat.ACTION_DISMISS;
        this.state = state;
    }

    public int getFlags() {
        return this.bitflags;
    }

    public Object[] getState() {
        return this.state;
    }

    public Object[] getPreInitializationState() {
        return this.preInitializationState;
    }

    public ProceedingJoinPoint linkClosureAndJoinPoint() {
        ProceedingJoinPoint jp = this.state[this.state.length - 1];
        jp.set$AroundClosure(this);
        return jp;
    }

    public ProceedingJoinPoint linkClosureAndJoinPoint(int flags) {
        ProceedingJoinPoint jp = this.state[this.state.length - 1];
        jp.set$AroundClosure(this);
        this.bitflags = flags;
        return jp;
    }
}
