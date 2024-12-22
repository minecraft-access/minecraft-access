package org.mcaccess.minecraftaccess.utils.position;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrientationTest {

    @Test
    void testOppositeDirections() {
        Arrays.stream(Orientation.values())
                .forEach(o -> {
                    Orientation opposite = o.getOpposite();
                    assertEquals(-o.vector.getX(), opposite.vector.getX(), () -> o + " X");
                    assertEquals(-o.vector.getY(), opposite.vector.getY(), () -> o + " Y");
                    assertEquals(-o.vector.getZ(), opposite.vector.getZ(), () -> o + " Z");
                });
    }
}