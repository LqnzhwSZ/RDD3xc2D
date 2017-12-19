package de.lambeck.pned.models.gui;

import java.util.UUID;

@SuppressWarnings("javadoc")
public class UUIDTest {

    public static void main(String[] args) {
        UUID uuid1 = UUID.randomUUID();
        System.out.println(uuid1);

        UUID uuid2 = new UUID(0, 0);
        System.out.println(uuid2);
    }

}
