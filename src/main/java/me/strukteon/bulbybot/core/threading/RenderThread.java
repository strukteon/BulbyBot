package me.strukteon.bulbybot.core.threading;
/*
    Created by nils on 23.08.2018 at 17:48.
    
    (c) nils 2018
*/

import me.strukteon.bulbybot.utils.Static;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RenderThread extends Thread {

    BlockingQueue<RenderItem> renderQueue = new ArrayBlockingQueue<RenderItem>(Static.RENDER_QUEUE_CAPACITY);

    public boolean offerRenderItem(RenderItem renderItem){
        boolean isAdded = renderQueue.offer(renderItem);
        if (isAdded)
            renderItem.setQueueStart(System.currentTimeMillis());
        return isAdded;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                RenderItem item = renderQueue.take();
                item.getOnStartInterface().started();
                long renderStart = System.currentTimeMillis();
                item.getRenderable().render();
                File result = item.getRenderable().getFile();
                long curTime = System.currentTimeMillis();
                item.getOnFinishInterface().finished(result, curTime - renderStart, curTime - item.getQueueStart());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
