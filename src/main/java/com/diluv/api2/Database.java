package com.diluv.api2;

import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.EmailDatabase;
import com.diluv.confluencia.database.FileDatabase;
import com.diluv.confluencia.database.GameDatabase;
import com.diluv.confluencia.database.NewsDatabase;
import com.diluv.confluencia.database.ProjectDatabase;
import com.diluv.confluencia.database.UserDatabase;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.dao.FileDAO;
import com.diluv.confluencia.database.dao.GameDAO;
import com.diluv.confluencia.database.dao.NewsDAO;
import com.diluv.confluencia.database.dao.ProjectDAO;
import com.diluv.confluencia.database.dao.UserDAO;

public class Database {
    
    public final GameDAO gameDAO = new GameDatabase();
    public final ProjectDAO projectDAO = new ProjectDatabase();
    public final FileDAO fileDAO = new FileDatabase();
    public final UserDAO userDAO = new UserDatabase();
    public final EmailDAO emailDAO = new EmailDatabase();
    public final NewsDAO newsDAO = new NewsDatabase();
    
    public void init(String host, String user, String password, boolean deleteAll) {
        
        Confluencia.init(host, user, password, deleteAll);
    }
}