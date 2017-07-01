package com.chj.persistence.repositories;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chj.datasource.TargetDataSource;
import com.chj.persistence.domain.Demo1Entity;

@Repository
@TargetDataSource
public interface Demo1EntityRepository extends CrudRepository<Demo1Entity, Serializable> {

}
