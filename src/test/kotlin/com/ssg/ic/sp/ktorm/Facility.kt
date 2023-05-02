package com.ssg.ic.sp.ktorm

import com.ssg.ic.sp.ktorm.Departments.primaryKey
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.forEach
import org.ktorm.dsl.from
import org.ktorm.dsl.isNotNull
import org.ktorm.dsl.select
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.varchar
//CREATE TABLE public.facility (
//cd bpchar(20) NOT NULL,
//nm text NOT NULL,
//"level" numeric(1) NOT NULL,
//parent bpchar(20) NULL DEFAULT NULL::bpchar,
//alias_nm bpchar(40) NULL,
//CONSTRAINT facility_pkey PRIMARY KEY (cd)
//);
object Facilities : Table<Nothing>("facility"){

    val cd = varchar("cd").primaryKey()
    val nm = text("nm")
    val level = int("level")
    val parent = varchar("parent")
    val alias_nm = varchar("alias_nm")
}

class KtormTest {

    @Test
    fun dslSelectTest() {

        val database = Database.connect(System.getProperty("WRITE_JDBC_URL"), driver = System.getProperty("DB_DRIVER"), user = System.getProperty("WRITE_DB_USER"), password = System.getProperty("WRITE_DB_PWD"))
        val ret = database.from(Facilities).select()

        Assertions.assertTrue(ret.rowSet.next())
    }

    @Test
    fun dslPoolTest() {

        val hikariConfig = HikariConfig().apply {
            driverClassName = System.getProperty("DB_DRIVER")
            jdbcUrl = System.getProperty("WRITE_JDBC_URL")
            maximumPoolSize = 3
            username = System.getProperty("WRITE_DB_USER")
            password =  System.getProperty("WRITE_DB_PWD")
            isAutoCommit = false
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            validate()
        }
        val database = Database.connect(HikariDataSource(hikariConfig))
        val ret = database.from(Facilities).select()

        Assertions.assertTrue(ret.rowSet.next())
        ret.forEach {
            println(it[Facilities.cd])
        }
    }
}
