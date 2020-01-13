package com.diluv.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.diluv.api.DiluvAPI;
import com.diluv.api.database.dao.EmailDAO;
import com.diluv.api.utils.SQLHandler;

public class EmailDatabase implements EmailDAO {
    private static final String EXISTS_BLACKLIST = SQLHandler.readFile("email/existsBlacklist");

    @Override
    public boolean existsBlacklist (String email, String domain) {

        try (PreparedStatement stmt = DiluvAPI.connection().prepareStatement(EXISTS_BLACKLIST)) {
            stmt.setString(1, email);
            stmt.setString(2, domain);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e) {
        	DiluvAPI.LOGGER.throwing(EmailDatabase.class.getName(), "existsBlacklist (String email, String domain)", e);
        }
        return false;
    }
}
