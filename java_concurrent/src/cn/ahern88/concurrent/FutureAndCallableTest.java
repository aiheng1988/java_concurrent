package cn.ahern88.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureAndCallableTest {

	public static void main(String[] args) throws Exception{
		long start = System.currentTimeMillis();
		// 申请十个长度的线程池
		ExecutorService executors = Executors.newFixedThreadPool(10);
		ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(108);
		for(int i = 1; i <= 108; i++) {
			queue.put(i);
			System.out.println("加入 " + i);
		}
		List<Execute> executes = new ArrayList<Execute>();
		while(!queue.isEmpty()) {
			final int value = queue.poll();
			executes.add(new Execute(){

				@Override
				public Long execute() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return (long) value * 2;
				}
				
			});
		}
		// 以十个十个的分组
		int i = 1;
		while((i-1) * 10 < executes.size()){
			List<Execute> subs = executes.subList((i - 1) * 10, Math.min(10 * i, executes.size()));
			List<Future<Long>> futures = new ArrayList<Future<Long>>();
			for(final Execute execute : subs){
				Future<Long> future = executors.submit(new Callable<Long>() {
		
					public Long call() throws Exception {
						System.out.println("开始执行...");
						return execute.execute();
					}
					
				});
				futures.add(future);
			}
			for(Future<Long> future : futures){
				System.out.println("得到：" + future.get());
			}
			i++;
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("耗时：" + time / 1000 + "s");
	}
	
	private static interface Execute {
		Long execute();
	}
	
}
