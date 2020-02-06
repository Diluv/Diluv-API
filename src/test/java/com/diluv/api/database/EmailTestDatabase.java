package com.diluv.api.database;

import java.util.List;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.EmailDAO;
import com.diluv.confluencia.database.record.EmailSendRecord;

public class EmailTestDatabase implements EmailDAO {

    private final String[] emailRecords;
    private final String[] domainRecords;
    private final List<EmailSendRecord> emailSentRecords;

    public EmailTestDatabase () {

        this.emailRecords = FileReader.readJsonFile("email/blacklistEmail", String[].class);
        this.domainRecords = FileReader.readJsonFile("email/blacklistDomain", String[].class);
        this.emailSentRecords = FileReader.readJsonFolder("email_sent", EmailSendRecord.class);
    }

    @Override
    public boolean insertDomainBlacklist (String[] domains) {

        for (String domain : domains) {
            for (String record : this.domainRecords) {
                if (domain.equalsIgnoreCase(record)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean existsBlacklist (String email, String domain) {

        for (String record : this.emailRecords) {
            if (email.equalsIgnoreCase(record)) {
                return true;
            }
        }

        for (String record : this.domainRecords) {
            if (domain.equalsIgnoreCase(record)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean insertEmailSent (String messageId, String email, String type) {

        return true;
    }

    @Override
    public boolean existsEmailSent (String messageId) {

        for (EmailSendRecord record : this.emailSentRecords) {
            if (messageId.equalsIgnoreCase(record.getMessageId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public EmailSendRecord findEmailSentByEmailAndType (String email, String type) {

        for (EmailSendRecord record : this.emailSentRecords) {
            if (email.equalsIgnoreCase(record.getEmail()) && type.equalsIgnoreCase(record.getType())) {
                return record;
            }
        }

        return null;
    }

    @Override
    public List<EmailSendRecord> findEmailSentByEmail (String email) {

        return null;
    }
}
