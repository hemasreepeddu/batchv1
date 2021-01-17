package com.emp.batchv1;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.emp.batchv1.model.Employee;

 
@Configuration
@EnableBatchProcessing
public class JobConfig {
     
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
    
    //read from CSV files
    @Value("input/inputData.csv")
    private Resource inputResource;
    
    //write in CSV files
    private Resource outputResource = new FileSystemResource("output/outputData.csv");
    
    //MultiResourceReader
   /*@Bean
    public MultiResourceItemReader<Employee> multiResourceItemReader() 
    {
        MultiResourceItemReader<Employee> resourceItemReader = new MultiResourceItemReader<Employee>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }*/
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FlatFileItemReader<Employee> reader() 
    {
    	System.out.println("job reader started");
        //Create reader instance
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
       //Set input file location
        reader.setResource(inputResource); 
        System.out.println(inputResource.toString());
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);   
         
        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName" });
                    }
                });
                //Set values in Employee class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {
                    {
                        setTargetType(Employee.class);
                    }
                });
            }
        });
        return reader;
        
    }
    
    //FlatFileItemWriter
    @Bean
    public FlatFileItemWriter<Employee> writer() 
    
    {
    	System.out.println("job writer started");
        //Create writer instance
        FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(outputResource);
         
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);
         
        //Name field values sequence based on object properties 
        writer.setLineAggregator(new DelimitedLineAggregator<Employee>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Employee>() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName" });
                    }
                });
            }
        });
        return writer;
    }
 
    @Bean
    public Step stepOne(){
    	System.out.println("step one started");
        return steps.get("stepOne").<Employee, Employee>chunk(5)
        		.reader(reader())
                .writer(writer())
                .build();
    }
     

     
    @Bean
    public Job demoJob(){
        return jobs.get("demoJob")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .build();
    }
}