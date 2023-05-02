package com.ssg.ic.sp.ktorm

import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

//CREATE TABLE public.facility (
//cd bpchar(20) NOT NULL,
//nm text NOT NULL,
//"level" numeric(1) NOT NULL,
//parent bpchar(20) NULL DEFAULT NULL::bpchar,
//alias_nm bpchar(40) NULL,
//CONSTRAINT facility_pkey PRIMARY KEY (cd)
//);
object Departments : Table<Nothing>("t_department") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val location = varchar("location")
}

fun main() {

    val database = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    database.from(Departments).select();
//    for (row in database.from(Employees).select()) {
//        println(row[Employees.name])
//    }
}