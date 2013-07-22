#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileReaderIterable implements Iterable<String> {

    private static Logger log= LoggerFactory.getLogger(FileReaderIterable.class);

    private String filePath;

    public FileReaderIterable(String filePath) {
        this.filePath=filePath;
    }

    public FileReaderIterable() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private List<String> strings = new ArrayList();
            private int index;

            {
                init();
            }

            public void init() {
                if(filePath==null){
                    return;
                }
                BufferedReader reader;
                try {
                    reader=new BufferedReader(new FileReader(new File(filePath)));
                } catch (FileNotFoundException e) {
                    log.warn("Exception during read file: {}",filePath);
                    return;
                }
                try {
                    String str;
                    while((str=reader.readLine())!=null){
                        strings.add(str);
                    }
                }
                catch (IOException e) {
                    log.warn("Exception during read file: {}",filePath);
                }
            }

            @Override
            public boolean hasNext() {
                if(index < strings.size()){
                    return true;
                }
                return false;
            }

            @Override
            public synchronized String next() {
                if(index>=strings.size()){
                    throw new IllegalArgumentException("Illegal index count");
                }
                return strings.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        };
    }
}