package com.demo;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isChildOf;

public class ExtractionClassFileTransformer implements ClassFileTransformer {

    /**
     * An indicator that an attempted class file transformation did not alter the handed class file.
     */
    private static final byte[] DO_NOT_TRANSFORM = null;

    /**
     * The class loader that is expected to have loaded the looked-up a class.
     */
    private final ClassLoader classLoader;

    /**
     * The name of the type to look up.
     */
    private final String typeName;

    /**
     * The binary representation of the looked-up class.
     */
    private volatile byte[] binaryRepresentation;

    /**
     * Creates a class file transformer for the purpose of extraction.
     *
     * @param classLoader The class loader that is expected to have loaded the looked-up a class.
     * @param typeName    The name of the type to look up.
     */
    protected ExtractionClassFileTransformer(ClassLoader classLoader, String typeName) {
        this.classLoader = classLoader;
        this.typeName = typeName;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(ClassLoader classLoader,
                            String internalName,
                            Class<?> redefinedType,
                            ProtectionDomain protectionDomain,
                            byte[] binaryRepresentation) {
        if (internalName != null && isChildOf(this.classLoader).matches(classLoader) && typeName.equals(internalName.replace('/', '.'))) {
            this.binaryRepresentation = binaryRepresentation.clone();
        }
        return DO_NOT_TRANSFORM;
    }

    /**
     * Returns the binary representation of the class file that was looked up. The returned array must never be modified.
     *
     * @return The binary representation of the class file or {@code null} if no such class file could
     * be located.
     */
    protected byte[] getBinaryRepresentation() {
        return binaryRepresentation;
    }
}