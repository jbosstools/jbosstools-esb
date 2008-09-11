package org.jboss.tools.esb.core.model;

import org.jboss.tools.common.model.filesystems.impl.SimpleFileImpl;
import org.jboss.tools.common.model.impl.OrderedByEntityChildren;
import org.jboss.tools.common.model.impl.RegularChildren;

public class ESBFileImpl extends SimpleFileImpl {
    private static final long serialVersionUID = 0L;

    public ESBFileImpl() {}

    protected RegularChildren createChildren() {
    	return new OrderedByEntityChildren();
    }

}
