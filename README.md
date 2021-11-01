# RDBMS

Database course project, Baylor University

## How to run

```shell
$ mvn clean package
$ java -jar target\\RDBMS-1.0.jar
```

## Sample queries

```
- select input.csv output.csv A B
- select input.csv output.csv A 1
- btree input.csv.A.btree

- project input.csv output.csv
- project input.csv output.csv A B C

- cross input.csv input2.csv output.csv
- join input.csv input2.csv output.csv
```
