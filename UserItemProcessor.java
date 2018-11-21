package com.scope.banking.processors;


import org.springframework.batch.item.ItemProcessor;

import com.scope.banking.models.BankUser;


public class UserItemProcessor implements ItemProcessor<BankUser, BankUser> {

 @Override
 public BankUser process(BankUser user) throws Exception {
	 BankUser test =new BankUser();
	 test.setId(user.getId());
	 test.setName(user.getName().toUpperCase());
	 System.out.println(user.getName());
  return test;
 }

}
