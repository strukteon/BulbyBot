package me.strukteon.bulbybot.core.threading;
/*
    Created by nils on 23.08.2018 at 17:53.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.utils.image.Renderable;

import java.io.File;

public class RenderItem {
    private Renderable renderable;
    private OnFinishInterface onFinishInterface;
    private OnStartInterface onStartInterface;
    private long queueStart = 0;

    public RenderItem(Renderable renderable){
        this(renderable, (f, l1, l2) -> {});
    }

    public RenderItem(Renderable renderable, OnFinishInterface onFinishInterface){
        this(renderable, onFinishInterface, () -> {});
    }

    public RenderItem(Renderable renderable, OnFinishInterface onFinishInterface, OnStartInterface onStartInterface){
        this.renderable = renderable;
        this.onFinishInterface = onFinishInterface;
        this.onStartInterface = onStartInterface;
    }

    public void setOnFinished(OnFinishInterface onFinishInterface) {
        this.onFinishInterface = onFinishInterface;
    }

    public void setOnStart(OnStartInterface onStartInterface) {
        this.onStartInterface = onStartInterface;
    }

    protected void setQueueStart(long queueStart) {
        this.queueStart = queueStart;
    }

    public Renderable getRenderable() {
        return renderable;
    }

    protected OnFinishInterface getOnFinishInterface() {
        return onFinishInterface;
    }

    protected OnStartInterface getOnStartInterface() {
        return onStartInterface;
    }

    protected long getQueueStart() {
        return queueStart;
    }

    public interface OnFinishInterface {
        void finished(File result, long renderDur, long queueDur);
    }

    public interface OnStartInterface {
        void started();
    }
}
