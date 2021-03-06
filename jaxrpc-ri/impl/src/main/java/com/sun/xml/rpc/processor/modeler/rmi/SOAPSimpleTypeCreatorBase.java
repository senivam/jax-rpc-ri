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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPWSDLConstants;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 * @author JAX-RPC Development Team
 *
 * Base class for SOAPSimpleTypeCreatorBase
 */
public abstract class SOAPSimpleTypeCreatorBase
    extends JavaSimpleTypeCreator
    implements RmiConstants {
    //  xsd: simpletypes
    public SOAPSimpleType XSD_BOOLEAN_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_BOOLEAN_SOAPTYPE;
    public SOAPSimpleType XSD_BYTE_SOAPTYPE;
    public SOAPSimpleType XSD_BYTE_ARRAY_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_BYTE_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_BYTE_ARRAY_SOAPTYPE;
    public SOAPSimpleType XSD_DOUBLE_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_DOUBLE_SOAPTYPE;
    public SOAPSimpleType XSD_FLOAT_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_FLOAT_SOAPTYPE;
    public SOAPSimpleType XSD_INT_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_INTEGER_SOAPTYPE;
    public SOAPSimpleType XSD_INTEGER_SOAPTYPE;
    public SOAPSimpleType XSD_LONG_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_LONG_SOAPTYPE;
    public SOAPSimpleType XSD_SHORT_SOAPTYPE;
    public SOAPSimpleType XSD_BOXED_SHORT_SOAPTYPE;
    public SOAPSimpleType XSD_DECIMAL_SOAPTYPE;
    public SOAPSimpleType XSD_DATE_TIME_SOAPTYPE;
    public SOAPSimpleType XSD_DATE_TIME_CALENDAR_SOAPTYPE;
    public SOAPSimpleType XSD_STRING_SOAPTYPE;
    public SOAPSimpleType XSD_QNAME_SOAPTYPE;
    public SOAPSimpleType XSD_VOID_SOAPTYPE;
    public SOAPAnyType XSD_ANYTYPE_SOAPTYPE;
    //     public SOAPSimpleType XSD_ANYTYPE_SOAPTYPE;
    public SOAPSimpleType XSD_ANY_URI_SOAPTYPE;

    // soap-enc: simple types
    public SOAPSimpleType SOAP_BOXED_BOOLEAN_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_BYTE_SOAPTYPE;
    public SOAPSimpleType SOAP_BYTE_ARRAY_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_BYTE_ARRAY_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_DOUBLE_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_FLOAT_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_INTEGER_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_LONG_SOAPTYPE;
    public SOAPSimpleType SOAP_BOXED_SHORT_SOAPTYPE;
    public SOAPSimpleType SOAP_DECIMAL_SOAPTYPE;
    public SOAPSimpleType SOAP_DATE_TIME_SOAPTYPE;
    public SOAPSimpleType SOAP_STRING_SOAPTYPE;
    public SOAPSimpleType SOAP_QNAME_SOAPTYPE;

    // Collection types    
    public SOAPSimpleType COLLECTION_SOAPTYPE;
    public SOAPSimpleType LIST_SOAPTYPE;
    public SOAPSimpleType SET_SOAPTYPE;
    public SOAPSimpleType VECTOR_SOAPTYPE;
    public SOAPSimpleType STACK_SOAPTYPE;
    public SOAPSimpleType LINKED_LIST_SOAPTYPE;
    public SOAPSimpleType ARRAY_LIST_SOAPTYPE;
    public SOAPSimpleType HASH_SET_SOAPTYPE;
    public SOAPSimpleType TREE_SET_SOAPTYPE;

    // Map types    
    public SOAPSimpleType MAP_SOAPTYPE;
    public SOAPSimpleType HASH_MAP_SOAPTYPE;
    public SOAPSimpleType TREE_MAP_SOAPTYPE;
    public SOAPSimpleType HASHTABLE_SOAPTYPE;
    public SOAPSimpleType PROPERTIES_SOAPTYPE;
    //     public SOAPSimpleType WEAK_HASH_MAP_SOAPTYPE;
    public SOAPSimpleType JAX_RPC_MAP_ENTRY_SOAPTYPE;

    // Types for attachments
    public SOAPSimpleType IMAGE_SOAPTYPE;
    public SOAPSimpleType MIME_MULTIPART_SOAPTYPE;
    public SOAPSimpleType SOURCE_SOAPTYPE;
    public SOAPSimpleType DATA_HANDLER_SOAPTYPE;

    protected boolean useStrictMode = false;

    public SOAPSimpleTypeCreatorBase() {
        initSOAPTypes(SOAPVersion.SOAP_11);
    }

    public SOAPSimpleTypeCreatorBase(boolean useStrictMode) {
        this(useStrictMode, SOAPVersion.SOAP_11);
    }

    public SOAPSimpleTypeCreatorBase(
        boolean useStrictMode,
        SOAPVersion version) {
        this.useStrictMode = useStrictMode;
        initSOAPTypes(version);
    }

    protected void initSOAPTypes(SOAPVersion version) {
        SOAPWSDLConstants soapConstants =
            SOAPConstantsFactory.getSOAPWSDLConstants(version);
        // xsd: simpletypes
        XSD_BOOLEAN_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BOOLEAN,
                BOOLEAN_JAVATYPE,
                false,
                version);
        XSD_BOXED_BOOLEAN_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BOOLEAN,
                BOXED_BOOLEAN_JAVATYPE,
                false,
                version);
        XSD_BYTE_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BYTE,
                BYTE_JAVATYPE,
                false,
                version);
        XSD_BYTE_ARRAY_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BASE64_BINARY,
                BYTE_ARRAY_JAVATYPE,
                true,
                version);
        XSD_BOXED_BYTE_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BYTE,
                BOXED_BYTE_JAVATYPE,
                false,
                version);
        XSD_BOXED_BYTE_ARRAY_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.BASE64_BINARY,
                BOXED_BYTE_ARRAY_JAVATYPE,
                true,
                version);
        XSD_DOUBLE_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.DOUBLE,
                DOUBLE_JAVATYPE,
                false,
                version);
        XSD_BOXED_DOUBLE_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.DOUBLE,
                BOXED_DOUBLE_JAVATYPE,
                false,
                version);
        XSD_FLOAT_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.FLOAT,
                FLOAT_JAVATYPE,
                false,
                version);
        XSD_BOXED_FLOAT_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.FLOAT,
                BOXED_FLOAT_JAVATYPE,
                false,
                version);
        XSD_INT_SOAPTYPE =
            new SOAPSimpleType(BuiltInTypes.INT, INT_JAVATYPE, false, version);
        XSD_BOXED_INTEGER_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.INT,
                BOXED_INTEGER_JAVATYPE,
                false,
                version);
        XSD_INTEGER_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.INTEGER,
                BIG_INTEGER_JAVATYPE,
                false,
                version);
        XSD_LONG_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.LONG,
                LONG_JAVATYPE,
                false,
                version);
        XSD_BOXED_LONG_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.LONG,
                BOXED_LONG_JAVATYPE,
                false,
                version);
        XSD_SHORT_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.SHORT,
                SHORT_JAVATYPE,
                false,
                version);
        XSD_BOXED_SHORT_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.SHORT,
                BOXED_SHORT_JAVATYPE,
                false,
                version);
        XSD_DECIMAL_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.DECIMAL,
                DECIMAL_JAVATYPE,
                false,
                version);
        XSD_DATE_TIME_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.DATE_TIME,
                DATE_JAVATYPE,
                false,
                version);
        XSD_DATE_TIME_CALENDAR_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.DATE_TIME,
                CALENDAR_JAVATYPE,
                false,
                version);
        XSD_STRING_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.STRING,
                STRING_JAVATYPE,
                true,
                version);
        XSD_QNAME_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.QNAME,
                QNAME_JAVATYPE,
                false,
                version);
        XSD_VOID_SOAPTYPE =
            new SOAPSimpleType(null, VOID_JAVATYPE, false, version);
        XSD_ANYTYPE_SOAPTYPE =
            new SOAPAnyType(
                SchemaConstants.QNAME_TYPE_URTYPE,
                OBJECT_JAVATYPE,
                version);
        XSD_ANY_URI_SOAPTYPE =
            new SOAPSimpleType(
                BuiltInTypes.ANY_URI,
                URI_JAVATYPE,
                false,
                version);

        // soap-enc: simple types
        SOAP_BOXED_BOOLEAN_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeBoolean(),
                BOXED_BOOLEAN_JAVATYPE,
                version);
        SOAP_BOXED_BYTE_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeByte(),
                BOXED_BYTE_JAVATYPE,
                version);
        SOAP_BYTE_ARRAY_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeBase64(),
                BYTE_ARRAY_JAVATYPE,
                version);
        SOAP_BOXED_BYTE_ARRAY_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeBase64(),
                BOXED_BYTE_ARRAY_JAVATYPE,
                version);
        SOAP_BOXED_DOUBLE_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeDouble(),
                BOXED_DOUBLE_JAVATYPE,
                version);
        SOAP_BOXED_FLOAT_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeFloat(),
                BOXED_FLOAT_JAVATYPE,
                version);
        SOAP_BOXED_INTEGER_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeInt(),
                BOXED_INTEGER_JAVATYPE,
                version);
        SOAP_BOXED_LONG_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeLong(),
                BOXED_LONG_JAVATYPE,
                version);
        SOAP_BOXED_SHORT_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeShort(),
                BOXED_SHORT_JAVATYPE,
                version);
        SOAP_DECIMAL_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeDecimal(),
                DECIMAL_JAVATYPE,
                version);
        SOAP_DATE_TIME_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeDateTime(),
                DATE_JAVATYPE,
                version);
        SOAP_STRING_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeString(),
                STRING_JAVATYPE,
                version);
        SOAP_QNAME_SOAPTYPE =
            new SOAPSimpleType(
                soapConstants.getQNameTypeQName(),
                QNAME_JAVATYPE,
                version);

        // Collection types    
        COLLECTION_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_COLLECTION,
                COLLECTION_JAVATYPE,
                version);
        LIST_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_LIST, LIST_JAVATYPE, version);
        SET_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_SET, SET_JAVATYPE, version);
        VECTOR_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_VECTOR, VECTOR_JAVATYPE, version);
        STACK_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_STACK, STACK_JAVATYPE, version);
        LINKED_LIST_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_LINKED_LIST,
                LINKED_LIST_JAVATYPE,
                version);
        ARRAY_LIST_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_ARRAY_LIST,
                ARRAY_LIST_JAVATYPE,
                version);
        HASH_SET_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_HASH_SET, HASH_SET_JAVATYPE, version);
        TREE_SET_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_TREE_SET, TREE_SET_JAVATYPE, version);

        // Map types    
        MAP_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_MAP, MAP_JAVATYPE, version);
        HASH_MAP_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_HASH_MAP, HASH_MAP_JAVATYPE, version);
        TREE_MAP_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_TREE_MAP, TREE_MAP_JAVATYPE, version);
        HASHTABLE_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_HASHTABLE,
                HASHTABLE_JAVATYPE,
                version);
        PROPERTIES_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_PROPERTIES,
                PROPERTIES_JAVATYPE,
                version);
        //     WEAK_HASH_MAP_SOAPTYPE           = new SOAPSimpleType(QNAME_TYPE_WEAK_HASH_MAP, WEAK_HASH_MAP_JAVATYPE, version);
        JAX_RPC_MAP_ENTRY_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_JAX_RPC_MAP_ENTRY,
                JAX_RPC_MAP_ENTRY_JAVATYPE,
                version);

        // Types for attachments
        IMAGE_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_IMAGE, IMAGE_JAVATYPE, version);
        MIME_MULTIPART_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_MIME_MULTIPART,
                MIME_MULTIPART_JAVATYPE,
                version);
        SOURCE_SOAPTYPE =
            new SOAPSimpleType(QNAME_TYPE_SOURCE, SOURCE_JAVATYPE, version);
        DATA_HANDLER_SOAPTYPE =
            new SOAPSimpleType(
                QNAME_TYPE_DATA_HANDLER,
                DATA_HANDLER_JAVATYPE,
                version);
    }

    public abstract void initializeTypeMap(Map typeMap);

    public void initializeJavaToXmlTypeMap(Map typeMap) {
        Map typeNameToDescription = new HashMap();
        initializeTypeMap(typeNameToDescription);

        for (Iterator eachClassName = typeNameToDescription.keySet().iterator();
            eachClassName.hasNext();
            ) {
            String className = (String) eachClassName.next();
            SOAPType typeDescription =
                (SOAPType) typeNameToDescription.get(className);

            Class javaType = classForName(className);
            if (javaType != null) {
                typeMap.put(javaType, typeDescription.getName());
            }
        }
    }

    public static Class classForName(String name) {
        Class javaType = null;
        try {
            javaType = Class.forName(name);
        } catch (Exception e) {
        }
        if (javaType == null) {
            if ("boolean".equals(name))
                return boolean.class;
            if ("byte".equals(name))
                return byte.class;
            if ("short".equals(name))
                return short.class;
            if ("int".equals(name))
                return int.class;
            if ("long".equals(name))
                return long.class;
            if ("char".equals(name))
                return char.class;
            if ("float".equals(name))
                return float.class;
            if ("double".equals(name))
                return double.class;
        }
        return javaType;
    }
}
