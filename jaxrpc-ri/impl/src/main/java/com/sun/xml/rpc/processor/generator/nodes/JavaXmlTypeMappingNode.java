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

package com.sun.xml.rpc.processor.generator.nodes;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralContentMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;

/**
 * @author  Qingqing Ouyang
 * @version 1.1.5
 */
public class JavaXmlTypeMappingNode extends JaxRpcMappingNode {

    /**
     * Default constructor.
     */
    public JavaXmlTypeMappingNode() {
    }

    /**
     * write the appropriate information to a DOM tree and return it
     *
     * @param parent node in the DOM tree 
     * @param nodeName name for the root element for this DOM tree fragment
     * @param type java type
     * @return the DOM tree top node
     */
    public Node write(
        Node parent,
        String nodeName,
        AbstractType type,
        Configuration config,
        boolean isSimpleType)
        throws Exception {

        if (isSimpleType) {
            return writeSimpleType(parent, nodeName, type);
        } else {
            return writeComplexType(parent, nodeName, type, config);
        }
    }

    private Node writeComplexType(
        Node parent,
        String nodeName,
        AbstractType type,
        Configuration config) {

        Element node = null;

        if (type instanceof RPCRequestOrderedStructureType
            || type instanceof RPCRequestUnorderedStructureType
            || type instanceof RPCResponseStructureType
            || (type instanceof LiteralStructuredType
                    &&((LiteralStructuredType)type).isRpcWrapper())) {
            // _RequestStruct, _ResponseStruct types are not written to mapping            
            return node;
        }     

        debug(MYNAME, "ComplexType = " + type.getClass().getName());

        if (type instanceof SOAPStructureType) {

            node = appendChild(parent, nodeName);

            SOAPStructureType soapStructType = (SOAPStructureType) type;
            QName qname = soapStructType.getName();
            String namespaceURI = qname.getNamespaceURI();

            //java-type
            appendTextChild(
                node,
                JaxRpcMappingTagNames.JAVA_TYPE,
                soapStructType.getJavaType().getName());

            //root-type-qname
            //XXX FIXME  Need to handle QName better
            Element rootTypeNode =
                (Element) appendTextChild(node,
                    JaxRpcMappingTagNames.ROOT_TYPE_QNAME,
                    "typeNS:" + qname.getLocalPart());
            rootTypeNode.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:typeNS",
                namespaceURI);

            //qname-scope
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.COMPLEX_TYPE);

            /* write members of structured type */
            for (Iterator iter = soapStructType.getMembers();
                iter.hasNext();
                ) {
                Object o = iter.next();
                if (o != null) {
                    debug(
                        MYNAME,
                        "SOAPStructureMemberType = " + o.getClass().getName());
                } else {
                    debug(MYNAME, "SOAPStructureMemberType == NULL");
                }

                SOAPStructureMember member = (SOAPStructureMember) o;

                String memberName = member.getName().getLocalPart();
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                String javaMemberName = javaMember.getName();

                //variable-mapping*
                Node variableMappingNode =
                    appendChild(node, JaxRpcMappingTagNames.VARIABLE_MAPPING);

                //java-variable-name
                appendTextChild(
                    variableMappingNode,
                    JaxRpcMappingTagNames.JAVA_VARIABLE_NAME,
                    javaMemberName);

                //data-member?
                if (javaMember.isPublic()) {
                    forceAppendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.DATA_MEMBER,
                        null);
                }

                //xml-element-name
                appendTextChild(
                    variableMappingNode,
                    JaxRpcMappingTagNames.XML_ELEMENT_NAME,
                    memberName);
            }
        } else if (type instanceof LiteralStructuredType) {

            debug(
                MYNAME,
                "TYPE = "
                    + type.getName()
                    + "; ANONY = "
                    + (String) type.getProperty(
                        ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME));

            node = appendChild(parent, nodeName);

            LiteralStructuredType litStructType = (LiteralStructuredType) type;
            QName qname = litStructType.getName();
            String namespaceURI = qname.getNamespaceURI();

            //java-type
            appendTextChild(
                node,
                JaxRpcMappingTagNames.JAVA_TYPE,
                litStructType.getJavaType().getName());

            if (litStructType
                .getProperty(ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME)
                == null) {

                //root-type-qname
                if (namespaceURI == null || "".equals(namespaceURI)) {
                    //This is purely a workaround since jax-rpc does not populate
                    //the namespace for LiteralStructuredType when it is soap 1.1
                    if (config.getModelInfo() instanceof RmiModelInfo) {
                        namespaceURI =
                            ((RmiModelInfo) config.getModelInfo())
                                .getTypeNamespaceURI();
                    }
                }

                Element rootTypeNode =
                    (Element) appendTextChild(node,
                        JaxRpcMappingTagNames.ROOT_TYPE_QNAME,
                        "typeNS:" + qname.getLocalPart());
                rootTypeNode.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:typeNS",
                    namespaceURI);
            } else { //anonymous complex type

                //anonymous-type-qname
                String name =
                    namespaceURI
                        + ":"
                        + (String) litStructType.getProperty(
                            ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME);
                Element anonymousTypeNode =
                    (Element) appendTextChild(node,
                        JaxRpcMappingTagNames.ANONYMOUS_TYPE_QNAME,
                        name);
            }

            //qname-scope
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.COMPLEX_TYPE);
            //QNameScopeTypes.ELEMENT);

            JavaStructureType t =
                (JavaStructureType) litStructType.getJavaType();

            /* write members of structured type */
            //variable-mapping*
            for (Iterator iter = t.getMembers(); iter.hasNext();) {

                Object o = ((JavaStructureMember) iter.next()).getOwner();
                if (o != null) {
                    debug(
                        MYNAME,
                        "LiteralElementMemberType = " + o.getClass().getName());
                } else {
                    debug(MYNAME, "LiteralElementMemberType == NULL");
                }

                if (o instanceof LiteralWildcardMember) {

                    debug(MYNAME, "LiteralWildcardMember = " + o);

                    LiteralWildcardMember member = (LiteralWildcardMember) o;

                    String memberName = member.getExcludedNamespaceName();

                    JavaStructureMember javaMember =
                        member.getJavaStructureMember();
                    String javaMemberName = javaMember.getName();

                    Node variableMappingNode =
                        appendChild(
                            node,
                            JaxRpcMappingTagNames.VARIABLE_MAPPING);

                    //java-variable-name
                    appendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.JAVA_VARIABLE_NAME,
                        javaMemberName);

                    //data-member?
                    if (javaMember.isPublic()) {
                        forceAppendTextChild(
                            variableMappingNode,
                            JaxRpcMappingTagNames.DATA_MEMBER,
                            null);
                    }

                    //xml-wildcard
                    forceAppendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.XML_WILDCARD,
                        null);

                } else if (o instanceof LiteralElementMember) {
                    LiteralElementMember member = (LiteralElementMember) o;

                    String memberName = member.getName().getLocalPart();
                    JavaStructureMember javaMember =
                        member.getJavaStructureMember();
                    String javaMemberName = javaMember.getName();

                    Node variableMappingNode =
                        appendChild(
                            node,
                            JaxRpcMappingTagNames.VARIABLE_MAPPING);

                    //java-variable-name
                    appendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.JAVA_VARIABLE_NAME,
                        javaMemberName);

                    //data-member?
                    if (javaMember.isPublic()) {
                        forceAppendTextChild(
                            variableMappingNode,
                            JaxRpcMappingTagNames.DATA_MEMBER,
                            null);
                    }

                    //xml-element-name
                    appendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.XML_ELEMENT_NAME,
                        memberName);
                } else if (o instanceof LiteralAttributeMember) {
                    LiteralAttributeMember member = (LiteralAttributeMember) o;

                    String memberName = member.getName().getLocalPart();
                    JavaStructureMember javaMember =
                        member.getJavaStructureMember();
                    String javaMemberName = javaMember.getName();

                    Node variableMappingNode =
                        appendChild(
                            node,
                            JaxRpcMappingTagNames.VARIABLE_MAPPING);

                    //java-variable-name
                    appendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.JAVA_VARIABLE_NAME,
                        javaMemberName);

                    //data-member?
                    if (javaMember.isPublic()) {
                        forceAppendTextChild(
                            variableMappingNode,
                            JaxRpcMappingTagNames.DATA_MEMBER,
                            null);
                    }

                    //xml-attribute-name
                    appendTextChild(
                        variableMappingNode,
                        JaxRpcMappingTagNames.XML_ATTRIBUTE_NAME,
                        memberName);
                } else if (o instanceof LiteralContentMember) {
                    // bug fix: 4931493
                    // 109 does not provide for a way to customize the mapping
                    // for literalContentMember (complexType type with Simple content)
                    // System.err.println("NOT SUPPORTED TYPE = " + o.getClass().getName());
                } else {
                    System.err.println(
                        "NOT SUPPORTED TYPE = " + o.getClass().getName());
                }
            }
        }

        return null;
    }

    private Node writeSimpleType(
        Node parent,
        String nodeName,
        AbstractType type) {

        Element node = null;

        if (type instanceof SOAPEnumerationType) {

            node = appendChild(parent, nodeName);

            SOAPEnumerationType soapEnumType = (SOAPEnumerationType) type;
            QName qname = type.getName();
            String namespaceURI = qname.getNamespaceURI();

            //java-type
            appendTextChild(
                node,
                JaxRpcMappingTagNames.JAVA_TYPE,
                soapEnumType.getJavaType().getName());

            //root-type-qname
            //XXX FIXME  Need to handle QName better
            Element rootTypeNode =
                (Element) appendTextChild(node,
                    JaxRpcMappingTagNames.ROOT_TYPE_QNAME,
                    "typeNS:" + qname.getLocalPart());
            rootTypeNode.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:typeNS",
                namespaceURI);

            //qname-scope
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.SIMPLE_TYPE);
        } else if (type instanceof LiteralEnumerationType) {
            node = appendChild(parent, nodeName);

            LiteralEnumerationType litEnumType = (LiteralEnumerationType) type;
            QName qname = type.getName();
            String namespaceURI = qname.getNamespaceURI();

            //java-type
            appendTextChild(
                node,
                JaxRpcMappingTagNames.JAVA_TYPE,
                litEnumType.getJavaType().getName());

            if (litEnumType
                .getProperty(ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME)
                == null) {
                //root-type-qname
                //XXX FIXME  Need to handle QName better
                Element rootTypeNode =
                    (Element) appendTextChild(node,
                        JaxRpcMappingTagNames.ROOT_TYPE_QNAME,
                        "typeNS:" + qname.getLocalPart());
                rootTypeNode.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:typeNS",
                    namespaceURI);
            } else { //anonymous simple type
                //anonymous-type-qname
                String name =
                    namespaceURI
                        + ":"
                        + (String) litEnumType.getProperty(
                            ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME);
                Element anonymousTypeNode =
                    (Element) appendTextChild(node,
                        JaxRpcMappingTagNames.ANONYMOUS_TYPE_QNAME,
                        name);
            }

            //qname-scope
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.SIMPLE_TYPE);
        } else {
            //don't know what to do.  shouldn't be here.    
        }

        return null;
    }

    public Node writeAnonymousArrayType(
        Node parent,
        String nodeName,
        LiteralStructuredType litStructType,
        Configuration config,
        boolean isSimpleType) {

        Element node = appendChild(parent, nodeName);

        QName qname = litStructType.getName();
        String namespaceURI = qname.getNamespaceURI();

        //java-type
        String javaType =
            (String) litStructType.getProperty(
                ModelProperties.PROPERTY_ANONYMOUS_ARRAY_JAVA_TYPE);
        appendTextChild(node, JaxRpcMappingTagNames.JAVA_TYPE, javaType);

        //anonymous-type-qname
        String name =
            namespaceURI
                + ":"
                + (String) litStructType.getProperty(
                    ModelProperties.PROPERTY_ANONYMOUS_ARRAY_TYPE_NAME);
        Element anonymousTypeNode =
            (Element) appendTextChild(node,
                JaxRpcMappingTagNames.ANONYMOUS_TYPE_QNAME,
                name);

        //qname-scope
        if (isSimpleType) {
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.SIMPLE_TYPE);
        } else {
            appendTextChild(
                node,
                JaxRpcMappingTagNames.QNAME_SCOPE,
                QNameScopeTypes.COMPLEX_TYPE);
        }

        return null;
    }

    private final static String MYNAME = "JavaXmlTypeMappingNode";
}
