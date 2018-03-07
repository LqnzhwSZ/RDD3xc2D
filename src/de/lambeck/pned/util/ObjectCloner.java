package de.lambeck.pned.util;

import java.io.*;

import de.lambeck.pned.exceptions.PNObjectNotClonedException;

/**
 * Class for cloning (deep copying) of objects.<BR>
 * <BR>
 * Based on:
 * http://www.tutego.de/blog/javainsel/2013/09/tiefe-objektkopien-deep-copy/
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ObjectCloner {

    /*
     * Original method from:
     * http://www.tutego.de/blog/javainsel/2013/09/tiefe-objektkopien-deep-copy/
     */

    // public static Object deepCopy(Object o) throws Exception {
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // new ObjectOutputStream(baos).writeObject(o);
    //
    // ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    //
    // return new ObjectInputStream(bais).readObject();
    // }

    /*
     * My variation that throws a specific Exception.
     */

    /**
     * Creates and returns a clone (deep copy) of the specified object where all
     * referenced objects are clones as well.<BR>
     * <BR>
     * Note: Converts the specified object to a stream of bytes and restores a
     * copy from that stream of bytes.
     * 
     * @param o
     *            the object to be cloned
     * @return a clone of the specified object.
     * @throws PNObjectNotClonedException
     *             if errors occurred and the object could not be cloned.
     */
    public static Object deepCopy(Object o) throws PNObjectNotClonedException {

        // TODO Use try-with-resources Statement?

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(baos).writeObject(o);
        } catch (IOException e) {
            System.err.println(e.getMessage());

            String message = "ObjectCloner: Unable to serialize: " + o.toString();
            System.err.println(message);

            throw new PNObjectNotClonedException(e.getMessage());
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try {
            return new ObjectInputStream(bais).readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());

            String message = "ObjectCloner: Unable to deserialize: " + o.toString();
            System.err.println(message);

            throw new PNObjectNotClonedException(e.getMessage());
        }
    }

}
