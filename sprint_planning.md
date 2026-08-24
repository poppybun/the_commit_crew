Sorint goal: create a new "Insights" tab
Value: users will be able to see how they spend their money and analyse that data more easily.

- insights overview needs to be on the page as soon as the app is opened
- transactions need to be broken down into categories
- user can go into each category to see transactions specific to that category
- two pie charts: current month and last month, with category labels
-- user can click on a category on either of the pie charts to see the transactions for that month
- if no category on a transaction: misc

Story1:
Add a charting library. All other stories depend on it.

Acceptance criteria:
- as a dev i want to have a charting library imported into the app and available to use

Estimate: 1

Story2:
As a user I want to see two pie charts on my homescreen.

Acceptance criteria:
- User can see two pie charts as soon as they open the app.
- If no spending in past month (new user), the last month's pie chart shows 0.
- The pie chart is divided into the relevant categories.
- The pie chart gets updated when a new transaction is added.

Estimate: 5

Story3:
As a user I want to drill into each category.

Acceptance criteria:
- user can click on a category on either pie chart and see the transactions specific to that category for that month.
- there is information about each transaction (date, time, amount) next to it. (assuming that data is also already available)

Estimate: 3

Story4:
As a user I can see the last 20 transactions.

Acceptance criteria:
- when user clicks on a category, only the last 20 transactions are listed.
- user can click on "next page" to see the next 20 transactions and so on.
- user can click on "previous page" to view the previous 20 transactions.

Estimate: 2

Story5:
As a user I want to be able to filter through my transactions by date and/or amount.

Acceptance criteria:

- a filter button available in ui.
- user can filter by date and amount.
- the list of transactions gets filtered correctly.

Estimate: 2

Total: 13 points = 26 days