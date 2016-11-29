/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * User: dkotlyarov
 */
public class ReportInputStream extends InputStream {
    private final ByteArrayInputStream xmlInput;
    private final boolean removeFrame;

    public ReportInputStream(InputStream input, boolean removeFrame) {
        this.removeFrame = removeFrame;

        try {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder parser = factory.newDocumentBuilder();
                Document document = parser.parse(input);
                processNode(document);

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream(65536);
                try {
                    transformer.transform(new DOMSource(document), new StreamResult(xmlOutput));
                } finally {
                    xmlOutput.close();
                }

                this.xmlInput = new ByteArrayInputStream(xmlOutput.toByteArray());
            } finally {
                input.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read() throws IOException {
        return xmlInput.read();
    }

    @Override
    public void close() throws IOException {
        xmlInput.close();
    }

    private void processNode(Node node) {
        String nodeName = node.getNodeName();

        if (!removeFrame && (node instanceof Element) && "jasperReport".equals(nodeName)) {
            Element element = (Element) node;
            if (element.hasAttribute("pageHeight")) {
                element.setAttribute("pageHeight", "100000000");
            }
        }

        if ((node instanceof Element) || (node instanceof Document)) {
            {
                Node[] childNodes = new Node[node.getChildNodes().getLength()];
                Node childNode = node.getFirstChild();
                for (int i = 0, ci = childNodes.length; i < ci; ++i, childNode = childNode.getNextSibling()) {
                    childNodes[i] = childNode;
                }

                for (Node childNode1 : childNodes) {
                    processNode(childNode1);
                }
            }

            // remove frame tag
            if ("frame".equals(nodeName)) {
                Node[] childNodes = new Node[node.getChildNodes().getLength()];
                Node childNode = node.getFirstChild();
                for (int i = 0, ci = childNodes.length; i < ci; ++i, childNode = childNode.getNextSibling()) {
                    childNodes[i] = childNode;
                }

                Node parentNode = node.getParentNode();
                if (removeFrame) {
                    parentNode.removeChild(node);
                }
                for (Node childNode1 : childNodes) {
                    if ((childNode1 instanceof Element) && !"reportElement".equals(childNode1.getNodeName())) {
                        if (removeFrame) {
                            node.removeChild(childNode1);
                            parentNode.appendChild(childNode1);
                        } else {
                            processY((Element) childNode1);
                        }
                    }
                }
            }
        }
    }

    private void processY(Element element) {
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if ((childNode instanceof Element) && "reportElement".equals(childNode.getNodeName())) {
                Element childElement = (Element) childNode;
//                String y = childElement.getAttribute("y");
//                childElement.setAttribute("y", "0");
            }
        }
    }
}
