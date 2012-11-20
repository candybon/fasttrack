fasttrack
=========

A Java Library that allows persisting, fast tracking any Java Class Instance in a single generic database table.

=========
## Objective

A typical JavaEE backend design is to use a Object-Relational-Mapping tool to have mapping from a POJO to a database table. For example, when a designer create a Java Class named Company or Employee, and he/she will probably create two database tables respectively for them. 

Such design routine requires a lot of time and effort on the development process, and it is tedious to maintain.

Is there any way that allows designers to design Java Class in application layer, and do not need to consider the persistence layer? In other words, is it possible to have a clever persisten layer can take care of all the CRUD operations for any java instance regardless what class the instance is? 

Now this Library is built for providing this solution. To be more specific, what this Library trys to achieve is, when a designer designed a few Java Classes, ie Company and Employee, he/she does not need to create any database table for them, and all the CRUD operations will be taken cared.

## Example
	/******************** 1. Store java instances *************************/
    Company google = new Company("Google");
    ftService.put("company_id1", google, "name_google" , "seattle");
	
	Employee data = new Employee("Steve Jobs");
	ftService.put("employee_id1", data, "Steve" "Jobs", "32", "male");
	
	Employee data2 = new Employee("Gordon Svensson");
	ftService.put("employee_id2", data2, "Gordon", "Svensson", "33", "male");

	/******************** 2. Get data by id ********************************/
	Employee steve = ftService.get(Employee, "employee_id1");

	/******************** 3. Get data by condition *************************/
	PredicateBuilder builder = new PredicateBuilder();
	//search male employees with age 33
	builder.addAND(Index.index3, "33").addAND(Index.index4, "male"); 
	Predicate predicate = builder.toPredicate();
	List<Employee> = ftService.find(Employee, predicate);

Details on how to work with this library will be shown bellow.
	
## Building & Testing the library

The library is organized in Maven, you need to have Maven to build.
And two database engine were provided for the test cases, MySQL and HsqlDB

1. Build & Test with HsqlDB
	mvn clean install -Dmaven.test.jvmargs="-Dtargetdb=hsqldb"
	
2. Build & Test with MySQL
	2.1 create MySQL user: CREATE USER 'fasttrack_user'@'localhost' IDENTIFIED BY '';
	2.2 create test database: create database fasttrack_db;
	2.3 GRANT ALL PRIVILEGES ON fasttrack_db.* TO 'fasttrack_user'@'localhost' WITH GRANT OPTION;
	2.4 Build: mvn clean install

## Using the library
	# 1. Include Project
	You can include Fasttrack into your maven project as bellow
	<dependency>
        <groupId>com.chen.candybon</groupId>
        <artifactId>fasttrack</artifactId>
        <version>1.0</version>
        <type>ejb</type>
    </dependency>
	
	# 2. Design Java Class
	You can now create Java Class that you would like to persist, you do that by extending SmallData or LargeData in the library. The choice of one or the other is dependent on the size of the data.  The fast track data is serialized as such, the users must select the proper type (Small or Large) as the Small is limited to roughly 8Kb.
	Note: At this point the API does not do a pre-check of the data before trying to persist it.
	
	public class Company extends SmallData {
		String name;
		String registrationNo;
		String year;
		......
	}
	
	public class MediaFile extends LargeData {
		String filename;
		String location;
		String type;
		String content;
		......
	}
	
	in your code:
	
	@EJB()
    FastTrackDataService ftService;
	
	Company google = new Company();
	Company facebook = new Company();
	//setting company attributes....
	
	ftService.put(google.getNO() + "", google, google.getYear(), google.getName());
	ftService.put(facebook.getNO() + "", facebook, facebook.getYear(), facebook.getName());
	
	MediaFile file1 = new MediaFile();
	MediaFile file2 = new MediaFile();
	//setting file attributes...
	
	ftService.put("file1_key", file1, file1.getlocation(), file1.getType(), file1.getFilename());
	ftService.put("file2_key", file2, file2.getlocation(), file2.getType(), file2.getFilename());
	
	//Find me companies that created in 1995
	PredicateBuilder builder = new PredicateBuilder();
	builder.addAND(Index.index1, "1995"); 
	Predicate predicate = builder.toPredicate();
	List<Company> companies = ftService.find(Company, predicate);
	
	//Find me the company with registration 123456
	ftService.get(Company, "123456"); //because registration was made the primary key as shown above
	
	//Find me all mp3 files
	PredicateBuilder builder = new PredicateBuilder();
	builder.addAND(Index.index2, "mp3"); //because "type" is specified in the Index2 location.
	Predicate predicate = builder.toPredicate();
	List<MediaFile> files = ftService.find(MediaFile, predicate);
	
	//Advanced Feature, you can define what field would you like to index when designing a POJO by using annotations
	public class Employee extends SmallData {
		String employeeNo;
	
		@Searchable(Index.index1)
		String firstname;
		
		@Searchable(Index.index2)
		String lastname;
		
		@Searchable(Index.index3)
		int age;
		
		@Searchable(Index.index4)
		String gender;
		......
	}
	
	ftService.put(employee.getEmployeeNo(), employee);
	//find me all employee that is 35 years old.
	PredicateBuilder builder = new PredicateBuilder();
	builder.addAND(Index.index3, "35");
	Predicate predicate = builder.toPredicate();
	List<Employee> employees = ftService.find(Employee, predicate);
	
##Some notes

Be ware when doing a search using a predicate builder that the built query will not return too many instances of a specific object.  The larger the data set to return the slower the query.  Also doing a search on an index with a very low distribution of values will be expensive.

The first condition for PredicateBuilder should be AND. (Future improvement)

There are maximum 6 indexes that you can specify for the instance to be handled by this library (enough for most of the application)




