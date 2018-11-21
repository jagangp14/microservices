package com.scope.banking.configuration;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.scope.banking.models.BankUser;
import com.scope.banking.processors.UserItemProcessor;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

 @Autowired
 public JobBuilderFactory jobBuilderFactory;
 
 @Autowired
 public StepBuilderFactory stepBuilderFactory;
 
 @Autowired
 public DataSource dataSource;
 
 @Bean
 public DataSource dataSource() {
  final DriverManagerDataSource dataSource = new DriverManagerDataSource();
  dataSource.setDriverClassName("org.postgresql.Driver");
  dataSource.setUrl("jdbc:postgresql://localhost:5432/springbatch");
  dataSource.setUsername("postgres");
  dataSource.setPassword("vignesh");
  
  return dataSource;
 }
 
 @Bean
 public JdbcCursorItemReader<BankUser> reader(){
  JdbcCursorItemReader<BankUser> reader = new JdbcCursorItemReader<BankUser>();
  reader.setDataSource(dataSource);
  reader.setSql("SELECT id,name FROM bankuser");
  reader.setRowMapper(new UserRowMapper());
  
  return reader;
 }
 
 public class UserRowMapper implements RowMapper<BankUser>{

  @Override
  public BankUser mapRow(ResultSet rs, int rowNum) throws SQLException {
	  BankUser user = new BankUser();
   user.setId(rs.getInt("id"));
   user.setName(rs.getString("name"));
   System.out.println(user.getName());
   return user;
  }
  
 }
 
 @Bean
 public UserItemProcessor processor(){
	
  return new UserItemProcessor();
 }
 
 @Bean
 public FlatFileItemWriter<BankUser> writer(){
	 System.out.println("file searching...");
  FlatFileItemWriter<BankUser> writer = new FlatFileItemWriter<BankUser>();
  System.out.println("file searching...");
  writer.setResource(new FileSystemResource("bankuser.txt"));
  System.out.println("file found");
  writer.setLineAggregator(new DelimitedLineAggregator<BankUser>() {{
   setDelimiter(",");
   setFieldExtractor(new BeanWrapperFieldExtractor<BankUser>() {{
    setNames(new String[] { "id", "name" });
   }});
  }});
  
  return writer;
 }
 
 
 
 @Bean
 public Step step1() {
  return stepBuilderFactory.get("step1").<BankUser, BankUser> chunk(10)
    .reader(reader())
    .processor(processor())
    .writer(writer())
    .build();
 }
 
 @Bean
 public Job exportUserJob() {
  return jobBuilderFactory.get("exportUserJob")
    .incrementer(new RunIdIncrementer())
    .flow(step1())
    .end()
    .build();
 }
 
}
