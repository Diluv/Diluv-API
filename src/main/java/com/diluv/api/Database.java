package com.diluv.api;

import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.SecurityDatabase;
import com.diluv.confluencia.database.UserDatabase;

public class Database {

    public final GameDatabase game = new GameDatabase();
    public final ProjectDatabase project = new ProjectDatabase();
    public final FileDatabase file = new FileDatabase();
    public final UserDatabase user = new UserDatabase();
    public final SecurityDatabase security = new SecurityDatabase();
    public final NewsDatabase news = new NewsDatabase();

    public void init (String host, String user, String password) {

        Confluencia.init(host, user, password);
    }
}
