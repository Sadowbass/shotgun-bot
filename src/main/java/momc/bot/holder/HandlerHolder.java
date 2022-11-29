package momc.bot.holder;

import com.google.common.reflect.ClassPath;
import momc.bot.CommandType;
import momc.bot.InteractionType;
import momc.bot.annotation.Handler;
import momc.bot.handlers.ShotgunHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HandlerHolder {

    private Map<CommandType, Map<InteractionType, List<ShotgunHandler>>> handlers;

    private HandlerHolder() {
        try {
            handlers = ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream().map(this::addHandlers)
                    .filter(Objects::nonNull)
                    .collect(
                            Collectors.groupingBy(
                                    ShotgunHandler::getCommandType,
                                    Collectors.groupingBy(ShotgunHandler::getInteractionType)
                            )
                    );


        } catch (IOException e) {
            //TODO add slf4j logger and log error
            System.out.println(e.getMessage());
            handlers = Collections.emptyMap();
        }
    }

    private ShotgunHandler addHandlers(ClassPath.ClassInfo classInfo) {
        if (isShotgunBotClass(classInfo)) {
            return loadClass(classInfo);
        }
        return null;
    }

    private boolean isShotgunBotClass(ClassPath.ClassInfo classInfo) {
        return classInfo.getPackageName().startsWith("momc.bot");
    }

    private ShotgunHandler loadClass(ClassPath.ClassInfo classInfo) {
        Class<?> load = classInfo.load();
        if (isHandler(load)) {
            return instantiate(load);
        }
        return null;
    }

    private boolean isHandler(Class<?> load) {
        return load.isAnnotationPresent(Handler.class) && ShotgunHandler.class.isAssignableFrom(load);
    }

    private ShotgunHandler instantiate(Class<?> clazz) {
        Constructor<?>[] cons = clazz.getDeclaredConstructors();
        try {
            return (ShotgunHandler) cons[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static Map<CommandType, Map<InteractionType, List<ShotgunHandler>>> getHandlers() {
        return HandlerHolderInstanceHolder.INSTANCE.handlers;
    }

    private static class HandlerHolderInstanceHolder {
        private static final HandlerHolder INSTANCE = new HandlerHolder();
    }
}
