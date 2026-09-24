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
git clone https://github.com/the-commit-crew-leap/the_commit_crew.git
```
Linux:
```bash
git clone git@github.com:the-commit-crew-leap/the_commit_crew.git
```
2. Create .env.dev and .env.prod files in the root directory. They should look something like this:

.env.dev:
```bash
POSTGRES_DB=the-commit-crew-dev
POSTGRES_PASSWORD=<db password here>
```

.env.prod:
```bash
POSTGRES_DB=the-commit-crew-prod
POSTGRES_PASSWORD=<db password here>
```

3. To run both the app and local database together:

3.1 Run docker-compose:<br />
dev:
```bash 
docker-compose -p commitcrew-dev  --env-file .env.dev  up -d --build
```
prod:
```bash 
docker-compose -p commitcrew-prod --env-file .env.prod up -d --build
```

3.2 Verify: <br />
dev:
```bash
docker-compose -p commitcrew-dev  --env-file .env.dev  exec db psql -U postgres -d the-commit-crew-dev  -c "\dt"
```

prod:
```bash
docker-compose -p commitcrew-prod --env-file .env.prod exec db psql -U postgres -d the-commit-crew-prod -c "\dt"
```
You should see the list of tables for each database. The app will be accessible at http://localhost:8081 on your machine and http://your-machine-ip:8081 from other machines.

4. To run only the app and access a shared dev database:

4.1 Run this command (make sure to add the password from you .env file):
```bash
POSTGRES_HOST=10.9.75.153 POSTGRES_DB=the-commit-crew-dev POSTGRES_PASSWORD=<password> docker-compose -p commitcrew-dev up app --no-deps -d --build
```

## Coding Conventions

### Naming 
- Classes: PascalCase (e.g. `UserService`)
- Methods/variables: camelCase (e.g. `isActive`)
- Constants: UPPER_SNAKE_CASE (e.g. `MAX_RETRY_ATTEMPTS`)
- Tables: lower_snake_case (e.g. `accounts_example`)

### Code Style
- Indent: 1 tab
- Line length: max 120 characters
- One class per file

### Git Workflow & Branch Protection
- **Develop branch**: All pushes require a pull request with 1 team member approval
- **Main branch**: Merges require 2 team member approvals before integration
- Follow Git flow: features branch from `develop`, releases branch from `develop`, hotfixes branch from `main`


## Jenkins Pipeline available at
[10.9.75.153:8080](http://10.9.75.153:8080) (develop & feature/)<br />
[10.9.70.90:8080](http://10.9.70.90:8080/job/the-commit-crew-main/) (main)

## Jira backlog:

[The Commit Crew](https://thecommitcrew.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog)
