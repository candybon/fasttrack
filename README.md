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

##API
You can browse more information in the source code, but you have take a look at bellow for a galance

	/**
     * Used to insert some data in the database. Think of this as a {@link java.util.Map#put}
     *
     * Associates the specified {@link T} instance with the specified key.  If the data store previously contained
     * an identical {@link T} instance of the same type and key, then the insertion will fail with an exception.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @param data An class instance (object) that implements either
     * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData} to
     * be associated with the specified key. Fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as index for search function.
     *
     * @param indexes String var arg (similar to a String[]) that can be used to specify one(1) to six(6) indexes to
     * create for this object.
     * Note: if you specify these parameters, all fields annotated
     * as Searchable{@link com.chen.candybon.fasttrack.Searchable} will be ignored, and not to be stored as indexes.
     * Otherwise, if you did not specify any string variables as parameter, the fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as indexes instead.
     *
     * @return True if the insertion is successful, false if not.
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * invalid class type {@link com.chen.candybon.fasttrack.exception.DataClassException}
     * or anything wrong with data {@link com.chen.candybon.fasttrack.exception.DataException}
     */
    <T> boolean put(String key, T data, String... indexes) throws FastTrackException;

    /**
     * Used to find data from the database by using key. Think of this as a {@link java.util.Map#remove}
     *
     * Get the instance of {@link T} with the specified key.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @return The persisted object. Null if not found.
     *
     * @throws FastTrackException In case of an invalid key
     * {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or if anything else goes wrong, eg. De-serialization
     * {@link com.chen.candybon.fasttrack.exception.DataException}.
     * 
     */
    <T> T get(Class<T> tClass, String key) throws FastTrackException;

    /**
     * Removes the data for this key from this database if it is present. Think of this as a {@link java.util.Map#get}
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * or in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException}.
     */
    <T> void delete(Class<T> tClass, String key) throws FastTrackException;

    /**
     * Update the data for this key from this database if it is present. If not present in db,
     * it throws NotFoundException {@link com.chen.candybon.fasttrack.exception.NotFoundException}.
     * It is important to specify the indexes to use for this object, as the indexes will also be updated.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @param data An class instance (object) that implements either
     * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData} to
     * be associated with the specified key. Fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as index for search function.
     *
     * @param indexes String var arg (similar to a String[]) that can be used to specify one(1) to six(6) indexes to
     * create for this object.
     * Note: if you specify these parameters, all fields annotated
     * as Searchable{@link com.chen.candybon.fasttrack.Searchable} will be ignored, and not to be stored as indexes.
     * Otherwise, if you did not specify any string variables as parameter, the fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as indexes instead.
     *
     * @return True if the insertion is successful, false if not.
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or Object not found {@link com.chen.candybon.fasttrack.exception.NotFoundException},
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.

     */
    <T> boolean update(String key, T data, String... indexes)
            throws FastTrackException;

    /**
     * Search for the data of the specified Class, which satisfy the Criteria specified in QueryBuilder. The result is
     * a collection of a maximum number (default is 20) of records. The paging information can also be specified in
     * QueryBuilder to specify different result sets.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param predicate Define a set of conditions that the elements of the response have in common,
     * which specify the criteria that should be satisfied in search. e.g. indexes combination, maximum number
     * of record per result set, starting position of the result.
     *
     * @return The a maximum number (default is 20) of result set according to QueryBuilder
     *
     * @throws FastTrackException In case of an
     * class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * bad queryStatement {@link com.chen.candybon.fasttrack.exception.InvalidPredicateException}
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.
     */
    <T> List<T> find(Class<T> tClass, Predicate predicate)
            throws FastTrackException;

    /**
     * Search for the a maximum number of {@link T} data from a specific position.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     * @param startingPosition Starting position.
     *
     * @param max Max number of results.
     *
     * @return A {@link List} of T with a maximum size of max.
     *
     * @throws FastTrackException In case of an
     * class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.
     */
    <T> List<T> find(Class<T> tClass, int startingPosition, int max) throws FastTrackException;


