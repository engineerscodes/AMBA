# AMBA
<p align="left">
1) How to set Content Type for Individualform data : https://stackoverflow.com/questions/21329426/spring-mvc-multipart-request-with-json
Fix for :  UNSUPPORTEDMEDIATYPEEXCEPTION: CONTENT TYPE 'APPLICATION/OCTET-STREAM' NOT SUPPORTED FOR (List<Options> options)


</p>

```roomsql
create table question (
        questionid uuid not null,
        answer_index bigint not null,
        options jsonb not null,
        question_img oid not null,
        question_number SERIAL not null,
        question_text varchar(255),
        fk_project_uuid uuid,
        primary key (questionid)
    )
```

## Learning  
1 ) https://www.toptal.com/spring/spring-security-tutorial <br>
2 ) https://github.com/jwtk/jjwt#jwt-create <br>
3 ) https://github.com/jwtk/jjwt#quickstart <br>
4 ) Auto incremented number generation for a Non-Primary key column <br>
5 ) Query by Example : https://github.com/spring-projects/spring-data-commons/blob/main/src/main/asciidoc/query-by-example.adoc  <br>

```java
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_number", unique = true, nullable = false, insertable = false, updatable = false)
  // JUST  @GeneratedValue(strategy = GenerationType.IDENTITY) doesn't work
```

# SETUP 
1) Install PostgreSQL Locally
2) Configure Username and Password in properties file (spring) and also ENV VARIABLE DB_PASSWORD ;PRIVATE_KEY
3) Disable Auth because Login and Logout is pending in Frontend 
```java 
  // to disable replace auth.requestMatchers("/auth/**") to 
  auth.requestMatchers("**")
```
3) Create Type (eg : MCQ) in USING admin http://localhost:4200/admin
4) Create Project using admin page 
5) Add question using http://localhost:4200/admin/questions


# Don't Turn on spring.threads.virtual.enabled=true
![image](https://github.com/user-attachments/assets/254ed6a5-2a1a-4338-9eb8-5c6fa8dfae27)


# DOCKER IMG
1) docker-compose -f docker-compose.yaml up --build

