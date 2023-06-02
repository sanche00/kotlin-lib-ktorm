package com.ssg.ic.sp.ktorm

import com.ssg.ic.sp.db.ENV_DB_DRIVER
import com.ssg.ic.sp.db.ENV_DB_WRITE_PWD
import com.ssg.ic.sp.db.ENV_DB_WRITE_URL
import com.ssg.ic.sp.db.ENV_DB_WRITE_USER
import com.ssg.ic.sp.ktorm.Departments.primaryKey
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
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
interface Facility : Entity<Facility>{
    companion object : Entity.Factory<Facility>()

    var cd: String
    var nm: String
    var level: Int
    var parent: String
    var alias_nm: String


}

object Facilities2 : Table<Facility>("facility") {

    val cd = varchar("cd").primaryKey().bindTo { it.cd }
    val nm = text("nm").bindTo { it.nm }
    val level = int("level").bindTo { it.level }
    val parent = varchar("parent").bindTo { it.parent }
    val alias_nm = varchar("alias_nm").bindTo { it.alias_nm }
}

class KtormTest2 {

    @Test
    fun dslSelectTest() {
        System.setProperty(ENV_DB_DRIVER, "org.postgresql.Driver")
        System.setProperty(
            ENV_DB_WRITE_URL,
            "jdbc:postgresql://sellpick-dev-main.cluster-c6bwy8w0ihhy.ap-northeast-2.rds.amazonaws.com:5432/sellpickdevSales"
        )
        System.setProperty(ENV_DB_WRITE_USER, "us_sellpick")
        System.setProperty(ENV_DB_WRITE_PWD, "us_sellpick")

        val hikariConfig = HikariConfig().apply {
            driverClassName = System.getProperty(ENV_DB_DRIVER)
            jdbcUrl = System.getProperty(ENV_DB_WRITE_URL)
            maximumPoolSize = 3
            username = System.getProperty(ENV_DB_WRITE_USER)
            password =  System.getProperty(ENV_DB_WRITE_PWD)
            isAutoCommit = false
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            validate()
        }
        val database = Database.connect(HikariDataSource(hikariConfig))

        val ret = database.from(Facilities).select()

        Assertions.assertTrue(ret.rowSet.next())
    }

    val Database.facilities get() = this.sequenceOf(Facilities2)

    @Test
    fun dslPoolTest() {
        System.setProperty(ENV_DB_DRIVER, "org.postgresql.Driver")
        System.setProperty(
            ENV_DB_WRITE_URL,
            "jdbc:postgresql://sellpick-dev-main.cluster-c6bwy8w0ihhy.ap-northeast-2.rds.amazonaws.com:5432/sellpickdevSales"
        )
        System.setProperty(ENV_DB_WRITE_USER, "us_sellpick")
        System.setProperty(ENV_DB_WRITE_PWD, "us_sellpick")

        val hikariConfig = HikariConfig().apply {
            driverClassName = System.getProperty(ENV_DB_DRIVER)
            jdbcUrl = System.getProperty(ENV_DB_WRITE_URL)
            maximumPoolSize = 3
            username = System.getProperty(ENV_DB_WRITE_USER)
            password =  System.getProperty(ENV_DB_WRITE_PWD)
            isAutoCommit = false
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            validate()
        }
        val database = Database.connect(HikariDataSource(hikariConfig))
        val ret = database.facilities.find { it.cd eq "0001" }
        if (ret != null) {
            println(ret.cd)
        }


        database.facilities.add(Facility{
            cd = "test"
            nm = "test"
            level = 1
            parent = "test"
            alias_nm = "test"
        })
//        val ret = database.from(Facilities).select()
//
//        Assertions.assertTrue(ret.rowSet.next())
//        ret.forEach {
//            println(it[Facilities.cd])
//        }
    }
}
