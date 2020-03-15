package com.diluv.api.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.diluv.api.utils.FileReader;
import com.diluv.confluencia.database.dao.SecurityDAO;
import com.diluv.confluencia.database.record.CompromisedPasswordRecord;
import com.diluv.confluencia.database.record.EmailSendRecord;

public class SecurityTestDatabase implements SecurityDAO {

    private final String[] emailRecords;
    private final String[] domainRecords;
    private final List<EmailSendRecord> emailSentRecords;

    public SecurityTestDatabase () {

        this.emailRecords = FileReader.readJsonFile("email/blacklistEmail", String[].class);
        this.domainRecords = FileReader.readJsonFile("email/blacklistDomain", String[].class);
        this.emailSentRecords = FileReader.readJsonFolder("email_sent", EmailSendRecord.class);
    }

    @Override
    public boolean insertDomainBlacklist (String[] domains) {

        for (final String domain : domains) {
            for (final String record : this.domainRecords) {
                if (domain.equalsIgnoreCase(record)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean existsBlacklist (String email, String domain) {

        for (final String record : this.emailRecords) {
            if (email.equalsIgnoreCase(record)) {
                return true;
            }
        }

        for (final String record : this.domainRecords) {
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

        for (final EmailSendRecord record : this.emailSentRecords) {
            if (messageId.equalsIgnoreCase(record.getMessageId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public EmailSendRecord findEmailSentByEmailAndType (String email, String type) {

        for (final EmailSendRecord record : this.emailSentRecords) {
            if (email.equalsIgnoreCase(record.getEmail()) && type.equalsIgnoreCase(record.getType())) {
                return record;
            }
        }

        return null;
    }

    @Override
    public List<EmailSendRecord> findEmailSentByEmail (String email) {

        return new ArrayList<>();
    }

    @Override
    public boolean insertPassword (Map<String, Long> hashOccurrences) {

        return false;
    }

    @Override
    public CompromisedPasswordRecord findOnePasswordByHash (String hash) {

        return null;
    }
}
