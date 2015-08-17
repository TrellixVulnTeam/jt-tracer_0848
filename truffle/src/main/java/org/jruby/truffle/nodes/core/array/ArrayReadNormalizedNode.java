/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.core.array;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.runtime.RubyContext;

@NodeChildren({
        @NodeChild(value="array", type=RubyNode.class),
        @NodeChild(value="index", type=RubyNode.class)
})
@ImportStatic(ArrayGuards.class)
public abstract class ArrayReadNormalizedNode extends RubyNode {

    public ArrayReadNormalizedNode(RubyContext context, SourceSection sourceSection) {
        super(context, sourceSection);
    }

    public abstract Object executeRead(VirtualFrame frame, DynamicObject array, int index);

    // Anything from a null array is nil

    @Specialization(
            guards={"isRubyArray(array)", "isNullArray(array)"}
    )
    public DynamicObject readNull(DynamicObject array, int index) {
        return nil();
    }

    // Read within the bounds of an array with actual storage

    @Specialization(
            guards={"isRubyArray(array)", "isInBounds(array, index)", "isIntArray(array)"}
    )
    public int readIntInBounds(DynamicObject array, int index) {
        return ((int[]) ArrayNodes.getStore(array))[index];
    }

    @Specialization(
            guards={"isRubyArray(array)", "isInBounds(array, index)", "isLongArray(array)"}
    )
    public long readLongInBounds(DynamicObject array, int index) {
        return ((long[]) ArrayNodes.getStore(array))[index];
    }

    @Specialization(
            guards={"isRubyArray(array)", "isInBounds(array, index)", "isDoubleArray(array)"}
    )
    public double readDoubleInBounds(DynamicObject array, int index) {
        return ((double[]) ArrayNodes.getStore(array))[index];
    }

    @Specialization(
            guards={"isRubyArray(array)", "isInBounds(array, index)", "isObjectArray(array)"}
    )
    public Object readObjectInBounds(DynamicObject array, int index) {
        return ((Object[]) ArrayNodes.getStore(array))[index];
    }

    // Reading out of bounds is nil of any array is nil - cannot contain isNullArray

    @Specialization(
            guards={"isRubyArray(array)", "!isInBounds(array, index)"}
    )
    public DynamicObject readOutOfBounds(DynamicObject array, int index) {
        return nil();
    }

    // Guards

    protected static boolean isInBounds(DynamicObject array, int index) {
        return index >= 0 && index < ArrayNodes.getSize(array);
    }

}
