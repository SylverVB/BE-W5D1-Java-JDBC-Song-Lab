# About JDBC.

# JDBC Walkthrough

JDBC stands for Java DataBase Connnectivity. It is utilized to connect our java code with a database. JDBC will allow us to execute SQL statements from java and retrieve the result set of that query to be utilized in java.

## JDBC classes to know
### **Connection**: Creates an active connection to a database
Example:
```java
Connection connection = DriverManager.getConnection(url,username,password);

//for this lab, we created a ConnectionUtil Singleton class so we do not have to worry about the credentials to the database.
//The connection statement will look more like this for this lab
Connection connection = ConnectionUtil.getConnection();
```

### **Statement**: An object that represents an SQL statement to be executed
Example:
```java
String sqlStatement = "SELECT * FROM users WHERE username = 'user123' AND password = 'pass123'"

Statement statement = connection.createStatement();
```

### Executing the SQL Statement 
How we execute the the statement object is different based on what sublanguage of sql we are working with. For example if we are trying to execute a DML statement, then there is only one more method we have to call

```java
statement.executeUpdate(sql);
```

On the other hand, if the sql statement is a dql operation, we will call the method `executeQuery(sql);` and store the value in a ResultSet object that we can loop through

```java
ResultSet rs = statement.executeQuery(sql);
```
### **ResultSet**: object that will store your data from the query that you executed. This will only be needed for DQL operations (as shown above).

## DQL Example

Retrieving all records from table users (DQL EXAMPLE):
```java
public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        try {
            //retrieve active connection to db
            Connection connection = ConnectionUtil.getConnection();
 
            String sql = "select * from users;";

            //create the Statement object
            Statement statement = connection.createStatement();

            //execute the statement and retrieve result set
            ResultSet rs = statement.executeQuery(sql);

            //loop through each record and add record to user list object
            while(rs.next()){
                users.add(new User(rs.getString(1), rs.getString(2)));
            }
            //NOTE: in the above statement, rs.getString(1) is retrieving column 1 as a string from a record. in our situation, that will get the firstname. Since lastname is in the second column in the table, we retrieve that value by using rs.getString(2);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
```

## DML Example
Inserting a Record into Table users (DML EXAMPLE):
```java
public void createUser(User user) {
        try {
            //retrieve active connection to db
            Connection connection = ConnectionUtil.getConnection();

            //SQL statement we are going to execute
            String sql = "insert into users (firstname, lastname) values ('" + user.getFirstname() + "', '" + user.getLastname() + "');";

            //create Statement object
            Statement statement = connection.createStatement();

            //execute the statement to db
            statement.executeUpdate(sql);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```

### Simple And Prepared Statements

### Examples of `execute()`, `executeUpdate()`, and `executeQuery()` for `Statement` and `PreparedStatement`

Below are examples for each method with use cases and explanations.

---

### **1. `execute()`**
- Used for **any kind of SQL statement** (DDL, DML, DQL).
- Returns a **boolean**:
  - `true` if the result is a `ResultSet` (for a `SELECT` query).
  - `false` if the result is an update count (e.g., for `INSERT`, `UPDATE`, or `DELETE`).

#### **Example 1: DDL Statement (Creating a Table)**
```java
Statement statement = connection.createStatement();
String createTableSQL = "CREATE TABLE members (id SERIAL PRIMARY KEY, name VARCHAR(50), age INT)";
boolean result = statement.execute(createTableSQL);

if (!result) {
    System.out.println("Table created successfully.");
} else {
    System.out.println("Unexpected result.");
}
```

#### **Example 2: DQL Statement (Retrieving Data)**
```java
Statement statement = connection.createStatement();
String selectSQL = "SELECT * FROM members";
boolean result = statement.execute(selectSQL);

if (result) {
    ResultSet resultSet = statement.getResultSet();
    while (resultSet.next()) {
        System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
    }
}
```

---

### **2. `executeUpdate()`**
- Used for **DML statements** (`INSERT`, `UPDATE`, `DELETE`).
- Returns an **int** representing the number of rows affected.

#### **Example 1: INSERT Statement**
```java
PreparedStatement preparedStatement = connection.prepareStatement(
    "INSERT INTO members (name, age) VALUES (?, ?)"
);
preparedStatement.setString(1, "John Doe");
preparedStatement.setInt(2, 30);

int rowsInserted = preparedStatement.executeUpdate();
System.out.println(rowsInserted + " row(s) inserted.");
```

#### **Example 2: UPDATE Statement**
```java
PreparedStatement preparedStatement = connection.prepareStatement(
    "UPDATE members SET age = ? WHERE name = ?"
);
preparedStatement.setInt(1, 35);
preparedStatement.setString(2, "John Doe");

int rowsUpdated = preparedStatement.executeUpdate();
System.out.println(rowsUpdated + " row(s) updated.");
```

#### **Example 3: DELETE Statement**
```java
PreparedStatement preparedStatement = connection.prepareStatement(
    "DELETE FROM members WHERE name = ?"
);
preparedStatement.setString(1, "John Doe");

int rowsDeleted = preparedStatement.executeUpdate();
System.out.println(rowsDeleted + " row(s) deleted.");
```

---

### **3. `executeQuery()`**
- Used specifically for **DQL statements** (`SELECT`).
- Returns a **`ResultSet`** containing the data retrieved from the database.

#### **Example 1: Retrieving All Rows**
```java
PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM members");
ResultSet resultSet = preparedStatement.executeQuery();

while (resultSet.next()) {
    System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
}
```

#### **Example 2: Retrieving Rows with a Condition**
```java
PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM members WHERE age > ?");
preparedStatement.setInt(1, 25);

ResultSet resultSet = preparedStatement.executeQuery();
while (resultSet.next()) {
    System.out.println("Name: " + resultSet.getString("name") + ", Age: " + resultSet.getInt("age"));
}
```

---

### **When to Use Each Method**

| Method            | SQL Type       | Returns        | Use Case Example                          |
|--------------------|----------------|----------------|-------------------------------------------|
| `.execute()`       | Any (DDL/DML)  | `boolean`      | Creating a table or checking query type.  |
| `.executeUpdate()` | DML            | `int`          | Insert, update, or delete rows.           |
| `.executeQuery()`  | DQL            | `ResultSet`    | Retrieve data using `SELECT`.             |

These methods provide flexibility depending on the type of SQL query you need to execute.


## Callable Statement

A **CallableStatement** is used in Java to execute **stored procedures** in a database. Here are five examples illustrating different use cases for `CallableStatement`. 

---

### **1. Calling a Simple Stored Procedure Without Parameters**
#### **Stored Procedure**:
```sql
CREATE PROCEDURE GetAllMembers()
BEGIN
    SELECT * FROM members;
END;
```

#### **Java Code**:
```java
CallableStatement callableStatement = connection.prepareCall("{CALL GetAllMembers()}");
ResultSet resultSet = callableStatement.executeQuery();

while (resultSet.next()) {
    System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
}
```

---

### **2. Calling a Stored Procedure with Input Parameters**
#### **Stored Procedure**:
```sql
CREATE PROCEDURE GetMemberById(IN memberId INT)
BEGIN
    SELECT * FROM members WHERE id = memberId;
END;
```

#### **Java Code**:
```java
CallableStatement callableStatement = connection.prepareCall("{CALL GetMemberById(?)}");
callableStatement.setInt(1, 1); // Setting input parameter (ID)

ResultSet resultSet = callableStatement.executeQuery();
if (resultSet.next()) {
    System.out.println("Name: " + resultSet.getString("name"));
}
```

---

### **3. Calling a Stored Procedure with Output Parameters**
#### **Stored Procedure**:
```sql
CREATE PROCEDURE GetMemberNameById(IN memberId INT, OUT memberName VARCHAR(50))
BEGIN
    SELECT name INTO memberName FROM members WHERE id = memberId;
END;
```

#### **Java Code**:
```java
CallableStatement callableStatement = connection.prepareCall("{CALL GetMemberNameById(?, ?)}");
callableStatement.setInt(1, 1); // Input parameter (ID)
callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR); // Output parameter

callableStatement.execute();
String memberName = callableStatement.getString(2); // Retrieve output parameter
System.out.println("Member Name: " + memberName);
```

---

### **4. Calling a Stored Procedure with Input and Output Parameters**
#### **Stored Procedure**:
```sql
CREATE PROCEDURE UpdateMemberAge(IN memberId INT, IN newAge INT, OUT successMessage VARCHAR(100))
BEGIN
    UPDATE members SET age = newAge WHERE id = memberId;
    SET successMessage = 'Update Successful';
END;
```

#### **Java Code**:
```java
CallableStatement callableStatement = connection.prepareCall("{CALL UpdateMemberAge(?, ?, ?)}");
callableStatement.setInt(1, 1); // Input parameter (ID)
callableStatement.setInt(2, 30); // Input parameter (new age)
callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR); // Output parameter

callableStatement.execute();
String message = callableStatement.getString(3); // Retrieve output parameter
System.out.println("Message: " + message);
```

---

### **5. Calling a Stored Procedure That Returns Multiple Result Sets**
#### **Stored Procedure**:
```sql
CREATE PROCEDURE GetMemberDetailsAndCount()
BEGIN
    SELECT * FROM members; -- First result set
    SELECT COUNT(*) AS TotalMembers FROM members; -- Second result set
END;
```

#### **Java Code**:
```java
CallableStatement callableStatement = connection.prepareCall("{CALL GetMemberDetailsAndCount()}");
boolean hasResults = callableStatement.execute();

do {
    if (hasResults) {
        ResultSet resultSet = callableStatement.getResultSet();
        while (resultSet.next()) {
            if (resultSet.getMetaData().getColumnName(1).equals("TotalMembers")) {
                System.out.println("Total Members: " + resultSet.getInt("TotalMembers"));
            } else {
                System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
            }
        }
    }
    hasResults = callableStatement.getMoreResults();
} while (hasResults);
```

---

### **When to Use CallableStatement**
CallableStatement is used when working with stored procedures for:
1. **Abstracting complex queries** into reusable procedures.
2. Handling **input/output parameters** easily.
3. Reducing **repeated SQL code** in your application logic.
4. Supporting **database logic centralization** for enterprise applications.
