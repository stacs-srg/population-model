---
layout: default
title: Running with Java 
markdown: kramdown
---

# Running Valipop with Java

## Dependencies

You will need the following installed on your system to run Valipop:

- [R 4.4.2 or greater](https://cran.r-project.org/)
- [Java 21 or greater](https://www.oracle.com/uk/java/)

You will also need to install the `geepack` R package, which can be done by running the following command:

```shell
# In a terminal

# Install the geepack R package on your system
R -e "install.packages('geepack', repos = c(CRAN = 'https://cloud.r-project.org'))"
```

## Installing the JAR file

Valipop can be run with Java using its JAR file. To install the latest valipop JAR file, you can visit [the releases page](https://github.com/Daniel5055/valipop/releases).

## Running Valipop

Run the following command to run Valipop:

```shell
java -jar valipop.jar
```

## Building from source

Instead of downloading the latest JAR file, you can also build it directly from source.

This will require the following dependencies installed:

- [Git](https://git-scm.com/)
- [Java 21](https://www.oracle.com/uk/java/)
- [Maven](https://maven.apache.org/)

Then you can follow these steps to build the JAR:

```shell
# Open a terminal

# Clone the repository
git clone https://github.com/Daniel5055/valipop

# Navigate to the project repository
cd valipop

# Installing dependencies, compiling, and packaging into JARs
mvn clean package -Dmaven.test.skip -Dmaven.repo.local=repository

# The build should be in `target/`, including the runnable JARs
```
