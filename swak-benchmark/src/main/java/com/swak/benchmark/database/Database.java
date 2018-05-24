package com.swak.benchmark.database;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.swak.common.persistence.incrementer.IdGen;
import com.tmt.database.DataSourceProvider;
import com.tmt.database.datasource.HikariDataSourceProvider;
import com.tmt.database.ops.JdbcOps;
import com.tmt.database.ops.MybatisOps;

@Warmup(iterations = 3) // 预热的迭代次数
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 10, time = -1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
@Threads(10)
@Fork(1)
public class Database {
	
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		volatile DataSource dataSource = null;
		volatile MybatisOps mybatisOps = null;
		volatile JdbcOps jops = null;
		public BenchmarkState() {
			DataSourceProvider hikariDataSourceProvider = new HikariDataSourceProvider();
			IdGen.setServerSn("server-1-1");
			dataSource = hikariDataSourceProvider.getDataSource();
			mybatisOps = new MybatisOps(dataSource);
			jops = new JdbcOps(dataSource);
		}
	}
	
    @Benchmark
    public void mybatis_query(BenchmarkState state) {
    	state.mybatisOps.query();
    }
    
    @Benchmark
    public void mybatis_insert_InnoBDB(BenchmarkState state) {
    	state.mybatisOps.insert1();
    }
    
    @Benchmark
    public void mybatis_insert_InnoBDB_no_primary_key(BenchmarkState state) {
    	state.mybatisOps.insert2();
    }
    
    @Benchmark
    public void mybatis_insert_MYISAM(BenchmarkState state) {
    	state.mybatisOps.insert3();
    }
    
    @Benchmark
    public void jdbc_query(BenchmarkState state) {
    	state.jops.query();
    }
    
    @Benchmark
    public void jdbc_insert_InnoBDB(BenchmarkState state) {
    	state.jops.insert1();
    }
    
    @Benchmark
    public void jdbc_insert_InnoBDB_no_primary_key(BenchmarkState state) {
    	state.jops.insert2();
    }
    
    @Benchmark
    public void jdbc_insert_MYISAM(BenchmarkState state) {
    	state.jops.insert3();
    }

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(Database.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
