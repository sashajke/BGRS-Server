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

	private Map<Course,Vector<String>> studentsInCourse; // list of students for each course
	private Map<String,Vector<Course>> coursesOfStudent; // a list of courses for each student
	private Map<String,String> registeredUsers; // username and password for each user(Admin/Student)
	private Vector<String> admins; // a list of admins in the system
	private Vector<String> loggedIn;// a list of users that are currently logged in
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
		admins = new Vector<>();
		loggedIn = new Vector<>();
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
	public boolean initialize(String coursesFilePath) {
		try {
			File myObj = new File(coursesFilePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String courseLine = myReader.nextLine();
				if(courseLine.isEmpty())
					break;
				String[] courseData = new String[4];
				int j=0;
				String part = "";
				for(int i=0;i<courseLine.length();i++){
					if(courseLine.charAt(i)== '|'){
						courseData[j++] = part;
						part = "";
					}
					else
					{
						part+= courseLine.charAt(i);
					}
				}
				courseData[j] = part; // add the last part
				Integer courseNum = Integer.parseInt(courseData[0]);
				String courseName = courseData[1];
				// remove the [] from the string
				String kdams = courseData[2];
				List<Integer> kdamsOfCourse = new ArrayList<>();
				if(!kdams.equals("[]"))
				{
					kdams = courseData[2].substring(1,courseData[2].length()-1);
					String[] kdamsInArray = kdams.split(",");
					// build the list of kdams with Integers
					for(int i=0;i<kdamsInArray.length;i++){
						kdamsOfCourse.add(Integer.parseInt(kdamsInArray[i]));
					}
				}
				Integer numOfMaxStudents = Integer.parseInt(courseData[3]);
				Course toAdd = new Course(courseNum,courseName,kdamsOfCourse,numOfMaxStudents);
				coursesByOrder.add(courseNum);
				studentsInCourse.putIfAbsent(toAdd,new Vector<>());
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
	public  boolean adminRegister(String userName,String password)
	{

			// check if the username and password are valid
			if(userName != null && password != null){
				// check if there is already a user with that username
				if(!registeredUsers.containsKey(userName)){
					if(registeredUsers.putIfAbsent(userName, password) != null) // add the admin to the map of all the users
						return false;
					admins.add(userName); // add him to the admins list
					return true;
				}
			}
			return false;
	}
	// OPCODE - 2
	public  boolean studentRegister(String userName,String password){
			// check if the username and password are valid
			if(userName != null && password != null){
				// check if there is already a user with that username
				if(!registeredUsers.containsKey(userName)){
					if(registeredUsers.putIfAbsent(userName, password) != null) // add the student to the registered users
						return false;
					if(coursesOfStudent.putIfAbsent(userName,new Vector<>()) != null) // create new list of courses for the student
						return false;
					return true;
				}
			}
			return false;


	}
	// OPCODE - 3
	public  boolean Login(String userName,String password)
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
	public  Boolean Logout(String userName){
		// check if the user is logged in at the moment
		if(loggedIn.contains(userName))
		{
			loggedIn.remove(userName);
			return true;
		}
		return false;
	}
	// OPCODE - 5
	public  Boolean registerToCourse(String userName,short courseNum){
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
						coursesOfStudent.get(userName).add(course); // add the course to the list of the student's courses
						studentsInCourse.get(course).add(userName); // add the student to the list of students that are registered to this course
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
	private Course findCourse(short courseNum){
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
		if(courses != null){
			for(Course course : courses){
				res.add(course.getCourseNum());
			}
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

	/**
	 * A private function that sorts the courses by the order they were in the input file
	 * @param courses
	 * @return
	 */
	private List<Integer> sortCoursesByOrder(List<Integer> courses){
		Collections.sort(courses, new Comparator<Integer>() {
			@Override
			public int compare(Integer course1, Integer course2) {
				Integer course1Order = coursesByOrder.indexOf(course1);
				Integer course2Order = coursesByOrder.indexOf(course2);
				return course1Order.compareTo(course2Order);
			}
		});
		return courses;
	}
	// OPCODE - 6

	public  List<Integer> getKdamCourses(short courseNum,String userName){
		// if admin calls this function it should not work
		if(admins.contains(userName))
			return null;
		Course course = findCourse(courseNum);
		if(course == null)
			  return null;
		List<Integer> kdams = course.getKdamCoursesList();
		// sort the kdams according to the order of the courses in the input file
		kdams = sortCoursesByOrder(kdams);
		return kdams;
	}
	// OPCODE - 7
	public  CourseStat getCourseState(short courseNum,String nameOfAdmin){
		if(!admins.contains(nameOfAdmin))
			return null;
		String name = getCourseName(courseNum);
		List<String> registered = getRegisteredStudents(courseNum);
		if(registered != null)
			Collections.sort(registered); // sort alphabetically
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
	private String getCourseName(short courseNum){
		Course course  = findCourse(courseNum);
		// check if this course exists
		if(course != null)
			return course.getCourseName();
		return null;
	}
	private List<String> getRegisteredStudents(short courseNum){
		Course course  = findCourse(courseNum);
		// check if this course exists
		if(course != null)
			return studentsInCourse.get(course);

		return null;
	}
	private Integer getNumOfMaxSeats(short courseNum){
		Course course = findCourse(courseNum);
		// check if this course exists
		if(course!= null)
			return course.getNumOfMaxStudents();
		return null;
	}

	// OPCODE - 8
	public List<Integer> getRegisteredCoursesByOrderOfInput(String userName,String nameOfAdmin){
		if(!admins.contains(nameOfAdmin))
			return null;
		List<Integer> courses = getCoursesNumbers(coursesOfStudent.get(userName));
		courses = sortCoursesByOrder(courses);
		return courses;
	}
	// OPCODE - 9
	public Boolean isRegistered(String userName,short courseNum){
		Course course = findCourse(courseNum);
		// if the course exists
		return studentsInCourse.get(course).contains(userName);
	}
	// OPCODE - 10
	public  Boolean unregister(String userName,short courseNum){
		// admin can't register so he can't unregister as well
		if(admins.contains(userName))
			return false;
		Course course = findCourse(courseNum);
		Boolean deletedStudentFromCourse = false;
		Boolean deletedCourseFromStudentList = false;
		if(course != null){
			if(studentsInCourse.get(course) != null)
				 deletedStudentFromCourse = studentsInCourse.get(course).remove(userName);
			if(coursesOfStudent.get(userName) != null)
				deletedCourseFromStudentList = coursesOfStudent.get(userName).remove(course);
		}
		return deletedStudentFromCourse && deletedCourseFromStudentList;
	}

	// OPCODE - 11
	public List<Integer> getRegisteredCourses(String userName){
		// admin can't use MyCourses command
		if(admins.contains(userName))
			return null;
		return getCoursesNumbers(coursesOfStudent.get(userName));
	}
}
