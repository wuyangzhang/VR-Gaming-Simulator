package Simulator;

/**
 * Created by wuyang on 3/22/17.
 */
@FunctionalInterface
public interface Action {

    public void execute(Object... args);
}
