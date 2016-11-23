/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

package com.griddynamics.jagger.providers;

import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.providers.creators.ObjectCreator;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Reads information from CSV files
 * @author Nikolay Musienko
 * @n
 * @par Details:
 * @details Reads data from CSV files and translates it to java objects.
 *
 * @ingroup Main_Providers_group */
public class CsvProvider<T> implements Iterable<T>, Serializable  {

    private String path;
    private CSVStrategy strategy = CSVStrategy.DEFAULT_STRATEGY;
    private boolean readHeader;
    private ObjectCreator<T> objectCreator;

    /** Returns file name
     * @author Nikolay Musienko
     * @return file name*/
    public String getPath() {
        return path;
    }

    /** Sets file name, which contains data
     * @author Nikolay Musienko
     * @n
     * @param path - full name of file */
    public void setPath(String path) {
        this.path = path;
    }

    /** Returns object creator, which translates row data to java objects
     * @author Nikolay Musienko
     * @return object creator*/
    public ObjectCreator<T> getObjectCreator() {
        return objectCreator;
    }

    /** Sets object creator
     * @author Nikolay Musienko
     * @n
     * @param objectCreator - translate data to java objects */
    public void setObjectCreator(ObjectCreator<T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    /** Returns csv strategy, which describes how to read file
     * @author Nikolay Musienko
     * @return csv strategy*/
    public CSVStrategy getStrategy() {
        return strategy;
    }

    /** Sets CSV strategy
     * @author Nikolay Musienko
     * @n
     * @param strategy - apache CSV strategy, which say how to read data from CSV file */
    public void setStrategy(CSVStrategy strategy) {
        this.strategy = strategy;
    }

    /** Creates csv provider, which reads from specified file
     * @author Nikolay Musienko
     * @n
     * @param path - file name */
    public CsvProvider(String path) {
        this.path = path;
    }

    /** Returns true if file contains header
     * @author Nikolay Musienko
     * @return true if file contains header*/
    public boolean getReadHeader() {
        return readHeader;
    }

    /** Set true if file contains header
     * @author Nikolay Musienko
     * @n
     * @param readHeader - true if file contains header */
    public void setReadHeader(boolean readHeader) {
        this.readHeader = readHeader;
    }

    /** Creates csv provider
     * @author Nikolay Musienko */
    public CsvProvider() {
    }

    /** Returns iterator over created objects.
     * @author Nikolay Musienko
     * @n
     * @par Details:
     * @details Reads data from file, translates it to java objects, return iterator over this objects */
    public Iterator<T> iterator() {

        return new AbstractIterator<T>() {

            private CSVParser parser;

            {
                init();
            }

            private void init(){
                if (path == null) {
                    throw new TechnicalException("File path can't be NULL!");
                }
                try {
                    parser = new CSVParser(new BufferedReader(new FileReader(new File(path))), strategy);
                } catch (FileNotFoundException e) {
                    throw Throwables.propagate(e);
                }  if(readHeader) {
                    try {
                        objectCreator.setHeader(parser.getLine());
                    } catch (IOException e){
                        throw Throwables.propagate(e);
                    }
                }

            }

            @Override
            protected T computeNext() {
                try {
                    String[] strings = parser.getLine();
                    if(strings == null) {
                        return endOfData();
                    }
                    return objectCreator.createObject(strings);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }

        };
    }
}


/* **************** Providers page *************************  */
/// @defgroup Main_Providers_General_group Providers main page
///
/// @li Available implementations: @ref Main_Providers_group
/// @li How to customize: @ref Main_HowToCustomizeProviders_group
/// @n
/// @n
/// @details
/// @par General info
/// Provides are providing list of endpoints and queries to distributors. @n
/// Endpoint - target for the test (address of System Under Test). @n
/// Query - request for System Under Test. @n
/// Providers are executed single time before start of the tests @n
///
/// @par Example of providers setup in XML:
/// Below is example of XML @xlink{test-description} section @n
/// In current example endpoints are hardcoded and list of queries is empty. @n
/// In real life it is more complicated. Endpoints and queries are usually provided as CVS files, property files or as some SUT test service @n
/// In this case you need to implement custom provider(s). How to do this you can read here: @ref Main_HowToCustomizeProviders_group
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - simple providers usage
/// @until end: following section is used for docu generation - simple providers usage
///
/// @par Variants of proviers available in XML:
/// @xlink{endpoint-provider} - documentation of endpoint provider element in XML schema @n
/// @xlink{query-provider} - documentation of query provider element in XML schema @n
/// @xlink_complex{endpointProviderAbstract} - types of endpoint providers available in XML schema. See <b> 'Sub Types' </b> section of man page @n
/// @xlink_complex{queryProviderAbstract} - types of query providers available in XML schema. See <b> 'Sub Types' </b> section of man page @n
/// How providers mentioned above are implemented you can see in section: @ref Main_Providers_group @n
/// @n


/* **************** How to customize provider ************************* */
/// @defgroup Main_HowToCustomizeProviders_group Custom providers
///
/// @details
/// @ref Main_Providers_General_group
/// @n
/// @n
/// To add custom provider you need to do -
/// 1. Create class which implements interface Iterable<Q>
/// @dontinclude  FileReaderIterable.java
/// @skipline  public class FileReaderIterable
/// @n
///
/// 2. Create bean in XML file with some id
/// @dontinclude  fileprovider.conf.xml
/// @skip begin: following section is used for docu generation - provider usage
/// @until end: following section is used for docu generation - provider usage
/// @n
///
/// 3. Create component @xlink{query-provider}(or @xlink{endpoint-provider}) with type query-provider-ref and set id of provider to attribute ref.
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - provider custom
/// @until end: following section is used for docu generation - provider custom
/// @n
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package
