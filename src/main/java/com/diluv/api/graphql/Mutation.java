package com.diluv.api.graphql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.Part;

import com.diluv.confluencia.database.record.GameDefaultProjectTypeEntity;

import org.apache.commons.io.IOUtils;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.GamesEntity;
import com.diluv.confluencia.database.record.ProjectTypesEntity;
import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLMutationResolver;

public class Mutation implements GraphQLMutationResolver {

    public Game addGame (String slug, String name, String url, Part logoPNG, Part logoWebp, String projectTypeSlug, String projectTypeName) {

        if (Confluencia.GAME.findOneBySlug(slug) != null) {
            throw new GraphQLException("Game already exists");
        }
        File logoPath = new File(Constants.CDN_FOLDER, "games/" + slug);
        logoPath.mkdirs();
        try {
            try (FileOutputStream fosPNG = new FileOutputStream(new File(logoPath, "logo.png"))) {
                IOUtils.copy(logoPNG.getInputStream(), fosPNG);
            }
            try (FileOutputStream fosWebp = new FileOutputStream(new File(logoPath, "logo.webp"))) {
                IOUtils.copy(logoWebp.getInputStream(), fosWebp);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new GraphQLException("Failed to create game logo's");
        }

        GamesEntity game = new GamesEntity();
        game.setSlug(slug);
        game.setName(name);
        game.setUrl(url);

        ProjectTypesEntity projectType = new ProjectTypesEntity();
        projectType.setGame(game);
        projectType.setSlug(projectTypeSlug);
        projectType.setName(projectTypeName);

        if (!Confluencia.GAME.insertGame(game)) {
            throw new GraphQLException("Failed to insert game");
        }

        if (!Confluencia.GAME.insertProjectType(projectType)) {
            throw new GraphQLException("Failed to insert project type");
        }

        if (!Confluencia.GAME.insertDefaultProjectType(new GameDefaultProjectTypeEntity(game, projectTypeSlug))) {
            throw new GraphQLException("Failed to insert project type");
        }

        return new Game(game);
    }
}