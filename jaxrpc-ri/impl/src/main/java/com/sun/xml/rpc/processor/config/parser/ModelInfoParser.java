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

package com.sun.xml.rpc.processor.config.parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.streaming.SOAP12NamespaceConstants;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ModelInfoParser {
    
    private ProcessorEnvironment env;
    private LocalizableMessageFactory messageFactory =
        new LocalizableMessageFactory(
            "com.sun.xml.rpc.resources.configuration");
    
    // TODO this must be fixed to handle SOAP1.2 as well
    //private static SOAPNamespaceConstants soapNamespaceConstants =
    //SOAPConstantsFactory.getSOAPNamespaceConstants(SOAPVersion.SOAP_11);
    public ModelInfoParser(ProcessorEnvironment env) {
        this.env = env;
    }
    
    public abstract ModelInfo parse(XMLReader reader);
    
    protected TypeMappingRegistryInfo parseTypeMappingRegistryInfo(
        XMLReader reader) {
            
        TypeMappingRegistryInfo typeMappingRegistryInfo =
            new TypeMappingRegistryInfo();
        
        boolean readyForImport = true;
        boolean gotAdditionalTypes = false;
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_IMPORT)) {
                if (!readyForImport) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                }
                readyForImport = false;
                while (reader.nextElementContent() != XMLReader.END) {
                    if (reader.getName().equals(Constants.QNAME_SCHEMA)) {
                        String namespace =
                            ParserUtil.getMandatoryNonEmptyAttribute(reader,
                                Constants.ATTR_NAMESPACE);
                        String location =
                            ParserUtil.getMandatoryNonEmptyAttribute(reader,
                                Constants.ATTR_LOCATION);
                        ImportedDocumentInfo docInfo =
                            new ImportedDocumentInfo(
                                ImportedDocumentInfo.SCHEMA_DOCUMENT);
                        docInfo.setNamespace(namespace);
                        docInfo.setLocation(location);
                        typeMappingRegistryInfo.addImportedDocument(docInfo);
                        ParserUtil.ensureNoContent(reader);
                    } else {
                        ParserUtil.failWithLocalName(
                            "configuration.invalidElement", reader);
                    }
                }
            } else if (reader.getName().equals(Constants.QNAME_TYPE_MAPPING)) {
                if (gotAdditionalTypes) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                }
                parseTypeMapping(typeMappingRegistryInfo, reader);
            } else if (reader.getName().equals(
                Constants.QNAME_ADDITIONAL_TYPES)) {
                    
                if (gotAdditionalTypes) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                }
                while (reader.nextElementContent() != XMLReader.END) {
                    if (reader.getName().equals(Constants.QNAME_CLASS)) {
                        String name = ParserUtil.getMandatoryNonEmptyAttribute(
                            reader, Constants.ATTR_NAME);
                        typeMappingRegistryInfo.addExtraTypeName(name);
                        ParserUtil.ensureNoContent(reader);
                    } else {
                        ParserUtil.failWithLocalName(
                            "configuration.invalidElement", reader);
                    }
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return typeMappingRegistryInfo;
    }
    
    private void parseTypeMapping(
        TypeMappingRegistryInfo typeMappingRegistryInfo, XMLReader reader) {
        
        String encodingStyle = ParserUtil.getMandatoryAttribute(reader,
            Constants.ATTR_ENCODING_STYLE);
        
        if (!knownEncodingStyles.contains(encodingStyle)) {
            warn("configuration.typemapping.unrecognized.encodingstyle",
                encodingStyle);
        }
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_ENTRY)) {
                parseEntry(typeMappingRegistryInfo, encodingStyle, reader);
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
    }
    
    private void parseEntry(TypeMappingRegistryInfo typeMappingRegistryInfo,
        String encodingStyle, XMLReader reader) {
        
        String rawSchemaType = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_SCHEMA_TYPE);
        String javaTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_JAVA_TYPE);
        String serializerFactoryName =
            ParserUtil.getMandatoryNonEmptyAttribute(reader,
                Constants.ATTR_SERIALIZER_FACTORY);
        String deserializerFactoryName =
            ParserUtil.getMandatoryNonEmptyAttribute(reader,
                Constants.ATTR_DESERIALIZER_FACTORY);
        
        ParserUtil.ensureNoContent(reader);
        
        String prefix = XmlUtil.getPrefix(rawSchemaType);
        String uri = (prefix == null ? null : reader.getURI(prefix));
        if (prefix != null && uri == null) {
            ParserUtil.failWithLocalName(
                "configuration.configuration.invalid.attribute.value",
                reader, rawSchemaType);
        }
        
        String localPart = XmlUtil.getLocalPart(rawSchemaType);
        QName xmlType = new QName(uri, localPart);
        
        TypeMappingInfo i = new TypeMappingInfo(encodingStyle,
            xmlType, javaTypeName,
            serializerFactoryName,
            deserializerFactoryName);
        typeMappingRegistryInfo.addMapping(i);
    }
    
    protected HandlerChainInfoData parseHandlerChainInfoData(XMLReader reader) {
        HandlerChainInfoData data = new HandlerChainInfoData();
        
        boolean gotClient = false;
        boolean gotServer = false;
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_CHAIN)) {
                String runatAttr = ParserUtil.getMandatoryNonEmptyAttribute(
                    reader, Constants.ATTR_RUN_AT);
                if (runatAttr.equals(Constants.ATTRVALUE_CLIENT)) {
                    if (gotClient) {
                        ParserUtil.failWithLocalName(
                            "configuration.handlerChain.duplicate",
                            reader, runatAttr);
                    } else {
                        data.setClientHandlerChainInfo(
                            parseHandlerChainInfo(reader));
                        gotClient = true;
                    }
                } else if (runatAttr.equals(Constants.ATTRVALUE_SERVER)) {
                    if (gotServer) {
                        ParserUtil.failWithLocalName(
                            "configuration.handlerChain.duplicate",
                            reader, runatAttr);
                    } else {
                        data.setServerHandlerChainInfo(
                            parseHandlerChainInfo(reader));
                        gotServer = true;
                    }
                } else {
                    ParserUtil.failWithLocalName(
                        "configuration.invalidAttributeValue",
                        reader, Constants.ATTR_RUN_AT);
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return data;
    }
    
    protected HandlerChainInfo parseHandlerChainInfo(XMLReader reader) {
        HandlerChainInfo chain = new HandlerChainInfo();
        // TODO is this sufficient to handle both versions of SOAP
        //chain.addRole(soapNamespaceConstants.getActorNext());
        chain.addRole(SOAPNamespaceConstants.ACTOR_NEXT);
        //chain.addRole(SOAP12NamespaceConstants.ACTOR_NEXT);
        
        String rolesAttr =
            ParserUtil.getAttribute(reader, Constants.ATTR_ROLES);
        if (rolesAttr != null) {
            List rolesList = XmlUtil.parseTokenList(rolesAttr);
            for (Iterator iter = rolesList.iterator(); iter.hasNext();) {
                chain.addRole((String) iter.next());
            }
        }
        
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_HANDLER)) {
                chain.add(parseHandlerInfo(reader));
            }
            else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return chain;
    }
    
    protected HandlerInfo parseHandlerInfo(XMLReader reader) {
        HandlerInfo handler = new HandlerInfo();
        
        String className = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_CLASS_NAME);
        handler.setHandlerClassName(className);
        String headers =
            ParserUtil.getAttribute(reader, Constants.ATTR_HEADERS);
        if (headers != null) {
            List headersList = XmlUtil.parseTokenList(headers);
            for (Iterator iter = headersList.iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String prefix = XmlUtil.getPrefix(name);
                String localPart = XmlUtil.getLocalPart(name);
                if (prefix == null) {
                    
                    // use the default namespace
                    prefix = "";
                }
                String uri = reader.getURI(prefix);
                if (uri == null) {
                    ParserUtil.failWithLocalName(
                        "configuration.invalidAttributeValue",
                        reader, Constants.ATTR_HEADERS);
                }
                handler.addHeaderName(new QName(uri, localPart));
            }
        }
        
        Map properties = handler.getProperties();
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_PROPERTY)) {
                String name = ParserUtil.getMandatoryNonEmptyAttribute(reader,
                    Constants.ATTR_NAME);
                String value = ParserUtil.getMandatoryAttribute(reader,
                    Constants.ATTR_VALUE);
                properties.put(name, value);
                ParserUtil.ensureNoContent(reader);
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return handler;
    }
    
    protected NamespaceMappingRegistryInfo parseNamespaceMappingRegistryInfo(
        XMLReader reader) {
            
        NamespaceMappingRegistryInfo namespaceMappingRegistryInfo =
            new NamespaceMappingRegistryInfo();
        
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_NAMESPACE_MAPPING)) {
                parseNamespaceMapping(namespaceMappingRegistryInfo, reader);
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return namespaceMappingRegistryInfo;
    }
    
    private void parseNamespaceMapping(
        NamespaceMappingRegistryInfo namespaceMappingRegistryInfo,
        XMLReader reader) {
        
        String namespaceURI = ParserUtil.getMandatoryAttribute(reader,
            Constants.ATTR_NAMESPACE);
        String javaPackageName = ParserUtil.getMandatoryAttribute(reader,
            Constants.ATTR_PACKAGE_NAME);
        
        ParserUtil.ensureNoContent(reader);
        
        NamespaceMappingInfo i =
            new NamespaceMappingInfo(namespaceURI, javaPackageName);
        namespaceMappingRegistryInfo.addMapping(i);
    }
    
    protected ProcessorEnvironment getEnvironment() {
        return env;
    }
    
    protected void warn(String key) {
        getEnvironment().warn(messageFactory.getMessage(key));
    }
    
    protected void warn(String key, String arg) {
        getEnvironment().warn(messageFactory.getMessage(key, arg));
    }
    
    protected void warn(String key, Object[] args) {
        getEnvironment().warn(messageFactory.getMessage(key, args));
    }
    
    protected void info(String key) {
        getEnvironment().info(messageFactory.getMessage(key));
    }
    
    protected void info(String key, String arg) {
        getEnvironment().info(messageFactory.getMessage(key, arg));
    }
    
    // These are the set of encodingSytles that are commonly used for pluggable serialzers
    // any encodingStyle specified for a pluggable serializer that is not in this
    // set will result in a warning.
    private static final HashSet knownEncodingStyles = new HashSet();
    
    static {
        knownEncodingStyles.add(SOAPNamespaceConstants.ENCODING);
        knownEncodingStyles.add(SOAP12NamespaceConstants.ENCODING);
        knownEncodingStyles.add("");
    }
}
