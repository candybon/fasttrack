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
	


