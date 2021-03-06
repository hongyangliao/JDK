/*
 * Copyright (c) 2013, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.graalvm.compiler.nodes;

import static org.graalvm.compiler.nodeinfo.InputType.State;
import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_UNKNOWN;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_UNKNOWN;

import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.IterableNodeType;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;

import jdk.vm.ci.meta.MetaAccessProvider;

/**
 * This node represents an unconditional explicit request for immediate deoptimization.
 *
 * After this node, execution will continue using a fallback execution engine (such as an
 * interpreter) at the position described by the {@link #stateBefore() deoptimization state}.
 */
// @formatter:off
@NodeInfo(cycles = CYCLES_UNKNOWN,
          cyclesRationale = "The cycles for a deopt are as high as possible as we continue execution in the interpreter, " +
                            "but they pollute the cost model, thus we do not care about their cycles.",
          size = SIZE_UNKNOWN,
          sizeRationale = "Deopts carry the meta information necessary to map the state back in the interpreter, but they pollute the cost model," +
                          "thus we do not care about their size.")
// @formatter:on
public abstract class AbstractDeoptimizeNode extends ControlSinkNode implements IterableNodeType, DeoptimizingNode.DeoptBefore {

    public static final NodeClass<AbstractDeoptimizeNode> TYPE = NodeClass.create(AbstractDeoptimizeNode.class);
    @OptionalInput(State) FrameState stateBefore;

    protected AbstractDeoptimizeNode(NodeClass<? extends AbstractDeoptimizeNode> c, FrameState stateBefore) {
        super(c, StampFactory.forVoid());
        this.stateBefore = stateBefore;
    }

    @Override
    public boolean canDeoptimize() {
        return true;
    }

    @Override
    public FrameState stateBefore() {
        return stateBefore;
    }

    @Override
    public void setStateBefore(FrameState f) {
        updateUsages(stateBefore, f);
        stateBefore = f;
    }

    public abstract ValueNode getActionAndReason(MetaAccessProvider metaAccess);

    public abstract ValueNode getSpeculation(MetaAccessProvider metaAccess);
}
