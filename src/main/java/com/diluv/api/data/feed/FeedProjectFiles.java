package com.diluv.api.data.feed;

import com.diluv.confluencia.database.record.ProjectFilesEntity;

import org.jboss.resteasy.plugins.providers.atom.*;

import java.net.URI;
import java.util.Date;

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
        entry.setTitle(file.getDisplayName());
        entry.setUpdated(Date.from(file.getUpdatedAt()));
        entry.setContent(content);
        entry.getAuthors().add(new Person(file.getUser().getDisplayName()));
        this.getEntries().add(entry);
    }
}
