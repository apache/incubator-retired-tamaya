package org.apache.tamaya.events.tests;

import org.apache.tamaya.events.folderobserver.ObservingPropertySourceProvider;
import org.apache.tamaya.format.formats.PropertiesFormat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

/**
 * Created by Anatole on 25.02.2015.
 */
public class TestObservingProvider extends ObservingPropertySourceProvider{

    public TestObservingProvider(){
        super(Paths.get("C:\\Users\\Anatole\\IdeaProjects\\incubator-tamaya\\modules\\events/src/test/data"), new PropertiesFormat());
    }
}
