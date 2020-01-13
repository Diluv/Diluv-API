package com.diluv.api.database;

import com.diluv.api.database.dao.EmailDAO;
import com.diluv.api.utils.FileReader;

public class EmailTestDatabase implements EmailDAO {

    private final String[] emailRecords;
    private final String[] domainRecords;

    public EmailTestDatabase () {

        this.emailRecords = FileReader.readJsonFile("blacklist/emails", String[].class);
        this.domainRecords = FileReader.readJsonFile("blacklist/domains", String[].class);
    }

    @Override
    public boolean existsBlacklist (String email, String domain) {

        for (String emailRecord : this.emailRecords) {
            if (email.equalsIgnoreCase(emailRecord)) {
                return true;
            }
        }

        for (String domainRecord : this.domainRecords) {
            if (domain.equalsIgnoreCase(domainRecord)) {
                return true;
            }
        }
        return false;
    }
}
