package com.tmt.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Stream
 * 
 * @author lifeng
 */
public class TestStream {

	public static void main(String[] args) {

		String[] strings = { "Hello", "World" };

		// map 就是进行转换
		Arrays.asList(strings).stream() // Stream<String>
				.map(new Function<String, String[]>() {
					@Override
					public String[] apply(String t) {
						System.out.println(t);
						return t.split("");
					}
				})// Stream<String[]>
				.map(new Function<String[], Stream<String>>() {
					@Override
					public Stream<String> apply(String[] t) {
						System.out.println(t);
						return Arrays.stream(t);
					}
				})// Stream<Stream<String>>
				.collect(Collectors.toList());

		// flatmap -- 这个在这里的主要作用是对流进行扁平化
		List<String> results = Arrays.asList(strings).stream()// Stream<String>
				.map(s -> s.split("")) // Stream<String[]>
				.flatMap(str -> Arrays.stream(str))// Stream<String> -- map 对应的是 Stream<Stream<String>>
				.collect(Collectors.toList());
		System.out.println(results);
	}

	public List<Student> getStudents() {
		Student stuA = new Student(1, "A", "M", 184);
		Student stuB = new Student(2, "B", "G", 163);
		Student stuC = new Student(3, "C", "M", 175);
		Student stuD = new Student(4, "D", "G", 158);
		Student stuE = new Student(5, "E", "M", 170);
		List<Student> list = new ArrayList<>();
		list.add(stuA);
		list.add(stuB);
		list.add(stuC);
		list.add(stuD);
		list.add(stuE);
		return list;
	}

	@Test
	public void test() {
		// List<Student> students = this.getStudents();
		// Student student = students.stream().filter(t ->
		// t.sex.equals("G")).findAny().get();
		// System.out.println(student.name);
		//
		// Stream.iterate(1, item -> item + 2)
		// .limit(10)
		// .forEach(System.out::print);

		Stream.of("li", "feng").map(s -> s.split("")).flatMap(s -> Stream.of(s)).forEach(System.out::print);
		Stream.of(1, 2, 3).flatMap(q -> Stream.of(q * 10)).forEach(System.out::print);

		Optional<Integer> accResult = Stream.of(1, 2, 3, 4).reduce((acc, item) -> {
			System.out.println("acc : " + acc);
			acc += item;
			System.out.println("item: " + item);
			System.out.println("acc+ : " + acc);
			System.out.println("--------");
			return acc;
		});
		System.out.println("accResult: " + accResult.get());
		System.out.println("--------");
	}

	class Student {
		int no;
		String name;
		String sex;
		float height;

		public Student(int no, String name, String sex, float height) {
			this.no = no;
			this.name = name;
			this.sex = sex;
			this.height = height;
		}

		public int getNo() {
			return no;
		}

		public void setNo(int no) {
			this.no = no;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public float getHeight() {
			return height;
		}

		public void setHeight(float height) {
			this.height = height;
		}
	}
}