# The Commit Crew

## Branching strategy: Git flow
We decided to go with git flow branching strategy because we expect to have many features, scheduled releases, and it is safer than pushing straight to main. 


## Group Members:
- Giulia Fattori
- Myroslava Bunciuc
- Ian Gill
- Louise Deeth

## Running the application:

1. Clone the repository:
Windows:
```bash
git clone https://github.com/poppybun/the_commit_crew.git
```
Linux:
```bash
git clone git@github.com:poppybun/the_commit_crew.git
```
2. Run:
```bash
mvn clean package
```
3. Create docker image:
```bash 
docker build -t the-commit-crew .
```
4. Run the container:
```bash
docker run -d -p 8081:8080 --name the-commit-crew the-commit-crew:latest
```
5. Check containder exists and note the id:
```bash
docker ps -a
```
6. Check the logs for the output: 
```bash
docker logs <containerID>
```


## Jenkins Pipeline available at
10.9.75.153:8080