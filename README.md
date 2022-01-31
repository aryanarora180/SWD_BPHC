# SWD BPHC
The SWD app for Android, rebuilt with a new design and using the best practices in Android Development.
Built entirely in Koltin.

## Features
- Buy goodies
- View your deductions and cancel orders
- Check mess menu, apply for mess graces, and register for mess
- View 212 bus timings
- Contact student representatives and officials
- Generate and download important documents
- Request outstations and receive notifications once approved
- Submit, edit, and delete MCN scholarship application
- Edit your SWD profile

## Tech stack & Open-source libraries
- Minimum SDK level 23
- MVVM Architecture
- ViewModels with LiveData and Coroutines for asynchronous tasks
- Hilt for dependency injection
- Retrofit and Moshi for API calls and JSON parsing
- Sealed classes to replace callbacks
- Material Components
- Coil to load images
- Kotlin extension functions
- Scooped storage
- Jetpack Compose

## Architecture
The application is based on MVVM architecture using a repository pattern. It uses Jetpack components such as ViewModels to hold UI related data while being lifecycle aware and LiveData to notify the domain layer about changes in data.
