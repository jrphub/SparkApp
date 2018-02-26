package com.spark.launcher;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

public class SparkYarnClient {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println(startTime);
        System.setProperty("HADOOP_USER_NAME", "huser");
        System.setProperty("SPARK_HOME", "/home/jrp/softwares/spark-2.1.0-bin-hadoop2.7");
        //
        //String slices = args[0];  // this is passed to SparkPi program 
        //
        String SPARK_HOME = System.getProperty("SPARK_HOME");
        //
        submit(SPARK_HOME, args); // ... the code being measured ... 
        //
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTime);
    }

    static void submit(String SPARK_HOME, String[] arguments) throws Exception {
        // 
        //String sparkExamplesJar = "file://" + SPARK_HOME + "/examples/jars/spark-examples_2.11-2.0.0.jar"; //local
        String applicationJar = "hdfs:///user/huser/wordcountHdfs/spark-core.jar"; // HDFS
        //
        final String[] args = new String[]{
            //"--name",
            //"WordCountHDFS",
            
            //
            //"--driver-memory",
            //"1000M",
            
            //
            "--jar",
            applicationJar,
            
            //
            "--class",
            "com.spark.core.WordCountHDFS",
            
            // argument 1 to my Spark program
            "--arg",
            "/user/huser/wordcountHdfs/input-data/wordcount.txt",
            
            "--arg",
            "/user/huser/wordcountHdfs/output",
            
            // argument 2 to my Spark program (helper argument to create a proper JavaSparkContext object)
            "--arg",
            "yarn-cluster"
            //"yarn"
        };

        Configuration config = new Configuration();
        //Configuration config = ConfigurationManager.createConfiguration();     
        //
        System.setProperty("SPARK_YARN_MODE", "true");
        //
        SparkConf sparkConf = new SparkConf();
        sparkConf.setSparkHome(SPARK_HOME);
        sparkConf.set("spark.yarn.jars", "hdfs:///user/huser/jars/spark-uber.jar");
        
        //sparkConf.set("spark.driver.host", "localhost");
        //sparkConf.set("spark.driver.port", "20002");

        //sparkConf.setMaster("yarn");
        //sparkConf.setMaster("yarn-cluster");
        
        //sparkConf.setAppName("spark-app");
        //sparkConf.set("master", "yarn");
        
        //sparkConf.set("spark.submit.deployMode", "cluster"); // worked
        //sparkConf.set("spark.submit.deployMode", "client");  // did not work
        
        //sparkConf.set("spark.yarn.am.port", "8088"); 
        //sparkConf.set("spark.driver.host", "localhost");
        sparkConf.set("spark.driver.host", "localhost");
        sparkConf.set("spark.driver.port", "8088");
        //sparkConf.set("spark.yarn.access.namenodes", "hdfs://localhost:9000");
        
        //
        //ClientArguments clientArguments = new ClientArguments(args, sparkConf);    // spark-1.6.1
        ClientArguments clientArguments = new ClientArguments(args);                 // spark-2.0.0
        Client client = new Client(clientArguments, config, sparkConf);
        //Client client = new Client(clientArguments, sparkConf);
        //
        client.run();
        // done!
    }
}