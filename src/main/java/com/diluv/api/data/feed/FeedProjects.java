package com.diluv.api.data.feed;

import com.diluv.confluencia.database.record.ProjectsEntity;

import org.jboss.resteasy.plugins.providers.atom.*;

import java.net.URI;
import java.util.Date;

public class FeedProjects extends Feed {
    private final String baseUrl;

    public FeedProjects (String baseUrl) {

        this.baseUrl = baseUrl;
        this.setId(URI.create(baseUrl + "/feed.atom"));
        this.getLinks().add(new Link("self", baseUrl));
        this.setUpdated(new Date());
        this.setTitle("Project Feed");
    }

    public void addProject (ProjectsEntity project) {

        Content content = new Content();
        content.setText(project.getSummary());
        Entry entry = new Entry();
        entry.setId(URI.create(baseUrl + "/" + project.getSlug()));
        entry.setTitle(project.getName());
        entry.setUpdated(Date.from(project.getUpdatedAt()));
        entry.setContent(content);

        //TODO Maybe show all authors?
        entry.getAuthors().add(new Person(project.getOwner().getDisplayName()));
        this.getEntries().add(entry);
    }
}
