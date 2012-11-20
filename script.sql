USE ps2db;

SELECT * FROM Students, Courses;

SELECT name, title, semester FROM Students, Courses WHERE semester = 2;

SELECT name, sid, age FROM Students WHERE sid >= 125;

SELECT name, title, semester FROM Students, Courses WHERE (name >= John) AND (title < Operating) AND (semester = 1);
