package com.readinglength.archivistws;

import com.readinglength.archivistws.controller.Bookshelf;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ArchivistModule.class)
public interface ArchivistComponent {
    Bookshelf bookshelf();
}
