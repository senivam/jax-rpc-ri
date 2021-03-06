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

/**
*
* @author JAX-RPC Development Team
*/
package com.sun.xml.rpc.streaming;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Text;

import org.w3c.dom.Element;



public class XmlTreeReader extends XMLReaderBase {
    protected SOAPElement root;
    protected SOAPElement currentNode;
    protected int state;
    protected QName name;
    protected ElementIdStack elementIds;
    protected int elementId;
    protected String currentValue;
    protected AttributesAdapter attributes = new AttributesAdapter();

    public XmlTreeReader(SOAPElement root) {
        elementIds = new ElementIdStack();
        setRoot(root);
    }

    private void setRoot(SOAPElement root) {
        this.root = root;
        this.state = BOF;
    }

    public void close() {
        state = EOF;
    }

    public Attributes getAttributes() {
        attributes.initialize();
        return attributes;
    }

    public int getElementId() {
        return elementId;
    }

    // not supported
    public int getLineNumber() {
        return 0;
    }

    public String getLocalName() {
        return currentNode.getElementName().getLocalName();
    }

	//Narayana Murthy P: Added this method to return the currentNode 
    public Element getCurrentNode() {
		return currentNode;
	}

    public QName getName() {
        if (name == null) {
            name = new QName(getURI(), getLocalName());
        }
        return name;
    }

    public Iterator getPrefixes() {
        return currentNode.getVisibleNamespacePrefixes();
    }

    public int getState() {
        return state;
    }

    public String getURI() {

        return currentNode.getElementName().getURI();
    }

    public String getURI(String prefix) {
        return currentNode.getNamespaceURI(prefix);
    }

    public String getValue() {
        //TODO: remove once SAAJ fixes text splitting bug
        currentNode.normalize();
        return currentNode.getValue();
    }

    public int next() {
        if (state == EOF) {
            return EOF;
        }
        name = null;
        attributes.unintialize();

        parse(); // sets state, currentNode
        switch (state) {
            case START :
                elementId = elementIds.pushNext();
                break;
            case END :
                elementId = elementIds.pop();
                break;
            case CHARS :
            case EOF :
            case PI :
                break;
            default :
                throw new XMLReaderException(
                    "xmlreader.illegalStateEncountered",
                    Integer.toString(state));
        }
        return state;
    }

    public void parse() {

        switch (state) {
            case BOF :
                currentNode = root;
                state = START;
                break;

            case START :
                // SAAJ tree might contain multiple contiguous text nodes
                currentNode.normalize();
                org.w3c.dom.Node first = currentNode.getFirstChild();
                if (first != null) {
                    if (first instanceof Text) {
                        org.w3c.dom.Node sec = first.getNextSibling();
                        if (sec != null) {
                            // ignoring inter-element whitespace
                            currentNode = (SOAPElement) sec;
                            state = START;
                        } else {
                            state = CHARS;
                            currentValue = currentNode.getValue();
                            if (currentValue == null) {
                                state = END;
                            }
                        }
                    } else if (first instanceof SOAPElement) {
                        state = START;
                        currentNode = (SOAPElement) first;
                    } else {
                        throw new XMLReaderException(
                            "xmlreader.illegalType " + first.getClass());
                    }
                } else {
                    state = END;
                }
                break;

            case END :
                org.w3c.dom.Node nextNode = currentNode.getNextSibling();
                if (nextNode != null && nextNode instanceof Text) {
                    // ignoring inter-element whitespace
                    nextNode = nextNode.getNextSibling();
                }
                if (nextNode == null) {
                    // use root instead null so that it works with subtrees
                    if (currentNode == root) {
                        state = EOF;
                    } else {
                        state = END;
                        currentNode = currentNode.getParentElement();
                    }
                } else {
                    state = START;
                    currentNode = (SOAPElement) nextNode;
                }
                break;

            case EOF :
                break;
            case PI :
                break;
            case CHARS :
                state = END;
                break;
            default :
                throw new XMLReaderException(
                    "xmlreader.illegalStateEncountered",
                    Integer.toString(state));
        }
    }

    public XMLReader recordElement() {
        state = END;
        return new XmlTreeReader(currentNode);
    }

    public void skipElement(int elementId) {
        if (state == EOF)
            return;
        while (this.elementId != elementId) {
            currentNode = currentNode.getParentElement();
            this.elementId = elementIds.pop();
        }
        state = END;
    }

    public class AttributesAdapter implements Attributes {
        static final String XMLNS_NAMESPACE_URI =
            "http://www.w3.org/2000/xmlns/";

        private boolean initialized = false;
        ArrayList attributeUris = new ArrayList();
        ArrayList attributePrefixes = new ArrayList();
        ArrayList attributeNames = new ArrayList();
        ArrayList attributeValues = new ArrayList();
        ArrayList attributeQNames = new ArrayList();

        void unintialize() {
            if (initialized) {
                attributeUris.clear();
                attributePrefixes.clear();
                attributeNames.clear();
                attributeValues.clear();
                attributeQNames.clear();
                initialized = false;
            }
        }
        void initialize() {
            if (!initialized) {
                Iterator eachAttribute = currentNode.getAllAttributes();
                while (eachAttribute.hasNext()) {
                    Name name = (Name) eachAttribute.next();
                    attributeUris.add(name.getURI());
                    attributePrefixes.add(name.getPrefix());
                    attributeNames.add(name.getLocalName());
                    attributeValues.add(currentNode.getAttributeValue(name));
                    attributeQNames.add(null);
                }
                initialized = true;
            }
        }

        public int getLength() {
            initialize();
            return attributeValues.size();
        }

        public boolean isNamespaceDeclaration(int index) {
            initialize();
            return XMLNS_NAMESPACE_URI.equals(getURI(index));
        }

        public QName getName(int index) {
            initialize();
            if (attributeQNames.get(index) == null) {
                QName qname = new QName(getURI(index), getLocalName(index));
                attributeQNames.set(index, qname);
            }
            return (QName) attributeQNames.get(index);
        }

        public String getURI(int index) {
            initialize();
            return (String) attributeUris.get(index);
        }

        public String getLocalName(int index) {
            initialize();
            return (String) attributeNames.get(index);
        }

        public String getPrefix(int index) {
            initialize();
            String prefix = (String) attributePrefixes.get(index);
            // return null instead of "" to be compatible with XMLReaderImpl
            if (prefix != null && prefix.equals("")) {
                prefix = null;
            }
            return prefix;
        }

        public String getValue(int index) {
            initialize();
            return (String) attributeValues.get(index);
        }

        public int getIndex(QName name) {
            return getIndex(name.getNamespaceURI(), name.getLocalPart());
        }

        public int getIndex(String uri, String localName) {
            initialize();

            for (int index = 0; index < attributeNames.size(); ++index) {
                if (attributeUris.get(index).equals(uri)
                    && attributeNames.get(index).equals(localName)) {
                    return index;
                }
            }

            return -1;
        }

        public int getIndex(String localName) {
            initialize();

            for (int index = 0; index < attributeNames.size(); ++index) {
                if (attributeNames.get(index).equals(localName)) {
                    return index;
                }
            }

            return -1;
        }

        public String getValue(QName name) {
            int index = getIndex(name);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }

        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }

        public String getValue(String localName) {
            int index = getIndex(localName);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }
    }

}
