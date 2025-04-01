package org.mcaccess.minecraftaccess.utils.system;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.MouseHandlerAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Contains functions to simulate mouse events.
 */
@Slf4j
public class MouseUtils {
    private static user32dllInterface user32dllInstance = null;
    private static CGWrapper cgWrapper = null;
    private static CoreFoundationInterface coreFoundationInstance = null;
    private static ApplicationServicesInterface applicationServicesInstance = null;
    /**
     * The {@link Minecraft} is singleton and {@link Minecraft#window} only be initialized once in constructor,
     * so this is safe to cache it.
     */
    private static long windowPointer = 0;

    /**
     * Move the mouse to the given pixel location and then perform left click.
     *
     * @param x the x position of the pixel location
     * @param y the y position of the pixel location
     */
    public static void moveAndLeftClick(int x, int y) {
        move(x, y);
        // fix the https://github.com/khanshoaib3/minecraft-access/issues/65
        if (OsUtils.isWindows()) {
            try {
                // with a little bit of waiting, everything is ok now.
                // I've tried to set the value to 10, and it doesn't always work, 20 is fine.
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (Exception ignored) {
            }
        }
        Key.LEFT.click();
    }

    /**
     * Move the mouse to the given pixel location and then perform right click.
     *
     * @param x the x position of the pixel location
     * @param y the y position of the pixel location
     */
    @SuppressWarnings("unused")
    public static void moveAndRightClick(int x, int y) {
        move(x, y);
        // fix the https://github.com/khanshoaib3/minecraft-access/issues/65
        if (OsUtils.isWindows()) {
            try {
                // with a little bit of waiting, everything is ok now.
                // I've tried to set the value to 10, and it doesn't always work, 20 is fine.
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (Exception ignored) {
            }
        }
        Key.RIGHT.click();
    }

    /**
     * Move the mouse to the given pixel location.
     *
     * @param x the x position of the pixel location
     * @param y the y position of the pixel location
     */
    public static void move(int x, int y) {
        doNativeMouseAction("mouse moving", true,
                "xdotool mousemove %d %d".formatted(x, y),
                (i) -> {
                    // Create a CGPoint containing the destination position
                    CoreGraphicsInterface.CGPoint.ByValue position = new CoreGraphicsInterface.CGPoint.ByValue((double) x, (double) y);

                    // Create the event
                    // Mouse button is ignored
                    Pointer event = i.cgInstance.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.mouseMoved.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.cgInstance.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> {
                    if (!i.SetCursorPos(x, y)) log.error("\nError encountered on moving mouse.");
                }
        );
    }

    /**
     * Move the mouse to the given pixel location after a delay.
     *
     * @param x     the x position of the pixel location
     * @param y     the y position of the pixel location
     * @param delay delay amount in milliseconds
     */
    public static void moveAfterDelay(int x, int y, int delay) {
        try {
            log.debug("Moving mouse to x:%d y:%d after %d milliseconds".formatted(x, y, delay));
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    move(x, y);
                }
            };
            new Timer().schedule(timerTask, delay);
        } catch (Exception e) {
            log.error("Error encountered on moving mouse.", e);
        }
    }

    private static void doNativeMouseAction(String name, boolean logCoordinates, String linuxXdotCommand, Consumer<CGWrapper> macOSAction, Consumer<user32dllInterface> windowsAction) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null)
            return;


        try {
            String coordinates = "";
            if (logCoordinates) {
                int x = (int) minecraftClient.mouseHandler.xpos(), y = (int) minecraftClient.mouseHandler.ypos();
                coordinates = " at minecraft x:%d y:%d".formatted(x, y);
            }
            log.debug("Performing {}{}", name, coordinates);


            if (OsUtils.isLinux()) {
                Runtime.getRuntime().exec(linuxXdotCommand);
            } else if (OsUtils.isMacOS()) {
                if (cgWrapper == null) initializeCoreGraphics();

                // Check if the accessibility permission has been granted
                // If not, mouse simulation will not work, so inform the user
                // 0 is false, 1 is true
                if (applicationServicesInstance.AXIsProcessTrusted() == 0) {
                    MainClass.speakWithNarrator(I18n.get("minecraft_access.messages.accessibility_permission_not_granted"), false);
                    return;
                }

                if (logCoordinates) {
                    var p = cgWrapper.getNativeMousePosition();
                    String nativeCoordinates = " at native x:%f y:%f".formatted(p.x, p.y);
                    log.debug("\nPerforming " + name + nativeCoordinates);
                }
                macOSAction.accept(cgWrapper);
            } else if (OsUtils.isWindows()) {
                if (user32dllInstance == null) initializeUser32dll();
                windowsAction.accept(user32dllInstance);
            }
        } catch (Exception e) {
            log.error("Error encountered on performing " + name + ".", e);
        }
    }

    public record Coordinates(int x, int y) {
    }

    public static Coordinates calcRealPositionOfWidget(int x, int y) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return new Coordinates(x, y);
        Window window = client.getWindow();
        if (window == null) return new Coordinates(x, y);

        int realX, realY;
        if (Config.getInstance().mouseSimulation.macMouseFix) {
            realX = (int) ((x * window.getGuiScale()));
            realY = (int) ((y * window.getGuiScale()));
        } else {
            realX = (int) (window.getX() + (x * window.getGuiScale()));
            realY = (int) (window.getY() + (y * window.getGuiScale()));
        }
        return new Coordinates(realX, realY);
    }

    public static void move(Coordinates coordinates) {
        move(coordinates.x(), coordinates.y());
    }

    /**
     * Preform a mouse event at the given location
     *
     * @param x        x coordinate
     * @param y        y coordinate
     * @param consumer event
     */
    public static void performAt(int x, int y, Consumer<Coordinates> consumer) {
        consumer.accept(calcRealPositionOfWidget(x, y));
    }

    /**
     * Initializes the User32.dll for windows
     */
    private static void initializeUser32dll() {
        if (!OsUtils.isWindows())
            return;

        try {
            user32dllInstance = Native.load("User32.dll", user32dllInterface.class);
        } catch (Exception e) {
            log.error("Error encountered while initializing User32.dll", e);
        }
    }

    /**
     * Initializes the CoreGraphics and CoreFoundation frameworks for MacOS
     */
    private static void initializeCoreGraphics() {
        if (!OsUtils.isMacOS())
            return;

        try {
            cgWrapper = new CGWrapper(Native.load("CoreGraphics", CoreGraphicsInterface.class));
            coreFoundationInstance = Native.load("CoreFoundation", CoreFoundationInterface.class);
            applicationServicesInstance = Native.load("ApplicationServices", ApplicationServicesInterface.class);
        } catch (Exception e) {
            log.error("Error encountered while initializing CoreGraphics or CoreFoundation", e);
        }
    }

    /**
     * Contains definition for SetCursorPos() and mouse_event() of User32.dll
     */
    private interface user32dllInterface extends Library {
        // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setcursorpos
        boolean SetCursorPos(int x, int y);

        // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-mouse_event?source=recommendations
        // https://stackoverflow.com/questions/8739523/directing-mouse-events-dllimportuser32-dll-click-double-click
        // https://stackoverflow.com/questions/37262822/c-sharp-simulate-mouse-wheel-down
        void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);
    }

    /**
     * Flags used in the mouse_event function of User32.dll
     */
    private enum WindowsMouseEventFlags {
        LEFTDOWN(0x00000002),
        LEFTUP(0x00000004),
        MIDDLEDOWN(0x00000020),
        MIDDLEUP(0x00000040),
        MOVE(0x00000001),
        ABSOLUTE(0x00008000),
        RIGHTDOWN(0x00000008),
        RIGHTUP(0x00000010),
        WHEEL(0x00000800);

        private final int value;

        WindowsMouseEventFlags(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Contains needed definitions for functions in CoreGraphics to send mouse events
     */
    private interface CoreGraphicsInterface extends Library {
        class CGPoint extends Structure {
            public static class ByValue extends CGPoint implements Structure.ByValue {
                public ByValue() {
                    super();
                }

                public ByValue(double x, double y) {
                    super(x, y);
                }
            }

            public double x;
            public double y;

            @SuppressWarnings({"unchecked", "rawtypes"})
            protected List getFieldOrder() {
                return Arrays.asList("x", "y");
            }

            public CGPoint() {
                super();
            }

            public CGPoint(double X, double Y) {
                super();
                x = X;
                y = Y;
            }
        }

        // https://developer.apple.com/documentation/coregraphics/1454356-cgeventcreatemouseevent
        Pointer CGEventCreateMouseEvent(Pointer source, int mouseType, CGPoint.ByValue mouseCursorPosition, int mouseButton);

        // https://developer.apple.com/documentation/coregraphics/1541327-cgeventcreatescrollwheelevent
        Pointer CGEventCreateScrollWheelEvent(Pointer source, int units, int wheelCount, int wheel1);

        // https://developer.apple.com/documentation/coregraphics/1456527-cgeventpost
        Pointer CGEventPost(int tap, Pointer event);

        // https://developer.apple.com/documentation/coregraphics/1454913-cgeventcreate
        Pointer CGEventCreate(Pointer source);

        // https://developer.apple.com/documentation/coregraphics/1455788-location
        CGPoint.ByValue CGEventGetLocation(Pointer event);
    }

    /**
     * Contains definition of the CFRelease function, needed to release mouse events when we are done with them
     */
    private interface CoreFoundationInterface extends Library {
        // https://developer.apple.com/documentation/corefoundation/1521153-cfrelease
        Pointer CFRelease(Pointer object);
    }

    /**
     * CoreGraphics mouse event types
     */
    @SuppressWarnings("unused")
    private enum CoreGraphicsMouseEventTypes {
        none(0),
        leftMouseDown(1),
        leftMouseUp(2),
        rightMouseDown(3),
        rightMouseUp(4),
        mouseMoved(5),
        otherMouseDown(25),
        otherMouseUp(26);

        private final int value;

        CoreGraphicsMouseEventTypes(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    private enum CoreGraphicsMouseButtons {
        left(0),
        right(1),
        center(2);

        private final int value;

        CoreGraphicsMouseButtons(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    private enum CoreGraphicsScrollEventUnits {
        pixel(0),
        line(1);

        private final int value;

        CoreGraphicsScrollEventUnits(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    private enum CoreGraphicsEventTapLocations {
        hid(0),
        session(1),
        annotatedSession(2);

        private final int value;

        CoreGraphicsEventTapLocations(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Contains the AXIsProcessTrusted function, which checks if the accessibility permission has been enabled
     */
    private interface ApplicationServicesInterface extends Library {
        // https://developer.apple.com/documentation/applicationservices/1460720-axisprocesstrusted
        byte AXIsProcessTrusted();
    }

    private static class CGWrapper {
        private CoreGraphicsInterface cgInstance;

        public CGWrapper(CoreGraphicsInterface instance) {
            cgInstance = instance;
        }

        /**
         * Creates a pointer event, extracts the x,y coordinates of its location, frees the event and then returns the coordinates
         */
        public CoreGraphicsInterface.CGPoint.ByValue getNativeMousePosition() {
            Pointer dummyEvent = cgInstance.CGEventCreate(new Pointer(0));
            var position = cgInstance.CGEventGetLocation(dummyEvent);
            coreFoundationInstance.CFRelease(dummyEvent);
            return position;
        }
    }

    private static MouseHandlerAccessor getMouseHandler() {
        return (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
    }

    private static long getWindowPointer() {
        if (windowPointer == 0) {
            windowPointer = Minecraft.getInstance().getWindow().getWindow();
        }
        return windowPointer;
    }

    public enum Key {
        LEFT(InputConstants.MOUSE_BUTTON_LEFT),
        RIGHT(InputConstants.MOUSE_BUTTON_RIGHT),
        MIDDLE(InputConstants.MOUSE_BUTTON_MIDDLE);

        public final int id;

        Key(int buttonId) {
            id = buttonId;
        }

        public void click() {
            press();
            release();
        }

        public void press() {
            operate(this, InputConstants.PRESS);
        }

        public void release() {
            operate(this, InputConstants.RELEASE);
        }

        private static void operate(Key key, int action) {
            // basing on MouseHandler.onPress():
            // if (Minecraft.ON_OSX && button == 0)
            // run macOS related logic
            int modifiers = Minecraft.ON_OSX ? 0 : 1;
            getMouseHandler().press(getWindowPointer(), key.id, action, modifiers);
        }
    }

    public enum Wheel {
        UP,
        DOWN;

        public void scroll() {
            // captured real mouse scrolling always results in x=0, y=1/-1
            int offset = this == Wheel.UP ? 1 : -1;
            getMouseHandler().scroll(getWindowPointer(), 0, offset);
        }
    }
}
