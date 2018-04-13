package com.swak.common.service

import com.swak.common.persistence.BaseDao

open abstract class BaseService<T, PK> {

    /**
     * 在子类实现此函数,为下面的CRUD操作提供DAO.
     */
    internal abstract fun getBaseDao(): BaseDao<T, PK>


}