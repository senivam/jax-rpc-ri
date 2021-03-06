/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.xml.rpc.spi.model;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.model.Service
 */
public interface Service extends ModelObject {
    public Iterator getPorts();
    public QName getName();
    public List getPortsList();

    /**
     * TODO: better way to derive the generated service * implementation class
     * PE uses service.getJavaInterface() + _Impl  Should we provide a method
     * for it?
     */
    public JavaInterface getJavaIntf();
}
