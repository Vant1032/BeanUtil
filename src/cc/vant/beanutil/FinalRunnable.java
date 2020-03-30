package cc.vant.beanutil;

import java.util.List;

public class FinalRunnable implements Runnable {

    private List<Runnable> toAdd;

    public FinalRunnable(List<Runnable> toAdd) {
        this.toAdd = toAdd;
    }

    @Override
    public void run() {
        for (Runnable runnable : toAdd) {
            runnable.run();
        }
    }
}
