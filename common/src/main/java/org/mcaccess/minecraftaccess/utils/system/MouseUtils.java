package com.github.khanshoaib3.minecraft_access.utils.system;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

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
    private static CoreGraphicsInterface coreGraphicsInstance = null;
    private static CoreFoundationInterface coreFoundationInstance = null;
    private static ApplicationServicesInterface applicationServicesInstance = null;

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
        leftClick();
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
        rightClick();
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
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.mouseMoved.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

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

    /**
     * Perform left click at the current pixel location.
     */
    public static void leftClick() {
        doNativeMouseAction("left click", true,
                "xdotool click 1",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the mouse down event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.leftMouseDown.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);

                    // Wait 15 ms before releasing the button
                    try {
                    TimeUnit.MILLISECONDS.sleep(15);
                    } catch (Exception ignored) {
                    }

                    // Create the mouse up event
                    event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.leftMouseUp.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> {
                    i.mouse_event(WindowsMouseEventFlags.LEFTDOWN.getValue(), 0, 0, 0, 0);
                    i.mouse_event(WindowsMouseEventFlags.LEFTUP.getValue(), 0, 0, 0, 0);
                }
        );
    }

    /**
     * Press left mouse key down at the current pixel location.
     */
    public static void leftDown() {
        doNativeMouseAction("left down", true,
                "xdotool mousedown 1",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.leftMouseDown.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.LEFTDOWN.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Press left mouse key up at the current pixel location.
     */
    public static void leftUp() {
        doNativeMouseAction("left up", true,
                "xdotool mouseup 1",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.leftMouseUp.getValue(), position, CoreGraphicsMouseButtons.left.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.LEFTUP.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Perform middle click at the current pixel location.
     */
    @SuppressWarnings("unused")
    public static void middleClick() {
        doNativeMouseAction("middle click", true,
                "xdotool click 2",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the mouse down event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.otherMouseDown.getValue(), position, CoreGraphicsMouseButtons.center.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);

                    // Wait 15 ms before releasing the button
                    try {
                    TimeUnit.MILLISECONDS.sleep(15);
                    } catch (Exception ignored) {
                    }

                    // Create the mouse up event
                    event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.otherMouseUp.getValue(), position, CoreGraphicsMouseButtons.center.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> {
                    i.mouse_event(WindowsMouseEventFlags.MIDDLEDOWN.getValue(), 0, 0, 0, 0);
                    i.mouse_event(WindowsMouseEventFlags.MIDDLEUP.getValue(), 0, 0, 0, 0);
                }
        );
    }

    /**
     * Press middle mouse key down at the current pixel location.
     */
    public static void middleDown() {
        doNativeMouseAction("middle down", true,
                "xdotool mousedown 2",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.otherMouseDown.getValue(), position, CoreGraphicsMouseButtons.center.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.MIDDLEDOWN.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Press middle mouse key up at the current pixel location.
     */
    public static void middleUp() {
        doNativeMouseAction("middle up", true,
                "xdotool mouseup 2",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.otherMouseUp.getValue(), position, CoreGraphicsMouseButtons.center.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.MIDDLEUP.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Perform right click at the current pixel location.
     */
    public static void rightClick() {
        doNativeMouseAction("right click", true,
                "xdotool click 3",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the mouse down event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.rightMouseDown.getValue(), position, CoreGraphicsMouseButtons.right.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);

                    // Wait 15 ms before releasing the button
                    try {
                    TimeUnit.MILLISECONDS.sleep(15);
                    } catch (Exception ignored) {
                    }

                    // Create the mouse up event
                    event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.rightMouseUp.getValue(), position, CoreGraphicsMouseButtons.right.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> {
                    i.mouse_event(WindowsMouseEventFlags.RIGHTDOWN.getValue(), 0, 0, 0, 0);
                    i.mouse_event(WindowsMouseEventFlags.RIGHTUP.getValue(), 0, 0, 0, 0);
                }
        );
    }

    /**
     * Press right mouse key down at the current pixel location.
     */
    public static void rightDown() {
        doNativeMouseAction("right down", true,
                "xdotool mousedown 3",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.rightMouseDown.getValue(), position, CoreGraphicsMouseButtons.right.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.RIGHTDOWN.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Press right mouse key up at the current pixel location.
     */
    public static void rightUp() {
        doNativeMouseAction("right up", true,
                "xdotool mouseup 3",
                (i) -> {
                    // Get the current position of the mouse by creating an empty event and getting its location
                    Pointer dummyEvent = i.CGEventCreate(new Pointer(0));
                    CoreGraphicsInterface.CGPoint.ByValue position = i.CGEventGetLocation(dummyEvent);
                    coreFoundationInstance.CFRelease(dummyEvent);

                    // Create the event
                    Pointer event = i.CGEventCreateMouseEvent(new Pointer(0), CoreGraphicsMouseEventTypes.rightMouseUp.getValue(), position, CoreGraphicsMouseButtons.right.getValue());

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.RIGHTUP.getValue(), 0, 0, 0, 0)
        );
    }

    /**
     * Performs mouse scroll up
     */
    public static void scrollUp() {
        doNativeMouseAction("scroll up", false,
                "xdotool click 4",
                (i) -> {
                    // Create an event that scrolls the wheel up by 1 line
                    Pointer event = i.CGEventCreateScrollWheelEvent(new Pointer(0), CoreGraphicsScrollEventUnits.line.getValue(), 1, 1);

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.WHEEL.getValue(), 0, 0, 120, 0)
        );
    }

    /**
     * Performs mouse scroll down
     */
    public static void scrollDown() {
        doNativeMouseAction("scroll down", false,
                "xdotool click 5",
                (i) -> {
                    // Create an event that scrolls the wheel down by 1 line
                    Pointer event = i.CGEventCreateScrollWheelEvent(new Pointer(0), CoreGraphicsScrollEventUnits.line.getValue(), 1, -1);

                    // Send the event
                    i.CGEventPost(CoreGraphicsEventTapLocations.hid.getValue(), event);

                    // Release the event so CoreFoundation can free it
                    coreFoundationInstance.CFRelease(event);
                },
                (i) -> i.mouse_event(WindowsMouseEventFlags.WHEEL.getValue(), 0, 0, -120, 0));
    }

    private static void doNativeMouseAction(String name, boolean logCoordinates, String linuxXdotCommand, Consumer<CoreGraphicsInterface> macOSAction, Consumer<user32dllInterface> windowsAction) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null)
            return;


        try {
            String coordinates = "";
            if (logCoordinates) {
                int x = (int) minecraftClient.mouse.getX(), y = (int) minecraftClient.mouse.getY();
                coordinates = " on x:%d y:%d".formatted(x, y);
            }
           log.debug("Performing " + name + coordinates);


            if (OsUtils.isLinux()) {
                Runtime.getRuntime().exec(linuxXdotCommand);
            } else if (OsUtils.isMacOS()) {
                if (coreGraphicsInstance == null) initializeCoreGraphics();

        // Check if the accessibility permission has been granted
        // If not, mouse simulation will not work, so inform the user
        // 0 is false, 1 is true
        if (applicationServicesInstance.AXIsProcessTrusted() == 0) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.messages.accessibility_permission_not_granted"), false);
            return;
        }

                macOSAction.accept(coreGraphicsInstance);
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return new Coordinates(x, y);
        Window window = client.getWindow();
        if (window == null) return new Coordinates(x, y);

        int realX = (int) (window.getX() + (x * window.getScaleFactor()));
        int realY = (int) (window.getY() + (y * window.getScaleFactor()));
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
            coreGraphicsInstance = Native.load("CoreGraphics", CoreGraphicsInterface.class);
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
    @SuppressWarnings("unused")
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
}
