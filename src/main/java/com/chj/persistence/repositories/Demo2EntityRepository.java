package com.chj.persistence.repositories;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chj.datasource.TargetDataSource;
import com.chj.persistence.domain.Demo2Entity;

@Repository
@TargetDataSource("db2")
public interface Demo2EntityRepository extends CrudRepository<Demo2Entity, Serializable> {

}
