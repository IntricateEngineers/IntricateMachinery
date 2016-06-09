package intricateengineers.intricatemachinery.api.util;

import java.util.function.Consumer;

/**
 * This is a magic class.
 * It will be a collection of magic that i enjoy because im crazy.
 * Most of it will probably include lambdas.
 * @author topisani
 */
public class Topitils {

    /**
     * This is a magic method
     * It tries to cast in to T, and if successfull calls Consumer then
     * If not, Runnable or will be called
     * @param in object to try cast on
     * @param then will be called if in was successfully cast to T
     * @param or will be called if then wasnt.
     * @return true if successfully cast.
     */
    public static <T> boolean tryCast(Object in, Consumer<T> then, Runnable or) {
        try {
            then.accept((T) in);
            return true;
        } catch(ClassCastException e) {
            or.run();
            return false;
        }
    }

    /**
     * This is a magic method
     * It tries to cast in to T, and if successfull calls Consumer then
     * @param in object to try cast on
     * @param then will be called if in was successfully cast to T
     * @return true if successfully cast.
     */
    public static <T> boolean tryCast(Object in, Consumer<T> then) {
        try {
            then.accept((T) in);
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

    /**
     * This is a magic method.
     * It will try to run the passed Runnable, catching any exceptions.
     * @param runnable the runnable to run
     * @return true if no exception was thrown
     */
    public static boolean catchIgnore(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch(Throwable e) {
            return false;
        }
    }

    /**
     * This is a magic method.
     * It will try to run the passed Runnable, catching any exceptions,
     * and logging their stacktrace using the mods own Logger.
     * @param runnable the runnable to run.
     * @return true if no exceptions where thrown.
     */
    public static final boolean catchLog(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch(Throwable e) {
            Logger.error("This is Topitils! We have caught an exception for you! Heres your stacktrace: %n %s", e.getStackTrace().toString());
            return false;
        }
    }
}
