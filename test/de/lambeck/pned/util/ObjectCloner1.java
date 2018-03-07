package de.lambeck.pned.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object-in-java
// -> Class SerializationUtils
// ->
// https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SerializationUtils.html#clone(T)

// https://dzone.com/articles/java-copy-shallow-vs-deep-in-which-you-will-swim

// http://javatechniques.com/blog/faster-deep-copies-of-java-objects/

// http://www.tutego.de/blog/javainsel/2013/09/tiefe-objektkopien-deep-copy/

// The try-with-resources Statement

@SuppressWarnings("javadoc")
public class ObjectCloner1 {

    // so that nobody can accidentally create an ObjectCloner object
    private ObjectCloner1() {
    }

    // returns a deep copy of an object
    static public Object deepCopy(Object oldObj) throws Exception {

        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {

            // out
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B

            // serialize and pass the object out
            oos.writeObject(oldObj);   // C
            oos.flush();               // D

            // in
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin);                  // F

            // read in and return the new object
            return ois.readObject(); // G

        } catch (Exception e) {
            System.out.println("Exception in ObjectCloner = " + e);
            throw (e);
        } finally {
            if (oos != null)
                oos.close();
            if (ois != null)
                ois.close();
        }

    }

}
