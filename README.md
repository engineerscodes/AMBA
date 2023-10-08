# AMBA
<p align="left">
1) How to set Content Type for Individualform data : https://stackoverflow.com/questions/21329426/spring-mvc-multipart-request-with-json
Fix for :  UNSUPPORTEDMEDIATYPEEXCEPTION: CONTENT TYPE 'APPLICATION/OCTET-STREAM' NOT SUPPORTED FOR (List<Options> options)


</p>

## Learning  
1 ) https://www.toptal.com/spring/spring-security-tutorial
2 ) https://github.com/jwtk/jjwt#jwt-create
3 ) https://github.com/jwtk/jjwt#quickstart
4 ) Auto incremented number generation for a Non-Primary key column

```java
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_number", unique = true, nullable = false, insertable = false, updatable = false)
  // JUST  @GeneratedValue(strategy = GenerationType.IDENTITY) doesn't work
```

