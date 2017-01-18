# DISeASE
## by Giacomo Bergami and Alexander Pollok

This project provides an implementation of a subset of IBM Watson architecture from scratch. [Here](https://www.youtube.com/watch?v=3FWMLVSx58M)'s a short demo.

### 1. Compiling

In order to compile the code, you have to install maven in your system.
All the libraries are provided via maven repository and the "libraries" folder.

You could simply compile by running compile.sh

### 2. Running the code.

Download the compressed dataset from http://jackbergus.alwaysdata.net/disease/, and put it in the main project folder.

Next, unzip wholeData.zip in the current folder. 

Each time run the "run.sh" script for starting the software

## 3. Web Server

You could even run the code as a web server: change the run.sh main class to "disease.web.Server"
