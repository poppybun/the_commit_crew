# The Commit Crew

## Branching strategy: Git flow
We decided to go with git flow branching strategy because we expect to have many features, scheduled releases, and it is safer than pushing straight to main. 


## Group Members:
- Giulia Fattori
- Myroslava Bunciuc
- Ian Gill
- Louise Deeth

## Running the application:

1. Clone the repository.
2. Run 'mvn clean package' to compile.
3. Create docker image: 'docker build -t the-commit-crew .'
4. Run the container: 'docker run -d -p 8081:8080 --name the-commit-crew the-commit-crew:latest'
5. Check the logs: 'docker logs <containerID>'