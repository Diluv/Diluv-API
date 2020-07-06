package com.diluv.api;

import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.SecurityDatabase;
import com.diluv.confluencia.database.UserDatabase;

public class Database {

    public final GameDatabase gameDAO = new GameDatabase();
    public final ProjectDatabase projectDAO = new ProjectDatabase();
    public final FileDatabase fileDAO = new FileDatabase();
    public final UserDatabase userDAO = new UserDatabase();
    public final SecurityDatabase securityDAO = new SecurityDatabase();
    public final NewsDatabase newsDAO = new NewsDatabase();

    public void init (String host, String user, String password) {

        Confluencia.init(host, user, password);
    }
}
