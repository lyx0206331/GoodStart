package com.adrian.goodstart.dao;

import android.database.sqlite.SQLiteDatabase;


import com.adrian.goodstart.pojo.Cmd;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by adrian on 16-3-20.
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cmdDaoConfig;

    private final CmdDao cmdDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);
        cmdDaoConfig = daoConfigMap.get(CmdDao.class).clone();
        cmdDaoConfig.initIdentityScope(type);

        cmdDao = new CmdDao(cmdDaoConfig, this);

        registerDao(Cmd.class, cmdDao);
    }

    public void clear() {
        cmdDaoConfig.getIdentityScope().clear();
    }

    public CmdDao getCmdDao() {
        return cmdDao;
    }
}
