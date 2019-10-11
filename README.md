# ReadCSVFile

## Getting Started

The project is developed for the Android platform by using Kotlin as a programming language. 

### Intro
The app is developed using MVP architecture.<br/>
Room persistance library is used to save data in a local database. To fetch and update cached data we have used Coroutines to perform background operations.<br/>
Also, in order to read CSV file we have used Coroutines to perform background operations.<br/>

# Features:
##  Home screen:
- Shows a list of users. <br/>
- If there are no cached data we will read User data from CSV file otherwise we will show cached data.<br/>
- When we pull down on a list of users (Swipe-to-refresh gesture) we read CSV file using Coroutines without blocking UI thread.
## About Screen:
About screen is implemented using web view.
## Orientation
The application handles phone rotation (Portrait and Landscape).
##  Tests:
- Code has been tested using Unit Tests<br/>
- The Room database is tested via Instrumented Unit Tests

