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

package com.sun.xml.rpc.tools.wscompile;

import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 *
 */
public interface UsageIf {
    
    /**
     * @return how to use the options of wscompile
     */
    public Localizable getOptionsUsage();
    
    /**
     * @return how to use the wscompile features
     */
    public Localizable getFeaturesUsage();
    
    /**
     * @return intals of wscompile
     */
    public Localizable getInternalUsage();
    
    /**
     * @return some examples of how to use wscompile
     */
    public Localizable getExamplesUsage();
    
    /**
     * After processing the argument in the array, mark the index as null
     * @param args
     * @return false if there is a problem with the expected arguments
     */
    public boolean parseArguments(String[] args, UsageError err);
    
    public class UsageError {
        public Localizable msg;
    }
}
