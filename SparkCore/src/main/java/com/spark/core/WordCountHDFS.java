package com.spark.core;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCountHDFS {

	public static void main(String[] args) {
		args = new String[3];
		args[0] = "/user/huser/wordcountHdfs/input-data/wordcount.txt";
		args[1] = "/user/huser/wordcountHdfs/output";
		args[2] = "yarn-client";
		SparkConf sparkConf = new SparkConf().setAppName(
				"WordCount").setMaster(args[2]);
		
		sparkConf.set("spark.yarn.jars", "hdfs://localhost:9000/user/huser/wordcountHdfs/spark-uber.jar");
		//sparkConf.set("spark.yarn.jars", "/home/huser/spark-uber.jar");
		sparkConf.setSparkHome("/home/jrp/softwares/spark-2.1.0-bin-hadoop2.7");
		sparkConf.set("spark.driver.memory", "1000M");
		sparkConf.set("spark.driver.extraJavaOptions", "-XX:ReservedCodeCacheSize=100M -XX:MaxMetaspaceSize=256m -XX:CompressedClassSpaceSize=256m");
		sparkConf.set("spark.executor.extraJavaOptions", "-XX:ReservedCodeCacheSize=100M -XX:MaxMetaspaceSize=256m -XX:CompressedClassSpaceSize=256m");
		
		
		
		System.setProperty("HADOOP_USER_NAME", "huser");
		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		
		String input = "hdfs://localhost:9000"+args[0];
		//input file in HDFS
		JavaRDD<String> distFile = jsc
				.textFile(input);

		JavaRDD<String> flat_words = distFile
				.flatMap(new FlatMapFunction<String, String>() {
					public Iterator<String> call(String line) throws Exception {
						return Arrays.asList(line.split(" ")).iterator();
					}
				});

		JavaPairRDD<String, Long> flat_words_mapped = flat_words
				.mapToPair(new PairFunction<String, String, Long>() {
					public Tuple2<String, Long> call(String flat_word)
							throws Exception {
						return new Tuple2<String, Long>(flat_word, 1L);
					}
				});

		JavaPairRDD<String, Long> flat_words_reduced = flat_words_mapped
				.reduceByKey(new Function2<Long, Long, Long>() {
					public Long call(Long l1, Long l2) throws Exception {
						return l1 + l2;
					}
				});
		
		// Delete output Directory if exists
		// hadoop fs -rm -r /user/huser/wordcountHdfs/output
		
		// All spark job needs file:// or hdfs:// prefix to distinguish between
		// local and cluster
		
		//Store output in HDFS
		String outputDir = "hdfs://localhost:9000"+args[1];
		System.out.println("=======outputDir : " + outputDir);
		flat_words_reduced
				.saveAsTextFile(outputDir);

		jsc.close();
	}

}
