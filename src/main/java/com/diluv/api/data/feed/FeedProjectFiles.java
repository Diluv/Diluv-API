package com.diluv.api.data.feed;

import java.net.URI;
import java.util.Date;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.diluv.confluencia.database.record.ProjectFilesEntity;

public class FeedProjectFiles extends Feed {
    private final String baseUrl;

    public FeedProjectFiles (String baseUrl, String projectName) {

        this.baseUrl = baseUrl;
        this.setId(URI.create(baseUrl + "/feed.atom"));
        this.getLinks().add(new Link("self", baseUrl + "/"));
        this.setUpdated(new Date());
        this.setTitle(projectName + " File Feed");
    }

    public void addFile (ProjectFilesEntity file) {

        Content content = new Content();
        content.setText(file.getChangelog());
        Entry entry = new Entry();
        entry.setId(URI.create(baseUrl + "/files/" + file.getId()));
        entry.setTitle(file.getName());
        entry.setUpdated(file.getUpdatedAt());
        entry.setContent(content);
        entry.getAuthors().add(new Person(file.getUser().getDisplayName()));
        this.getEntries().add(entry);
    }
}
