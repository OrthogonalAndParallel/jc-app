package com.jc.config;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/**
 * @author jinchenj
 * @description
 * @create:2025-08-2116:42:21
 */
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {

    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select last_insert_rowid()";
    }

    @Override
    public String getIdentityColumnString(int type) {
        return "integer";
    }
}
