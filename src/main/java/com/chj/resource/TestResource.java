package com.chj.resource;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chj.persistence.domain.Demo1Entity;
import com.chj.persistence.domain.Demo2Entity;
import com.chj.persistence.repositories.Demo1EntityRepository;
import com.chj.persistence.repositories.Demo2EntityRepository;

@RestController
public class TestResource {
	
	@Autowired
	Demo1EntityRepository demo1EntityRepository;
	
	@Autowired
	Demo2EntityRepository demo2EntityRepository;

	@RequestMapping("/test1")
	public void test1(){
		Demo1Entity demo1 = new Demo1Entity();
		demo1.setName("demo1 - " + UUID.randomUUID().toString());
		demo1EntityRepository.save(demo1);
	}
	
	@RequestMapping("/test2")
	public void test2(){
		Demo2Entity demo2 = new Demo2Entity();
		demo2.setName("demo2 - " + UUID.randomUUID().toString());
		demo2EntityRepository.save(demo2);
	}
}
