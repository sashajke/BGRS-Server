package bgu.spl.net.srv;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

	private Map<Course,CopyOnWriteArrayList<String>> studentsInCourse; // list of students for each course
	private Map<String,CopyOnWriteArrayList<Course>> coursesOfStudent; // a list of courses for each student
	private Map<String,String> registeredUsers; // username and password for each user(Admin/Student)
	private List<String> admins; // a list of admins in the system
	private List<String> loggedIn;// a list of users that are currently logged in
	private List<Integer> coursesByOrder; // a list that is sorted according to the input, used to sort the list of courses we want to return when needed

	// Thread-safe singleton implementation
	private static class SingletonHolder{
		private static Database instance = new Database();
	}
	//to prevent user from creating new Database
	private Database() {
		studentsInCourse = new ConcurrentHashMap<>();
		coursesOfStudent = new ConcurrentHashMap<>();
		registeredUsers = new ConcurrentHashMap<>();
		admins = new CopyOnWriteArrayList<>();
		loggedIn = new CopyOnWriteArrayList<>();
		coursesByOrder = new ArrayList<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		try {
			File myObj = new File(coursesFilePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String courseLine = myReader.nextLine();
				String[] courseData = courseLine.split("|");
				Integer courseNum = Integer.parseInt(courseData[0]);
				String courseName = courseData[1];
				// remove the [] from the string
				String kdams = courseData[2].substring(1,courseData[2].length()-1);
				// build the list of kdams with Integers
				String[] kdamsInArray = kdams.split(",");
				List<Integer> kdamsOfCourse = new ArrayList<>();
				for(int i=0;i<kdamsInArray.length;i++){
					kdamsOfCourse.add(Integer.parseInt(kdamsInArray[i]));
				}
				Integer numOfMaxStudents = Integer.parseInt(courseData[3]);
				Course toAdd = new Course(courseNum,courseName,kdamsOfCourse,numOfMaxStudents);
				coursesByOrder.add(courseNum);
				studentsInCourse.putIfAbsent(toAdd,new CopyOnWriteArrayList<>());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	// OPCODE - 1
	public boolean adminRegister(String userName,String password)
	{
		// check if the username and password are valid
		if(userName != null && password != null){
			// check if there is already a user with that username
			if(!registeredUsers.containsKey(userName)){
				registeredUsers.putIfAbsent(userName, password); // add the admin to the map of all the users
				admins.add(userName); // add him to the admins list
				return true;
			}
		}
		return false;
	}
	// OPCODE - 2
	public boolean studentRegister(String userName,String password){
		// check if the username and password are valid
		if(userName != null && password != null){
			// check if there is already a user with that username
			if(!registeredUsers.containsKey(userName)){
				registeredUsers.putIfAbsent(userName, password); // add the student to the registered users
				coursesOfStudent.putIfAbsent(userName,new CopyOnWriteArrayList<>()); // create new list of courses for the student
				return true;
			}
		}
		return false;
	}
	// OPCODE - 3
	public boolean Login(String userName,String password)
	{
		// check if the user exists
		if(registeredUsers.containsKey(userName)){
			// check if the password is correct and if the user is not logged in already
			if(registeredUsers.get(userName).equals(password) && !loggedIn.contains(userName))
			{
				loggedIn.add(userName);
				return true;
			}
		}
		return false;
	}
	// OPCODE - 4
	public Boolean Logout(String userName){
		// check if the user is logged in at the moment
		if(loggedIn.contains(userName))
		{
			loggedIn.remove(userName);
			return true;
		}
		return false;
	}
	// OPCODE - 5
	public Boolean registerToCourse(String userName,Integer courseNum){
		// check if the user exists and that he is logged in and that he is not an admin
		if(loggedIn.contains(userName) && !admins.contains(userName)){
			Course course = findCourse(courseNum);
			// if we found a course that fits the course number provided
			if(course != null){
				List<Integer> studentsCourses = getCoursesNumbers(coursesOfStudent.get(userName));
				// if the student is not already registered to this course and he has all of the needed kdam courses
				if(!coursesOfStudent.get(userName).contains(course) && studentHasAllkdamsNeeded(course,studentsCourses)){
					Integer amountOfStudentsInCourse = studentsInCourse.get(course).size();
					// check if there is room for another student in the course
					if(amountOfStudentsInCourse < course.getNumOfMaxStudents()){
						coursesOfStudent.get(userName).addIfAbsent(course); // add the course to the list of the student's courses
						studentsInCourse.get(course).addIfAbsent(userName); // add the student to the list of students that are registered to this course
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * a private function that returns the course according to the courseNum
	 * @param courseNum
	 * @return
	 */
	private Course findCourse(Integer courseNum){
		Set<Course> Allcourses = studentsInCourse.keySet();
		for(Course course : Allcourses) {
			// find the course we want to register the student to
			if (course.getCourseNum() == courseNum)
				return course;
		}
		return null;
	}

	/**
	 * a private function that return a list of all the course numbers of courses
	 * @param courses
	 * @return
	 */
	private List<Integer> getCoursesNumbers(List<Course> courses){
		List<Integer> res = new ArrayList<>();
		for(Course course : courses){
			res.add(course.getCourseNum());
		}
		return res;
	}

	/**
	 * a private function that checks if a students has all the kdam courses that's needed to take a certain course
	 * @param course
	 * @param studentsCourses
	 * @return
	 */
	private Boolean studentHasAllkdamsNeeded(Course course, List<Integer> studentsCourses){
		List<Integer> kdamsNeeded = course.getKdamCoursesList();
		for(Integer courseNum : kdamsNeeded){
			if(!studentsCourses.contains(courseNum))
				return false;
		}
		return true;
	}

	public List<Integer> getKdamCourses(Integer courseNum){
		// TODO : implement
		// need to add the ordering of courses
		return null;
	}
	// OPCODE - 7
	public CourseStat getCourseState(Integer courseNum){
		String name = getCourseName(courseNum);
		List<String> registered = getRegisteredStudents(courseNum);
		Integer max = getNumOfMaxSeats(courseNum);
		// check that the courseNum is correct and we got the needed info
		if(name!=null && registered!= null && max!= null)
			return new CourseStat(max,name,registered);
		return null;
	}
	/**
	 * The following 3 function will be used to execute the COURSESTAT message
	 * each function will get a part of the needed information
	 */
	private String getCourseName(Integer courseNum){
		Course course  = findCourse(courseNum);
		// check if this course exists
		if(course != null)
			return course.getCourseName();
		return null;
	}
	private List<String> getRegisteredStudents(Integer courseNum){
		Course course  = findCourse(courseNum);
		// check if this course exists
		if(course != null)
			return studentsInCourse.get(course);

		return null;
	}
	private Integer getNumOfMaxSeats(Integer courseNum){
		Course course = findCourse(courseNum);
		// check if this course exists
		if(course!= null)
			return course.getNumOfMaxStudents();
		return null;
	}

	// OPCODE - 8
	public List<Integer> getRegisteredCoursesByOrderOfInput(String userName){
		// TODO : implement
		// need to think about how to implement the order of the list
		return null;
	}
	// OPCODE - 9
	public Boolean isRegistered(String userName,Integer courseNum){
		Course course = findCourse(courseNum);
		// if the course exists
		return studentsInCourse.get(course).contains(userName);
	}
	// OPCODE - 10
	public Boolean unregister(String userName,Integer courseNum){
		Course course = findCourse(courseNum);
		Boolean deletedStudentFromCourse = studentsInCourse.get(course).remove(userName);
		Boolean deletedCourseFromStudentList = coursesOfStudent.get(userName).remove(course);
		return deletedStudentFromCourse && deletedCourseFromStudentList;
	}

	// OPCODE - 11
	public List<Integer> getRegisteredCourses(String userName){
		return getCoursesNumbers(coursesOfStudent.get(userName));
	}






}
