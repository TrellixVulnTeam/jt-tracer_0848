/*
 * Copyright (c) 2015, 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.language.globals;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.RubyContext;
import org.jruby.truffle.instrumentation.Tracer;
import org.jruby.truffle.instrumentation.Tracer.UseGlobalDefStack;
import org.jruby.truffle.language.RubyNode;

public abstract class ReadGlobalVariableNode extends RubyNode implements UseGlobalDefStack {

    private final String name;

    public ReadGlobalVariableNode(RubyContext context, SourceSection sourceSection, String name) {
        super(context, sourceSection);
        this.name = name;
    }

    @Specialization(assumptions = "storage.getUnchangedAssumption()")
    public Object readConstant(
            @Cached("getStorage()") GlobalVariableStorage storage,
            @Cached("storage.getValue()") Object value) {
        return value;
    }

    @Specialization
    public Object read(@Cached("getStorage()") GlobalVariableStorage storage) {
        return storage.getValue();
    }

    protected GlobalVariableStorage getStorage() {
        return getContext().getCoreLibrary().getGlobalVariables().getStorage(name);
    }

    @Override
    public Object isDefined(VirtualFrame frame) {
        if (coreLibrary().getGlobalVariables().get(name) != nil()) {
            return coreStrings().GLOBAL_VARIABLE.createInstance();
        } else {
            return nil();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTaggedWith(Class<?> tag) {
        if (tag == Tracer.USE_GLOBAL_DEF_STACK_TAG)
            return true;
        else
            return super.isTaggedWith(tag);
    }
}
