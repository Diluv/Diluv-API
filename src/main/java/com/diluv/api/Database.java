package com.diluv.api;

import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.SecurityDatabase;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.UserDatabase;
import com.diluv.confluencia.database.dao.SecurityDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;

public class Database {

    public final GameDAO gameDAO;
    public final ProjectDAO projectDAO;
    public final FileDAO fileDAO;
    public final UserDAO userDAO;
    public final SecurityDAO securityDAO;
    public final NewsDAO newsDAO;

    public Database () {

        this.gameDAO = new GameDatabase();
        this.projectDAO = new ProjectDatabase();
        this.fileDAO = new FileDatabase();
        this.userDAO = new UserDatabase();
        this.securityDAO = new SecurityDatabase();
        this.newsDAO = new NewsDatabase();
    }

    public Database (GameDAO gameDAO, ProjectDAO projectDAO, FileDAO fileDAO, UserDAO userDAO, SecurityDAO emailDAO, NewsDAO newsDAO) {

        this.gameDAO = gameDAO;
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.userDAO = userDAO;
        this.securityDAO = emailDAO;
        this.newsDAO = newsDAO;
    }

    public void init (String host, String user, String password, boolean deleteAll) {

        Confluencia.init(host, user, password, deleteAll);
    }
}
